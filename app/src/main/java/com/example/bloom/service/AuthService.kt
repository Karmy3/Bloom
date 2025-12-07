package com.example.bloom.service

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

//Le service gère l'interaction directe avec l'API (Firebase Auth)
class AuthService(
    private val firebaseAuth: FirebaseAuth,
    private val applicationContext: Context
) {
    //Fournit l'état de l'utilisateur via Flow pour le Repository (UID + Jetons)
    fun getCurrentUserFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    //Connexion Email/Mot de Passe
    suspend fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    //Google Sign-In

    //Récupère l'Intent nécessaire pour démarrer l'activité Google Sign-In
    fun getGoogleSignInIntent(webClientId: String): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
        return googleSignInClient.signInIntent
    }

    //Gère le résultat de l'activité Google et retourne le jeton Firebase
    fun getGoogleIdToken(data: Intent?): String? {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
        return account?.idToken
    }

    //Connecte Firebase avec le jeton Google
    suspend fun signInWithGoogleToken(idToken: String) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    //Déconnexion
    fun signOut() {
        firebaseAuth.signOut()
        GoogleSignIn.getClient(applicationContext, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
    }

    //Méthode pour l'échange de jeton Supabase
    suspend fun signInWithCustomToken(idToken: String) {
        firebaseAuth.signInWithCustomToken(idToken)
    }
}