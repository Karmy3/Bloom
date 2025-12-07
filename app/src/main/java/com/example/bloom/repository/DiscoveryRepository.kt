package com.example.bloom.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.bloom.model.model.dao.DiscoveryDao
import com.example.bloom.ia.AIInterpretationEngine
import com.example.bloom.model.model.entities.Discovery
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoveryRepository @Inject constructor(
    private val discoveryDao: DiscoveryDao,
    private val aiEngine: AIInterpretationEngine,
    private val supabaseClient: SupabaseClient,
) {
    private val DISCOVERY_TABLE = "discoveries"
    private val STORAGE_BUCKET = "plant_images"

    //READ
    fun getAllDiscoveries(userId: String): Flow<List<Discovery>> {
        return discoveryDao.getAllDiscoveriesByUserId(userId)
    }

    fun searchDiscoveries(userId: String, query: String): Flow<List<Discovery>> {
        return discoveryDao.searchDiscoveries(userId, "%$query%")
    }

    //Fonction pour la synchronisation initiale (pull des données de Supabase)
    suspend fun initialSync() {
        try {
            val response = supabaseClient.from("discoveries").select().decodeList<Discovery>()
            response.forEach { discovery ->
                discoveryDao.insertDiscovery(discovery)
            }
        } catch (e: Exception) {
            println("Initial Supabase Pull Error: ${e.message}")
        }
    }

    //CRÉATION/MISE À JOUR
    suspend fun saveDiscoveryWithSync(
        newEntry: Discovery,
        imageUri: Uri,
        userId: String
    ): String = withContext(Dispatchers.IO) {

        val imageFile = File(newEntry.localImagePath)
        val fileExtension = imageFile.extension.ifEmpty { "jpg" }
        val storagePath = "$userId/${newEntry.id}.$fileExtension"
        var imageUrl = ""

        // 1. Upload de l'image
        try {
            supabaseClient.storage[STORAGE_BUCKET].upload(storagePath, imageFile.readBytes())
            imageUrl = supabaseClient.storage[STORAGE_BUCKET].publicUrl(storagePath)
        } catch (e: Exception) {
            println("Erreur d'upload Storage: ${e.message}. Sauvegarde locale uniquement.")
        }

        val finalEntry = newEntry.copy(imageUrl = imageUrl)
        discoveryDao.insertDiscovery(finalEntry)

        //3. Synchronisation Supabase BDD (Distant)
        try {
            supabaseClient.from(DISCOVERY_TABLE).upsert(finalEntry)
        } catch (e: Exception) {
            println("Erreur de synchronisation BDD: ${e.message}. Synchro distante échouée.")
        }

        return@withContext finalEntry.id
    }

    //SUPPRESSION
    suspend fun deleteDiscoveryAndSync(entry: Discovery) = withContext(Dispatchers.IO) {
        discoveryDao.deleteDiscoveryById(entry.id)

        try {
            supabaseClient.from(DISCOVERY_TABLE).delete { filter { eq("id", entry.id) } }

            val pathInStorage = if (entry.imageUrl.isNotEmpty()) {
                entry.imageUrl.substringAfterLast("plant_images/").substringBefore("?")
            } else null

            if (pathInStorage != null) {
                supabaseClient.storage[STORAGE_BUCKET].delete(listOf(pathInStorage))
            }
        } catch (e: Exception) {
            println("Erreur de suppression distante: ${e.message}.")
        }
    }

    //PARTAGE / IA
    suspend fun shareDiscovery(discovery: Discovery) {
        println("Partage de la découverte: ${discovery.name}")
    }

    suspend fun analyzeImage(bitmap: Bitmap): Pair<String, String> = withContext(Dispatchers.IO) {
        return@withContext aiEngine.identifyPlant(bitmap)
    }


    //REPOSITORY DANS LE SCREEN DE DETAILS

    suspend fun getDiscoveryById(id: String): Discovery? {
        return discoveryDao.getDiscoveryById(id)
    }

    // 2. CREATE / UPDATE
    suspend fun saveDiscovery(discovery: Discovery) {
        // Enregistre d'abord localement
        discoveryDao.insertDiscovery(discovery)

        // Synchronise avec Supabase (insère ou met à jour si l'ID existe)
        try {
            supabaseClient.from("discoveries").upsert(discovery)
        } catch (e: Exception) {
            println("Supabase Sync Error: ${e.message}")
            // Optionnel: Stocker l'ID pour une resynchronisation ultérieure
        }
    }

    // 3. DELETE
    suspend fun deleteDiscovery(id: String) {
        // Supprime localement
        discoveryDao.deleteDiscoveryById(id)

        // Supprime sur Supabase
        try {
            supabaseClient.from("discoveries").delete {
                filter { eq("id", id) }
            }
        } catch (e: Exception) {
            println("Supabase Delete Error: ${e.message}")
        }
    }


}