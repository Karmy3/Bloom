
package com.example.bloom.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloom.repository.DiscoveryRepository
import com.example.bloom.repository.AuthRepository
import com.example.bloom.model.model.entities.Discovery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoveryDetailViewModel @Inject constructor(
    private val discoveryRepository: DiscoveryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _discovery = MutableStateFlow<Discovery?>(null)
    val discovery: StateFlow<Discovery?> = _discovery

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Charge les détails d'une découverte spécifique à partir de son ID.
     */
    fun loadDiscovery(discoveryId: String?) {
        if (discoveryId.isNullOrEmpty()) {
            _discovery.value = null
            _isLoading.value = false
            return
        }

        // Évite de recharger si l'ID est le même et que le chargement est terminé
        if (_discovery.value?.id == discoveryId && !_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            // Correction pour lire l'état actuel des Flows d'authentification
            val isAuthenticated = authRepository.isAuthenticated.first()
            val userId = authRepository.currentUserId.first()

            if (isAuthenticated && userId != null) {
                try {
                    val result = discoveryRepository.getDiscoveryById(discoveryId)
                    _discovery.value = result
                } catch (e: Exception) {
                    println("VM Error: Failed to load discovery details for ID $discoveryId: ${e.message}")
                    _discovery.value = null
                }
            } else {
                _discovery.value = null
            }
            _isLoading.value = false
        }
    }

    /**
     * SUPPRESSION CORRIGÉE : Utilise l'état interne (_discovery.value) et ne prend PAS d'argument.
     * Ceci est la signature attendue par l'écran maintenant.
     */
    fun deleteCurrentDiscovery() = viewModelScope.launch {
        val entry = _discovery.value
        if (entry != null) {
            discoveryRepository.deleteDiscovery(entry.id)
            _discovery.value = null // Vide l'état pour indiquer la suppression
        } else {
            println("VM Warning: Tentative de suppression d'une découverte nulle.")
        }
    }

    fun shareDiscovery(discovery: Discovery) = viewModelScope.launch {
        discoveryRepository.shareDiscovery(discovery)
    }

}


