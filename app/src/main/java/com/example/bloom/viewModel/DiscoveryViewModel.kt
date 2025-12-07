package com.example.bloom.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloom.repository.AuthRepository
import com.example.bloom.repository.DiscoveryRepository
import com.example.bloom.model.model.entities.Discovery // CORRECTION 1: Utiliser Discovery (sans 's')
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first // IMPORTANT: Import pour .first()
import kotlinx.coroutines.launch

class DiscoveryViewModel(
    private val discoveryRepository: DiscoveryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _captureState = MutableStateFlow<CaptureState>(CaptureState.Idle)
    val captureState: StateFlow<CaptureState> = _captureState

    fun resetCaptureState() {
        _captureState.value = CaptureState.Idle
    }

    fun processAndSaveDiscovery(bitmap: Bitmap, imageUri: Uri, localImagePath: String) = viewModelScope.launch {

        // CORRECTION 2: Accéder à la valeur du Flow unique via .first()
        // Nécessite une coroutine et le Flow de AuthRepository pour fonctionner.
        val userId = authRepository.currentUserId.first() ?: run {
            _captureState.value = CaptureState.Error("Utilisateur non connecté.")
            return@launch
        }

        _captureState.value = CaptureState.AIProcessing(localImagePath)

        try {
            // 1. Analyse AI
            val (name, fact) = discoveryRepository.analyzeImage(bitmap) // CORRECTION 3: Utiliser 'fact'

            if (name == "NOT_RELEVANT") throw Exception(fact)

            // 2. Création de l'Entité Locale
            val newEntry = Discovery( // CORRECTION 1: Utiliser Discovery
                userId = userId,
                name = name,
                fact = fact, // CORRECTION 3: Utiliser 'fact' pour correspondre à l'entité
                imageUrl = "", // Sera rempli par le repository après l'upload
                localImagePath = localImagePath,
                timestamp = System.currentTimeMillis()
            )

            _captureState.value = CaptureState.Saving(newEntry)

            // 3. Sauvegarde Room et Synchronisation Supabase
            val generatedId = discoveryRepository.saveDiscoveryWithSync(newEntry, imageUri, userId)

            _captureState.value = CaptureState.Success(generatedId)

        } catch (e: Exception) {
            _captureState.value = CaptureState.Error("Erreur de synchro: ${e.message}")
        }
    }
}