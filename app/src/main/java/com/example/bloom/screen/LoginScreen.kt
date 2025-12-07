package com.example.bloom.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bloom.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.bloom.model.model.AuthState
import com.example.bloom.model.model.dao.UserDao
import com.example.bloom.ui.theme.Shapes
import com.example.bloom.viewModel.AuthViewModel
import com.example.bloom.viewModel.FirebaseAuthViewModel // Assurez-vous que GoogleLoginButton est défini et utilise ce ViewModel

@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavController,userDao: UserDao,googleViewModel: FirebaseAuthViewModel){
    val authState by authViewModel.authState.collectAsState(initial = AuthState.Loading)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.leaf),
                contentDescription = "Leaf Icon",
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        //Onglets SignIn / SignUp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = Shapes.large)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("signIn") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = Shapes.small,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        "Sign In",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Button(
                    onClick = { navController.navigate("signUp") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = Shapes.small,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Text(
                        "Sign Up",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Champs
        var email by remember { mutableStateOf("") }

        Text(
            "Email Address",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    "enter your email",
                    color = MaterialTheme.colorScheme.onSecondary
                ) },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = Shapes.large)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = Shapes.large)
        )


        Spacer(Modifier.height(16.dp))

        var password by remember { mutableStateOf("") }

        Text("Password",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    "enter your password",
                    color = MaterialTheme.colorScheme.onSecondary
                ) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = Shapes.large)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = Shapes.large)
        )


        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.login(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = Shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26D962))
        ) {
            Text(
                "Sign In",
                color =MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        }

        // Navigation automatique si connecté
        LaunchedEffect(authState) {
            if(authState is AuthState.Authenticated){
                navController.navigate("journal")
            }
        }

        //Affichage de l'état (Chargement/Erreur)
        when(authState) {
            AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text((authState as AuthState.Error).message)
            else -> {}
        }

        Spacer(Modifier.height(24.dp))

        Text("OR",
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(Modifier.height(16.dp))

        GoogleLoginButton(authViewModel = googleViewModel, navController = navController)
    }
}