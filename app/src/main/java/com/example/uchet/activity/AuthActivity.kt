package com.example.uchet.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.uchet.screens.AuthorizationScreen
import com.example.uchet.ui.theme.UchetTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UchetTheme {
                AuthorizationScreen(context = this)
            }
        }
    }
}