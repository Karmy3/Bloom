package com.example.bloom.model.model

// Représente l'état de l'utilisateur pour l'UI
sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}