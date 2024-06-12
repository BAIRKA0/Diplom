package com.example.uchet.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
import com.example.uchet.activity.SettingActivity
import com.example.uchet.entities.RFIDStates
import com.example.uchet.viewModels.MainViewModel
import com.example.uchet.viewModels.RfidViewModel
import com.example.uchet.viewModels.reportViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(
    context: Context
) {
    val mainViewModel = viewModel(modelClass = MainViewModel::class.java)
    val reposrtViewModel = viewModel(modelClass = reportViewModel::class.java)
    mainViewModel.getDateList()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showDialog by remember { mutableStateOf(false) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent("Список документов",menuItems = listOf(
                "Настройки" to {
                    context.startActivity(Intent(context, SettingActivity::class.java))
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
            Header(mainViewModel,drawerState, context)
            Table(context, mainViewModel)
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

                CustomButton(title = "Сформировать", onClick = {
                    reposrtViewModel.saveByDate(
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
fun Header(mainViewModel: MainViewModel, drawerState: DrawerState, context: Context) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(color = Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Burger(drawerState)
        Filters(mainViewModel)
        Datetime()
        RFID("main", context)
    }
}
@Composable
fun Burger(drawerState: DrawerState) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()
    val coroutineScope = rememberCoroutineScope()
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
                CustomButton(onClick = {
                    mainViewModel.filterDocs(i)},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(mainViewModel.dateState.filteredDate==i){
                            Color(0xff81E95C)
                        }else{Color.Gray}),
                    modifier = Modifier.padding(2.dp),
                    title = i,
                    fontSize = 18.sp
                    )
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
            CustomButton(
                onClick = {
                    val intent = Intent(context, DocumentActivity::class.java)
                    intent.putExtra("id", "0")
                    context.startActivity(intent)
                },
                title="Без документа распределения"
            )
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
@SuppressLint("ResourceType")
@Composable
fun RFID(screen: String, context: Context) {
    val rfidViewModel = viewModel(modelClass = RfidViewModel::class.java)
//    var rfidBleManager = RfidBleManager(context)

    val rfidColor = mutableStateOf(Color(0xffFF3333))
    Log.d("currentState", rfidViewModel.state.currentState.toString())

    rfidColor.value = when (rfidViewModel.state.currentState) {
        RFIDStates.RFID_CONNECTED -> Color(0xff81E95C)
        RFIDStates.RFID_DISCONNECTED -> Color(0xffFF3333)
        RFIDStates.RFID_PROCESSING -> Color(0xffE4B91E)
    }
    Column(
        modifier = Modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(id = R.drawable.rfid),
            "Индикация RFID",
            Modifier.width(96.dp),
            tint = rfidColor.value
        )
    }

}
@Composable
fun DatePeriodPickers(startDate: MutableState<LocalDate>, endDate: MutableState<LocalDate>) {
    Column {
        Text(text = "Настройка отчетного периода")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

            val startDateDialogState = rememberMaterialDialogState()
            val endDateDialogState = rememberMaterialDialogState()

            Box(
                modifier = Modifier
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .height(56.dp)
                    .padding(8.dp)
                    .clickable {
                        startDateDialogState.show()
                    }
            ) {
                Text(modifier = Modifier
                    .align(Alignment.Center),
                    text = startDate.value.format(dateFormatter) ?: "Выберите",
                    fontSize = 20.sp)
            }
            Text(modifier = Modifier
                .align(Alignment.CenterVertically),
                text = "-",
                fontSize = 24.sp)
            Box(
                modifier = Modifier
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .height(56.dp)
                    .padding(8.dp)
                    .clickable {
                        endDateDialogState.show()
                    }
            ) {
                Text(modifier = Modifier
                    .align(Alignment.Center),
                    text = endDate.value.format(dateFormatter) ?: "Выберите",
                    fontSize = 20.sp)
            }

            MaterialDialog(
                dialogState = startDateDialogState,
                buttons = {
                    positiveButton("Подтвердить")
                    negativeButton("Отмена")
                },
                border = BorderStroke(1.dp, Color.Black)
            ) {
                datepicker(
                    initialDate = startDate.value,
                    title = "Выберите начальную дату",
                    allowedDateValidator = {
                        it <= endDate.value
                    }
                ) {
                    startDate.value = it
                }
            }

            MaterialDialog(
                dialogState = endDateDialogState,
                buttons = {
                    positiveButton("Подтвердить")
                    negativeButton("Отмена")
                },
                border = BorderStroke(1.dp, Color.Black)
            ) {
                datepicker(
                    initialDate = endDate.value,
                    title = "Выберите конечную дату",
                    waitForPositiveButton = false,
                    allowedDateValidator = {
                        it >= startDate.value && it <= LocalDate.now()
                    }
                ) {
                    endDate.value = it
                }
            }
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
            fontSize = 24.sp,
            modifier = Modifier
                .padding(top = 16.dp),
            textAlign = TextAlign.Center
            )
        menuItems.forEach { (title, action) ->
            CustomButton(
                onClick = action,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                title = title,
                buttonType = ButtonTypes.Neutral,
                fontSize = 18.sp
            )
        }
    }
}


fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}