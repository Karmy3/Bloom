package com.example.bloom.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.example.bloom.model.model.AuthResult // Import manquant ajouté
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow // Import Flow ajouté si non présent

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    // --- Propriétés ---
    val authState: Flow<com.example.bloom.model.model.AuthState> // Ajouté le type Flow
    val lastAuthResult: StateFlow<AuthResult> // Ajouté le type StateFlow

    val currentUserId: Flow<String?>

    // --- Méthodes Email/Password (Ajoutées) ---
    suspend fun signUp(email: String, password: String)
    suspend fun login(email: String, password: String)

    //Méthodes Google Sign-In
    fun getGoogleSignInIntent(webClientId: String): Intent
    //Implementation utilise handleGoogleSignInResult, on le garde
    suspend fun handleGoogleSignInResult(data: Intent?)

    //Méthodes Room/Locale
    suspend fun saveUserLocally(email: String, password: String)

    //Méthode de Déconnexion
    fun signOut()
}