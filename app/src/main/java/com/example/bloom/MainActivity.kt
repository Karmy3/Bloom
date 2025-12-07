package com.example.bloom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.bloom.model.model.config.AppDatabase
import com.example.bloom.ui.theme.BloomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        enableEdgeToEdge()
        setContent {
            BloomTheme(
                darkTheme = isSystemInDarkTheme(),
                dynamicColor = false
            ) {
                NavGraph(userDao = userDao)
            }
        }
    }
}

