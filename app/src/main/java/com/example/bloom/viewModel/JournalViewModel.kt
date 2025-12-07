// Fichier : com/example/bloom/viewModel/JournalViewModel.kt (CORRIGÉ)
package com.example.bloom.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloom.repository.DiscoveryRepository
import com.example.bloom.repository.AuthRepository
import com.example.bloom.model.model.entities.Discovery
import kotlinx.coroutines.flow.* // Import de flow.* pour combine et flatMapLatest
import kotlinx.coroutines.launch

class JournalViewModel(
    private val discoveryRepository: DiscoveryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    val filteredDiscoveries: StateFlow<List<Discovery>> =
        // Combine les trois sources de changement qui doivent déclencher une mise à jour
        combine(
            authRepository.isAuthenticated, // Flow<Boolean>
            authRepository.currentUserId,   // Flow<String?>
            _searchQuery                    // Flow<String>
        ) { isAuthenticated, userId, query ->
            // Le corps de combine reçoit les VRAIES valeurs actuelles (non-Flow)
            // à chaque fois que l'un des trois Flow émet.

            if (!isAuthenticated || userId == null) {
                // Utilisateur non connecté ou ID manquant : pas de données
                flowOf(emptyList())
            } else if (query.isBlank()) {
                // Connecté et pas de recherche : renvoie toutes les découvertes
                discoveryRepository.getAllDiscoveries(userId)
            } else {
                // Connecté et recherche active : renvoie les résultats filtrés
                discoveryRepository.searchDiscoveries(userId, query.lowercase())
            }
        }
            // Utilise flatMapLatest pour basculer vers le nouveau Flow (getAllDiscoveries ou searchDiscoveries)
            .flatMapLatest { innerFlow -> innerFlow }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteEntry(entry: Discovery) = viewModelScope.launch {
        discoveryRepository.deleteDiscoveryAndSync(entry)
    }

    fun shareDiscovery(discovery: Discovery) = viewModelScope.launch {
        discoveryRepository.shareDiscovery(discovery)
    }
}