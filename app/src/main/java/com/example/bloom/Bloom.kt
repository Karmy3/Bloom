package com.example.bloom

import android.app.Application
import androidx.room.Room
import com.example.bloom.repository.AuthRepository
import com.example.bloom.repository.AuthRepositoryImpl
import com.example.bloom.repository.DiscoveryRepository
import com.example.bloom.ia.AIInterpretationEngine
import com.example.bloom.model.model.config.AppDatabase
import com.example.bloom.service.AuthService
import com.google.firebase.auth.FirebaseAuth //Import nécessaire pour la connection
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// DÉCLARATIONS GLOBALES (TOP LEVEL)
lateinit var AUTH_REPO: AuthRepository
lateinit var DISCOVERY_REPO: DiscoveryRepository
lateinit var supabase : SupabaseClient

class Bloom : Application() {

    override fun onCreate() {
        super.onCreate()

        //1. Supabase
        supabase = createSupabaseClient(
            supabaseUrl = "https://yeoghssaxsyoobvaknsw.supabase.co",
            supabaseKey = "TA_CLEI"
        ) {
            install(Postgrest)
            install(Storage)
        }

        //2. ROOM
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "bloom-db"
        ).build()

        val userDao = db.userDao()
        val discoveryDao = db.discoveryDao()

        //3. Firebase
        val firebaseAuthInstance = FirebaseAuth.getInstance()

        //AuthService
        val authService = AuthService(
            firebaseAuth = firebaseAuthInstance,
            applicationContext = applicationContext
        )

        val aiEngine = AIInterpretationEngine()


        //4. INITIALISATION AUTH_REPO
        val webClientId = "639062559591-6f4mrrko7qbdjbv0jbv97q5bhncoeej0.apps.googleusercontent.com"

        AUTH_REPO = AuthRepositoryImpl(
            authService = authService,
            webClientId = webClientId,
            userDao = userDao
        )

        //5. INITIALISATION DISCOVERY_REPO
        DISCOVERY_REPO = DiscoveryRepository(
            discoveryDao = discoveryDao,
            aiEngine = aiEngine,
            supabaseClient = supabase
        )
    }
}
