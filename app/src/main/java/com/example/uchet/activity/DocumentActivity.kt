package com.example.uchet.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.uchet.screens.DocumentScreen
import com.example.uchet.ui.theme.UchetTheme

class DocumentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UchetTheme {
                val receivedValue = intent.getStringExtra("id")
                if (receivedValue != null) {
                    Log.d("id", receivedValue)
                    DocumentScreen(context = this,receivedValue.toInt())
                }
            }
        }
    }
}