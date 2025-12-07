package com.example.bloom.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bloom.R
import com.example.bloom.ui.theme.Shapes
import com.example.bloom.viewModel.FirebaseAuthViewModel

@Composable
fun LoginGoogleScreen(navController: NavController,googleViewModel: FirebaseAuthViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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

        Text("Connect with Google")
        Text("Select your profile")

        var email by remember { mutableStateOf("") }

        Text("Email",
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

        Divider(
            modifier = Modifier
                .padding(top=10.dp),
            thickness = 5.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        GoogleLoginButton(authViewModel = googleViewModel, navController = navController)

        Divider(
            modifier = Modifier
                .padding(top=10.dp),
            thickness = 5.dp,
            color = MaterialTheme.colorScheme.secondary
        )


        Button(
            onClick = { navController.navigate("signIn") },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = Shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp)
        ) {
            Text(
                "Add a new count",
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        Divider(
            modifier = Modifier
                .padding(top=10.dp),
            thickness = 5.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}