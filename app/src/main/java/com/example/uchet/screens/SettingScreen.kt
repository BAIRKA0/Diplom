package com.example.uchet.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uchet.AdminPasswordManager
import com.example.uchet.activity.AuthActivity
import com.example.uchet.activity.MainActivity
import com.example.uchet.entities.Acc
import com.example.uchet.viewModels.SettingViewModel
import com.example.uchet.viewModels.reportViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun SettingScreen(
    context: Context
) {
    val dataStore = AdminPasswordManager(context)
    val settingViewModel = viewModel(modelClass = SettingViewModel::class.java)
    val reposrtViewModel = viewModel(modelClass = reportViewModel::class.java)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent("Настройки",menuItems = listOf(
                "Список документов" to {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                "Отчетность" to {
                    showDialog = true
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
            SettingHeader(drawerState, settingViewModel,dataStore)
            SettingMenu(settingViewModel,dataStore)
        }
    }
    if (showDialog) {
        val startDate = remember { mutableStateOf(LocalDate.now()) }
        val endDate = remember { mutableStateOf(LocalDate.now()) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Выберите период") },
            text = {
                DatePeriodPickers(startDate,endDate)
            },
            confirmButton = {
                CustomButton(title = "Сформировать", onClick = { reposrtViewModel.saveByDate(
                    startDate.value.toDate(),
                    endDate.value.toDate(),
                    context
                ) })
            },
            dismissButton = {
                CustomButton(title = "Отмена", onClick = { showDialog = false })
            }
        )
    }
}

@Composable
fun SettingHeader(drawerState: DrawerState, settingViewModel: SettingViewModel, adminPasswordManager: AdminPasswordManager){
    val page by settingViewModel.page.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val adminPass = remember { mutableStateOf("") }
    LaunchedEffect(key1 = true) {
        adminPasswordManager.getAdminPass.collectLatest {
            adminPass.value = it
        }
    }
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(color = Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Burger(drawerState)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround

        ){

            CustomButton(onClick = {
                if(page!=1)
                settingViewModel.changePage(1)
            },
                title = "Сервер",
                buttonType = if(page==1) ButtonTypes.Positive else ButtonTypes.Neutral)
            CustomButton(onClick = {
                if(page!=2)
                settingViewModel.changePage(2)
            },
                title = "RFID",
                buttonType = if(page==2) ButtonTypes.Positive else ButtonTypes.Neutral)
            CustomButton(onClick = {
                if(page!=3)
                showDialog = true
            },
                title = "Панель администратора",
                buttonType = if(page==3) ButtonTypes.Positive else ButtonTypes.Neutral)
        }
    }
    if(showDialog) {
        PasswordDialog(onConfirm = {
            settingViewModel.changePage(3)
            showDialog = false
                                   },
            onDismiss = { showDialog = false },
            adminPass = adminPass.value)
    }
}

@Composable
fun PasswordDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    adminPass: String
) {
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { },
        title = {
                Column {
                    TextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        label = { Text("Пароль администратора") },
                        isError = error != "",
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(end = 6.dp)
                    )
                    if(error != "")
                        Text(error, color = Color.Red)
                }
                },
        confirmButton = {
            CustomButton(
                onClick = {
                    if(password == adminPass){
                        onConfirm()
                    }else{
                        error = "Введен неверный пароль"
                    }
                },
                title = "ОK"
            )
        },
        dismissButton = {
            CustomButton(
                onClick = {
                    onDismiss()
                },
                title = "Отмена"
            )
        }
    )
}

@Composable
fun AccDialog(
    onDismiss: () -> Unit,
    settingViewModel: SettingViewModel
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { },
        title = {
            Column {
                TextField(
                    value = login,
                    onValueChange = {
                        login = it
                    },
                    label = { Text("Логин") },
                    isError = error != "",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(end = 6.dp)
                )
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = { Text("Пароль") },
                    isError = error != "",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(end = 6.dp)
                )
                TextField(
                    value = uid,
                    onValueChange = {
                        uid = it
                    },
                    label = { Text("Пропуск") },
                    isError = error != "",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(end = 6.dp)
                )
                if(error != "")
                    Text(error, color = Color.Red)
            }
        },
        confirmButton = {
            CustomButton(
                onClick = {
                    settingViewModel.insertAcc(Acc(login,password,uid))
                    onDismiss()
                },
                title = "Добавить"
            )
        },
        dismissButton = {
            CustomButton(
                onClick = {
                    onDismiss()
                },
                title = "Отмена"
            )
        }
    )
}

@Composable
fun SettingMenu(settingViewModel: SettingViewModel,adminPasswordManager: AdminPasswordManager){
    val page by settingViewModel.page.collectAsState()
    if(page == 1) {
        ApiSetting(settingViewModel,adminPasswordManager)
    }else if(page==2){
        RfidSetting(adminPasswordManager)
    }else if(page==3){
        AdminSetting(adminPasswordManager,settingViewModel)
    }
}

