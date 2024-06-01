package com.example.uchet.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import com.example.uchet.R
import com.example.uchet.activity.AuthActivity
import com.example.uchet.activity.DocumentActivity
import com.example.uchet.activity.MainActivity
import com.example.uchet.activity.SettingActivity
import com.example.uchet.viewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MainScreen(
    context: Context
) {
    val mainViewModel = viewModel(modelClass = MainViewModel::class.java)
    mainViewModel.getDateList()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent("Список документов",menuItems = listOf(
                "Настройки" to {
                    context.startActivity(Intent(context, SettingActivity::class.java))
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
            Header(mainViewModel,drawerState, coroutineScope, context)
            Table(context, mainViewModel)
        }
    }
}

@Composable
fun Header(mainViewModel: MainViewModel, drawerState: DrawerState, coroutineScope: CoroutineScope, context: Context) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(color = Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Burger(drawerState, coroutineScope)
        Filters(mainViewModel)
        Datetime()
        RFID("main", context)
    }
}

@Composable
fun Burger(drawerState: DrawerState, coroutineScope: CoroutineScope) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(R.raw.burger, imageLoader),
        contentDescription = null,
        modifier = Modifier
            .fillMaxHeight(0.6f)
            .clickable {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
    )
}

@Composable
fun Filters(mainViewModel: MainViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (

        ) {
            for (i in mainViewModel.dateState.dateList){
                Button(onClick = {
                    mainViewModel.filterDocs(i)},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(mainViewModel.dateState.filteredDate==i){
                             Color.Green
                        }else{Color.Gray}),
                    modifier = Modifier.padding(2.dp)
                    ) {
                    Text(
                        text = i,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun Datetime() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(true) {
        val job = Job()
        val coroutineScope = CoroutineScope(Dispatchers.Main + job)
        while (true) {
            delay(1000)
            coroutineScope.launch {
                currentTime = System.currentTimeMillis()
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Текущее время и дата:")
        Text(text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTime))
        Text(text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(currentTime))
    }
}

@Composable
fun Table(context: Context, mainViewModel: MainViewModel) {
    val docState = mainViewModel.docViewState
    Column(modifier = Modifier
        .padding(1.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .border(1.dp, Color.Black)
                .height(40.dp)
        ) {
            Cell(
                text = "Распределение", modifier = Modifier
                    .weight(2f)
                    .border(1.dp, Color.Black)
            )
            Cell(
                text = "Дата выезда", modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black)
            )
            Cell(
                text = "Транспорт", modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black)
            )
            Cell(
                text = "Точки сбора", modifier = Modifier
                    .weight(2f)
                    .border(1.dp, Color.Black)
            )
            Cell(
                text = "Точка назначения", modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black)
            )
        }
        if(docState.docs.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(docState.docs) { document ->
                    if(document.id!=0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Black)
                                .clickable {
                                    val intent = Intent(context, DocumentActivity::class.java)
                                    intent.putExtra("id", document.id.toString())
                                    context.startActivity(intent)
                                }
                                .height(45.dp)
                        ) {
                            Cell(
                                text = document.distribution,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(2f)
                                    .border(1.dp, Color.Black)
                            )
                            Cell(
                                text = document.departure_date,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(1.dp, Color.Black)
                            )
                            Cell(
                                text = document.transport,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(1.dp, Color.Black)
                            )
                            Cell(
                                text = document.venues,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(2f)
                                    .border(1.dp, Color.Black)
                            )
                            Cell(
                                text = document.destination,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(1.dp, Color.Black)
                            )
                        }
                    }
                }
            }
        }else{
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(text = "Документов распределения нет")
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Button(
                onClick = {
                    val intent = Intent(context, DocumentActivity::class.java)
                    intent.putExtra("id", "0")
                    context.startActivity(intent)
                }
            ) {
                Text(text = "Без документа распределения")
            }
        }
    }
}

@Composable
fun Cell(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .fillMaxHeight()
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
            color = Color.Black,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RFID(screen: String, context: Context) {
    var showDialog by remember { mutableStateOf(false) }
    var showDialog2 by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val mainViewModel = viewModel(modelClass = MainViewModel::class.java)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            showDialog = true
        }
    ) {
        val imageLoader = ImageLoader.Builder(LocalContext.current)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        Image(
            painter = rememberAsyncImagePainter(R.raw.rfid, imageLoader),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                //.background(Color.White)
        )
    }
    if(screen=="auth" || screen == "doc") {
        if (showDialog2) {
            Alert(onConfirm = { showDialog2 = false }, text = "Пропуск не найден")
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Введите номер пропуска") },
                text = {
                    Column {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    coroutineScope.launch {
                                        if (screen == "auth") {
                                            if (mainViewModel.getPass(inputText)) {
                                                context.startActivity(
                                                    Intent(
                                                        context,
                                                        MainActivity::class.java
                                                    )
                                                )
                                            } else {
                                                showDialog2 = true
                                            }
                                        } else {
                                            if (screen == "doc") {
                                                /////
                                            } else {
                                                /////
                                            }
                                        }
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (mainViewModel.getPass(inputText)) {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            MainActivity::class.java
                                        )
                                    )
                                    showDialog = false
                                    inputText = ""
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                } else {
                                    showDialog2 = true
                                }
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            inputText = ""
                        }
                    ) {
                        Text("Отмена")
                    }
                }

            )
            LaunchedEffect(Unit) {
                keyboardController?.show()
            }
        }
    }else{
        if (showDialog) {
            Alert(onConfirm = { showDialog = false }, text = "Сканер работает корректно")
        }
    }
}

@Composable
fun DrawerContent(title: String, menuItems: List<Pair<String, () -> Unit>>) {
    Column(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(0.2f)
        .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 16.dp)
            )
        menuItems.forEach { (title, action) ->
            Button(
                onClick = action,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(title)
            }
        }
    }
}