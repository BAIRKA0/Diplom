package com.example.uchet.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uchet.activity.AuthActivity
import com.example.uchet.activity.MainActivity
import com.example.uchet.viewModels.SettingViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun SettingScreen(
    context: Context
) {
    val settingViewModel = viewModel(modelClass = SettingViewModel::class.java)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent("Настройки",menuItems = listOf(
                "Список документов" to {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                "Отчетность" to {

                },
                "Выход" to {
                    context.startActivity(Intent(context, AuthActivity::class.java))
                }
            ))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            SettingHeader(drawerState, coroutineScope)
            SettingMenu(settingViewModel)
        }
    }
}

@Composable
fun SettingHeader(drawerState: DrawerState,coroutineScope: CoroutineScope){
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(color = Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Burger(drawerState, coroutineScope)
    }
}

@Composable
fun SettingMenu(settingViewModel: SettingViewModel){

}