@Composable
fun AdminSetting(adminPasswordManager: AdminPasswordManager, settingViewModel: SettingViewModel){
    val adminPass = remember { mutableStateOf("") }
    val isEmpty = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val accState = settingViewModel.AccState
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        adminPasswordManager.getAdminPass.collectLatest {
            adminPass.value = it
        }
        isEmpty.value = adminPass.value.trim().isEmpty()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            TextField(
                value = adminPass.value,
                onValueChange = {
                    adminPass.value = it
                    isEmpty.value = adminPass.value.trim().isEmpty()
                },
                label = { Text("Пароль администратора") },
                isError = isEmpty.value,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(end = 6.dp)
            )
            CustomButton(title = "Сохранить", enabled = !isEmpty.value, onClick = {
                coroutineScope.launch {
                    adminPasswordManager.saveStringPreference(adminPasswordManager.companion.ADMIN_PASS, adminPass.value)
                    scaffoldState.snackbarHostState.showSnackbar("Сохранено")

                }
            })
        }
        Column(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .border(1.dp, Color.Black)
                    .height(40.dp)
            ) {
                Cell(
                    text = "Логин", modifier = Modifier
                        .weight(3f)
                        .border(1.dp, Color.Black)
                )
                Cell(
                    text = "Пароль", modifier = Modifier
                        .weight(3f)
                        .border(1.dp, Color.Black)
                )
                Cell(
                    text = "Пропуск", modifier = Modifier
                        .weight(3f)
                        .border(1.dp, Color.Black)
                )
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            showDialog = true
                        }
                ) {
                    Text(text = "+",
                        color = Color.Green,
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if(accState.acc.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    items(accState.acc) { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Black)
                                .height(45.dp)
                        ) {
                            Cell(
                                text = account.login,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(3f)
                                    .border(1.dp, Color.Black)
                            )
                            Cell(
                                text = account.password,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(3f)
                                    .border(1.dp, Color.Black)
                            )
                            Cell(
                                text = account.uid,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(3f)
                                    .border(1.dp, Color.Black)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .clickable {
                                        settingViewModel.delAcc(account)
                                    }
                            ) {
                                Text(text = "-",
                                    fontSize = 24.sp,
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if(showDialog){
        AccDialog(onDismiss = { showDialog=false }, settingViewModel = SettingViewModel())
    }
}

@Composable
fun ApiSetting(settingViewModel: SettingViewModel,adminPasswordManager: AdminPasswordManager){
    val isDialogOpen by settingViewModel.isDownloadOpen.collectAsState()
    val deviceID = remember { mutableStateOf("") }
    val regex = Regex("\\d{15}")
    val matchesPattern = remember { mutableStateOf(true) }
    val isEmpty = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        adminPasswordManager.getUniqueId.collectLatest {
            deviceID.value = it
        }
        isEmpty.value = deviceID.value.trim().isEmpty()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
            ){
            TextField(
                value = deviceID.value,
                onValueChange = {
                    deviceID.value = it
                    isEmpty.value = deviceID.value.trim().isEmpty()
                    matchesPattern.value = regex.matches(deviceID.value)
                },
                label = { Text("ВЖК (серверный уникальный идентификатор)") },
                isError = isEmpty.value or !matchesPattern.value,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(end = 6.dp)
            )
            CustomButton(title = "Сохранить", enabled = !isEmpty.value and matchesPattern.value, onClick = {
                coroutineScope.launch {
                    adminPasswordManager.saveStringPreference(adminPasswordManager.companion.UNIQUE_ID, deviceID.value)
                    scaffoldState.snackbarHostState.showSnackbar("Сохранено")
                }
            })
        }
        CustomButton(modifier = Modifier
            .padding(top = 6.dp),
            onClick = {
            settingViewModel.updateSotrudnik()
        },title="Обновить сотрудников")
        CustomButton(modifier = Modifier
            .padding(top = 6.dp),
            onClick = {
            settingViewModel.updateDoc()
        },
            title = "Обновить документы распределения")
    }
    if (isDialogOpen) {
        LoadingDialog(message = "Идет выгрузка данных")
    }

}

@Composable
fun RfidSetting(adminPasswordManager: AdminPasswordManager){
    val rfid = remember { mutableStateOf("") }
    val isEmpty = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        adminPasswordManager.getRFID.collectLatest {
            rfid.value = it
        }
        isEmpty.value = rfid.value.trim().isEmpty()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ){
            var showDialog = false
            TextField(
                value = rfid.value,
                onValueChange = {
                    rfid.value = it
                    isEmpty.value = rfid.value.trim().isEmpty()
                },
                label = { Text("Наименование RFID устройства") },
                isError = isEmpty.value,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(end = 6.dp)
            )
            CustomButton(title = "Сохранить", enabled = !isEmpty.value, onClick = {
                coroutineScope.launch {
                    adminPasswordManager.saveStringPreference(adminPasswordManager.companion.RFID_NAME, rfid.value)
                    showDialog = true
                }
            })
            if(showDialog) {
                Alert(onConfirm = { showDialog = false }, text = "Сохранено")
            }
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

@Composable
fun CustomButton(modifier: Modifier = Modifier,
                 enabled: Boolean = true,
                 title: String,
                 buttonType: ButtonTypes = ButtonTypes.Positive,
                 onClick: () -> Unit,
                 fontSize: TextUnit = 24.sp,
                 colors: ButtonColors = ButtonDefaults.buttonColors(
                     contentColor = Color.Black,
                     containerColor = when(buttonType) {
                         ButtonTypes.Positive -> Color(0xff81E95C)
                         ButtonTypes.Negative -> Color(0xffFF3333)
                         ButtonTypes.Neutral -> Color(0xffD9D9D9)
                     }
                 )
                 ) {

    Button(modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = colors,
        enabled = enabled,
        border = BorderStroke(1.dp, Color.Black),
        onClick = { onClick() }) {
        Text(
            text = title,
            fontSize = fontSize,
            color = Color.Black
        )
    }
}

enum class ButtonTypes {
    Positive,
    Negative,
    Neutral
}
