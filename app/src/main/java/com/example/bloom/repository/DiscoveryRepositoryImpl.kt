package com.example.bloom.repository
/*
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.bloom.ia.AIInterpretationEngine
import com.example.bloom.model.model.dao.DiscoveryDao
import com.example.bloom.model.model.entities.Discoveries
import com.example.bloom.model.model.entities.SupabaseDiscovery
import com.example.bloom.service.SupabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiscoveryRepositoryImpl(
    private val discoveryDao: DiscoveryDao,
    private val supabaseService: SupabaseService,
    private val aiEngine: AIInterpretationEngine
) : DiscoveryRepository {

    // ... (getAllDiscoveries, searchDiscoveries, getDiscoveryById, analyzeImage, shareDiscovery restent les mêmes) ...

    override fun getAllDiscoveries(userId: String): Flow<List<Discoveries>> =
        discoveryDao.getAllDiscoveries(userId)

    override fun searchDiscoveries(userId: String, query: String): Flow<List<Discoveries>> =
        discoveryDao.searchDiscoveries(userId, query)

    override fun getDiscoveryById(entryId: Int, userId: String): Flow<Discoveries?> =
        discoveryDao.getDiscoveryById(entryId, userId)

    override suspend fun analyzeImage(bitmap: Bitmap): Pair<String, String> =
        withContext(Dispatchers.Default) { aiEngine.identifyPlant(bitmap) }

    override suspend fun shareDiscovery(discovery: Discoveries) {
        Log.d("DiscoveryRepository", "Partage de la découverte: ${discovery.name}")
        // Implémentation réelle du partage (Intent.ACTION_SEND)
    }

    override suspend fun saveDiscoveryWithSync(entry: Discoveries, imageUri: Uri, userId: String): Int {
        return withContext(Dispatchers.IO) {

            // 1. Sauvegarde Locale (Room) - Initial
            val localId = discoveryDao.insert(entry) // Renvoie l'ID généré (Int)

            try {
                // 2. Upload de l'Image vers Supabase Storage
                val imageUrl = supabaseService.uploadImage(imageUri, entry.localImagePath)

                // 3. Insertion dans la Table Supabase (PostgREST)
                val remoteDiscovery = SupabaseDiscovery(
                    userId = userId,
                    name = entry.name,
                    summary = entry.summary,
                    imageUrl = imageUrl,
                    timestamp = entry.timestamp,
                    searchText = entry.searchText
                )
                val remoteId = supabaseService.insertDiscovery(remoteDiscovery) // ID distant simulé

                // 4. Mise à jour de l'Entité Room avec les données distantes
                discoveryDao.updateRemoteData(localId, imageUrl, remoteId)

            } catch (e: Exception) {
                Log.e("DiscoveryRepository", "Échec de la synchronisation Supabase pour ID $localId: ${e.message}")
            }

            localId
        }
    }

    override suspend fun deleteDiscoveryAndSync(entry: Discoveries) {
        withContext(Dispatchers.IO) {
            // 1. Suppression LOCALE (Room)
            discoveryDao.delete(entry)

            // 2. Tenter la suppression DISTANTE (si les données sont connues)
            try {
                if (entry.remoteImageUrl != null) {
                    supabaseService.deleteImage(entry.remoteImageUrl)
                }
                if (entry.remoteId != null) {
                    supabaseService.deleteDiscovery(entry.remoteId)
                }
            } catch (e: Exception) {
                Log.e("DiscoveryRepository", "Échec de la suppression Supabase pour ID ${entry.id}: ${e.message}")
            }
        }
    }
}

 */