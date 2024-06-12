package com.example.uchet.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.uchet.AdminPasswordManager
import com.example.uchet.screens.MainScreen
import com.example.uchet.ui.theme.UchetTheme

class MainActivity : ComponentActivity() {

    private lateinit var adminPasswordManager: AdminPasswordManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UchetTheme {
                MainScreen(context = this)
            }
        }
        adminPasswordManager = AdminPasswordManager(applicationContext)
//        lifecycleScope.launch {
//            adminPasswordManager.saveAdminPassword("123")
//        }
//        lifecycleScope.launch {
//            adminPasswordManager.adminPassword.collect { password ->
//                println("Admin Password: $password")
//            }
//        }
    }
}