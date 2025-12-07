package com.example.bloom.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bloom.repository.AuthRepository
import com.example.bloom.repository.DiscoveryRepository
import com.example.bloom.viewModel.*

//Pour gérer l'injection manuelle des Repositories dans les ViewModels
class AppViewModelFactory(
    private val application: Application,
    private val authRepository: AuthRepository,
    private val discoveryRepository: DiscoveryRepository,
    private val defaultEntryId: Int = -1 // Pour DetailViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                // Si AuthViewModel attend (authRepository, googleWebClientId)
                AuthViewModel(authRepository, "639062559591-6f4mrrko7qbdjbv0jbv97q5bhncoeej0.apps.googleusercontent.com") as T
            }
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                JournalViewModel(discoveryRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(DiscoveryViewModel::class.java) -> {
                DiscoveryViewModel(discoveryRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(DiscoveryDetailViewModel::class.java) -> {
                //Si DiscoveryDetailViewModel attend seulement (discoveryRepository, authRepository)
                DiscoveryDetailViewModel(discoveryRepository, authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}