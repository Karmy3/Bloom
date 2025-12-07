package com.example.bloom.repository

import android.content.Intent
import com.example.bloom.service.AuthService
import com.example.bloom.model.model.AuthResult
import com.example.bloom.model.model.AuthState
import com.example.bloom.model.model.dao.UserDao
import com.example.bloom.model.model.entities.User
import kotlinx.coroutines.flow.* //Contient Flow, StateFlow, MutableStateFlow, etc.
import kotlinx.coroutines.GlobalScope

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val webClientId: String ,// Client ID pour Google Sign-In
    private val userDao: UserDao
) : AuthRepository {

    //Gère l'état d'authentification principal (Authenticated, Unauthenticated, Loading)
    override val authState: Flow<AuthState> = authService.getCurrentUserFlow()
        .map { user ->
            if (user != null) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }.catch {
            // Gère les erreurs de l'observateur d'état global
            emit(AuthState.Error("Erreur d'état d'authentification."))
        }
        .distinctUntilChanged()
        .shareIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly // <-- CORRIGÉ
        )

    //Gère le résultat des opérations ponctuelles (login, signup)
    private val _lastAuthResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    override val lastAuthResult: StateFlow<AuthResult> = _lastAuthResult

    override val isAuthenticated: Flow<Boolean> = authState.map { it == AuthState.Authenticated }

    //UID change rarement une fois connecté
    override val currentUserId: Flow<String?> = authService.getCurrentUserFlow()
        .map { user ->
            user?.uid
        }
        .distinctUntilChanged()
        .shareIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly
        )

    // --- Email/Mot de Passe ---
    override suspend fun signUp(email: String, password: String) {
        _lastAuthResult.value = AuthResult.Idle // Reset
        try {
            _lastAuthResult.value = AuthResult.Loading

            //1. Inscription Cloud (Service API)
            authService.signUp(email, password)

            //2. Si l'opération Cloud a réussi, on procède à la sauvegarde locale
            saveUserLocally(email, password)

            _lastAuthResult.value = AuthResult.Success
        } catch (e: Exception) {
            _lastAuthResult.value = AuthResult.Failure(e.message ?: "Erreur d'inscription.")
        }
    }

    override suspend fun saveUserLocally(email: String, password: String) {
        val user = User(
            email = email,
            password = password
        )
        userDao.insert(user)
    }

    override suspend fun login(email: String, password: String) {
        _lastAuthResult.value = AuthResult.Idle // Reset
        try {
            _lastAuthResult.value = AuthResult.Loading
            authService.login(email, password)
            _lastAuthResult.value = AuthResult.Success
        } catch (e: Exception) {
            _lastAuthResult.value = AuthResult.Failure(e.message ?: "Erreur de connexion.")
        }
    }

    //Google Sign-In
    override fun getGoogleSignInIntent(webClientId: String): Intent {
        return authService.getGoogleSignInIntent(webClientId)
    }

    override suspend fun handleGoogleSignInResult(data: Intent?) {
        _lastAuthResult.value = AuthResult.Idle
        try {
            _lastAuthResult.value = AuthResult.Loading

            val idToken = authService.getGoogleIdToken(data)
            if (idToken != null) {
                authService.signInWithGoogleToken(idToken)
                _lastAuthResult.value = AuthResult.Success
            } else {
                _lastAuthResult.value = AuthResult.Failure("Échec de la récupération du jeton Google.")
            }
        } catch (e: Exception) {
            _lastAuthResult.value = AuthResult.Failure(e.message ?: "Erreur de connexion Google.")
        }
    }

    //Déconnexion
    override fun signOut() {
        authService.signOut()
        _lastAuthResult.value = AuthResult.Idle
    }
}