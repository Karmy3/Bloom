package com.example.bloom.screen

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import com.example.bloom.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloom.ui.theme.Shapes
import com.example.bloom.viewModel.FirebaseAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun GoogleLoginButton(authViewModel: FirebaseAuthViewModel, navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity

    //Configure Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id)) // depuis google-services.json
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken!!
            authViewModel.firebaseAuthWithGoogle(idToken, {
                //Succès, utilisateur connecté
                navController.navigate("home") // navigation vers l'écran principal
            }, { error ->
                // Affiche l'erreur
                Log.e("GoogleLogin", "Erreur login: ${error.message}")
            })
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Sign in failed", e)
        }
    }

    OutlinedButton(
        onClick = {
            val signInIntent: Intent = googleSignInClient.signInIntent
            launcher.launch(signInIntent) },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = Shapes.large)
            .height(50.dp) ,
        shape = MaterialTheme.shapes.large
    ) {
        Icon(
            painter = painterResource(id = R.drawable.google),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(32.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            "Continue with Google",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}