package com.example.bloom.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloom.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
// ----------------------------------------------------------------------
import kotlinx.coroutines.flow.Flow // ⬅️ CORRECTION 1 : L'import manquait
// ----------------------------------------------------------------------
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val googleWebClientId: String // Client ID doit être injecté
) : ViewModel() {

    // ----------------------------------------------------------------------
    // CORRECTION 2 : Les déclarations redondantes et non initialisées sont RETIRÉES
    //
    // L'interface AuthRepository DOIT définir isAuthenticated: Flow<Boolean>
    // et currentUserId: Flow<String?>
    // Si ces propriétés existent dans le AuthRepository, elles sont exposées ci-dessous.
    // ----------------------------------------------------------------------

    // --- PROPRIÉTÉS EXPOSÉES À L'UI ET À LA NAVIGATION ---

    // 1. État d'authentification principal (AuthState)
    val authState = authRepository.authState

    // 2. Résultat des opérations ponctuelles (AuthResult)
    val authResult = authRepository.lastAuthResult

    // 3. ID de l'utilisateur actuel
    //La déclaration est UNIQUE
    val currentUserId: Flow<String?> = authRepository.currentUserId

    // 4. État observé par le SplashScreen pour la logique de session courte/connexion
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    // --- LOGIQUE DE VÉRIFICATION D'ÉTAT (POUR SPLASHSCREEN) ---

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            // **Logique de vérification de l'état de connexion/session courte**
            val isLoggedIn = performSessionCheck()
            _isUserLoggedIn.value = isLoggedIn
        }
    }

    // Fonction simulée qui vérifie si l'utilisateur est connecté (à remplacer par la vraie logique de session)
    private suspend fun performSessionCheck(): Boolean {
        // Simule une opération asynchrène pour vérifier l'état
        kotlinx.coroutines.delay(500) // Simule le temps de vérification (réseau/DB)
        // Vous pouvez utiliser authRepository.isAuthenticated.first() ici si nécessaire
        return false // Valeur par défaut pour l'exemple
    }

    // --- ACTIONS POUR L'UI ---

    // 1. Inscription : Délègue au Repository
    fun signUp(email: String, password: String) = viewModelScope.launch {
        authRepository.signUp(email, password)
    }

    // 2. Connexion
    fun login(email: String, password: String) = viewModelScope.launch {
        authRepository.login(email, password)
    }

    // 3. Google Sign-In (Intent)
    fun getGoogleSignInIntent() = authRepository.getGoogleSignInIntent(googleWebClientId)

    // 4. Google Sign-In (Résultat)
    fun handleGoogleSignInResult(data: android.content.Intent?) = viewModelScope.launch {
        authRepository.handleGoogleSignInResult(data)
    }

    // 5. Déconnexion
    fun signOut() {
        authRepository.signOut()
    }
}