package com.example.bloom

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bloom.model.model.dao.UserDao
import com.example.bloom.screen.DiscoveryCaptureAndAnalyzer
import com.example.bloom.screen.JournalListScreen
import com.example.bloom.screen.LoginGoogleScreen
import com.example.bloom.screen.LoginScreen
import com.example.bloom.screen.SignUpScreen
import com.example.bloom.screen.SplashScreen
import com.example.bloom.viewModel.AuthViewModel
import com.example.bloom.viewModel.FirebaseAuthViewModel
import com.example.bloom.viewModel.JournalViewModel

@Composable
fun NavGraph(userDao: UserDao) {
    val navController = rememberNavController()

    //ViewModels globaux
    val authViewModel: AuthViewModel = viewModel()
    val googleViewModel: FirebaseAuthViewModel = viewModel()
    val journalViewModel: JournalViewModel = viewModel()

    //La destination de départ est toujours "splash".
    NavHost(navController, startDestination = "splash") {

        //1. Écran de Démarrage
        composable("splash") {
            SplashScreen(navController, authViewModel)
        }

        //2. Authentification
        composable("signIn") {
            LoginScreen(
                authViewModel = authViewModel,
                userDao = userDao,
                navController = navController,
                googleViewModel = googleViewModel
            )
        }

        composable("signUp") {
            SignUpScreen(
                authViewModel = authViewModel,
                userDao = userDao,
                navController = navController,
                googleViewModel = googleViewModel
            )
        }

        //3. Route non définie (Home)
        composable("home") {
            JournalListScreen(navController = navController, viewModel = journalViewModel)
        }

        //4. Capture et Ajout de Découvert
        composable("addDiscovery") {
            DiscoveryCaptureAndAnalyzer(
                navController = navController
            )
        }

        //5. Journal / Liste des Découvertes
        composable("journal") {
            JournalListScreen(
                navController = navController,
                viewModel = journalViewModel
            )
        }

        //5. Details
        composable("details"){

        }

        //Route additionnelle
        composable("addManual") {
            DiscoveryCaptureAndAnalyzer(navController = navController)
        }

        //Route Google Connect
        composable("connectGoogle") {
            LoginGoogleScreen(navController = navController, googleViewModel = googleViewModel)
        }
    }
}