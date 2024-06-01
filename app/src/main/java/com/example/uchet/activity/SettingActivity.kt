package com.example.uchet.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.uchet.AdminPasswordManager
import com.example.uchet.screens.SettingScreen
import com.example.uchet.ui.theme.UchetTheme

class SettingActivity : ComponentActivity() {

    private lateinit var adminPasswordManager: AdminPasswordManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UchetTheme {
                SettingScreen(context = this)
            }
        }
    }
}