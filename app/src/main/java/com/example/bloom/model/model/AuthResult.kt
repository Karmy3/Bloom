package com.example.bloom.model.model

// Représente l'état d'un processus d'authentification spécifique (login, signup, Google Sign-In)
sealed class AuthResult {
    object Success : AuthResult()
    data class Failure(val message: String) : AuthResult()
    object Idle : AuthResult() // État initial ou réinitialisé
    object Loading : AuthResult() // État pendant l'exécution d'une opération
}
