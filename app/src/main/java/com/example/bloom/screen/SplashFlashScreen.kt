package com.example.bloom.screen

import android.R.attr.delay
import com.example.bloom.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.model.model.AuthState
import com.example.bloom.viewModel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val MIN_SPLASH_TIME = 1000L
    val authState by authViewModel.authState.collectAsState(initial = AuthState.Loading)

    LaunchedEffect (authState) {

        delay(MIN_SPLASH_TIME)

        when (authState) {

            //1. État de chargement :
            is AuthState.Loading -> {
                // Aucune action de navigation tant que l'état est "Loading"
            }

            //2. Authentifié : Session courte/longue valide -> Aller à la page d'accueil
            is AuthState.Authenticated -> {
                navController.navigate("journal") { // J'utilise "journal" comme page d'accueil
                    popUpTo("splash") { inclusive = true }
                }
            }

            //3. Non Authentifié : Délai dépassé ou jamais connecté -> Aller à la connexion
            is AuthState.Unauthenticated -> {
                navController.navigate("signIn") {
                    popUpTo("splash") { inclusive = true }
                }
            }

            //4. Erreur :On affiche l'erreur ou redirige vers la connexion avec un message
            is AuthState.Error -> {
                navController.navigate("signIn") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF26D962)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.leaf),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
