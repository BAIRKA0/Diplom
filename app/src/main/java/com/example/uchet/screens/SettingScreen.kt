package com.example.uchet.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uchet.activity.AuthActivity
import com.example.uchet.activity.MainActivity
import com.example.uchet.viewModels.SettingViewModel

@Composable
fun SettingScreen(
    context: Context
) {
    val settingViewModel = viewModel(modelClass = SettingViewModel::class.java)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
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
            SettingHeader(drawerState, settingViewModel)
            SettingMenu(settingViewModel)
        }
    }
}

@Composable
fun SettingHeader(drawerState: DrawerState, settingViewModel: SettingViewModel){

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(color = Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Burger(drawerState)
        Row(){
            Button(onClick = {
                settingViewModel.changePage(1)
            }) {
                Text(text = "Страйница 1")
            }
            Button(onClick = {
                settingViewModel.changePage(2)
            }) {
                Text(text = "Страйница 2")
            }
            Button(onClick = {
                settingViewModel.changePage(3)
            }) {
                Text(text = "Страйница 3")
            }
        }
    }
}

@Composable
fun SettingMenu(settingViewModel: SettingViewModel){
    Column(

    ) {
        val page by settingViewModel.page.collectAsState()
        val isDialogOpen by settingViewModel.isDownloadOpen.collectAsState()
        if(page == 1) {

            Button(onClick = {
                settingViewModel.updateSotrudnik()
            }) {
                Text(text = "Обновить сотрудников")
            }
            Button(onClick = {
                settingViewModel.updateDoc()
            }) {
                Text(text = "Обновить документы")
            }
        }else if(page==2){

        }else if(page==3){

        }
        if (isDialogOpen) {
            LoadingDialog(message = "Идет выгрузка данных")
        }
    }
}

@Composable
fun LoadingDialog(message: String) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                Modifier
                    .width(IntrinsicSize.Max)
                    .padding(32.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = message,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                val infiniteTransition = rememberInfiniteTransition()
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ), label = "Loading"
                )

                Icon(
                    Icons.Filled.Refresh,
                    "Загрузка",
                    Modifier
                        .height(64.dp)
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .rotate(angle)
                )
            }
        }
    }
}
