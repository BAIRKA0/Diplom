package com.example.uchet.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uchet.activity.AuthActivity
import com.example.uchet.activity.MainActivity
import com.example.uchet.activity.SettingActivity
import com.example.uchet.viewModels.DocViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DocumentScreen(
    context: Context,
    docId: Int
) {
    val docViewModel = viewModel(modelClass = DocViewModel::class.java)
    docViewModel.getDocState(docId)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent("Сотрудники в документе",menuItems = listOf(
                "Список документов" to {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
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
            DocumentHeader(context, docViewModel,drawerState)
            Table2(docViewModel, context)
        }
    }
}

@Composable
fun DocumentHeader(context: Context, docViewModel: DocViewModel, drawerState: DrawerState){
    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(color = Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ){
        Burger(drawerState)
        DocInfo(docViewModel)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
//                docViewModel.addSotr(SotrudnikiInDocument(
//                    id_sotrudnik = java.util.Random().nextInt(10)+1,
//                    id_venue_fact = docViewModel.docState2.selectedVenueId ?: 1,
//                    id_venue_in_doc = java.util.Random().nextInt(10)+1,
//                    id_doc = docViewModel.docState2.id!!,
//                    route = "",
//                    mark = 1,
//                    available_in_doc = 0
//                ))
            }) {
                Text(text = "Добавить вручную",modifier = Modifier.padding(bottom = 4.dp))
            }
            SearchField(docViewModel)
        }
        SotrInfo(docViewModel)
        RFID("doc",context)
    }
}

@Composable
fun DocInfo(docViewModel: DocViewModel){
    Column (
        horizontalAlignment = Alignment.Start
    ){
        if(docViewModel.docState2.departure_date!=null) {
            Text(
                text = "Дата выезда: " + SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(docViewModel.docState2.departure_date),
                modifier = Modifier.padding(bottom =  2.dp)
            )
        }else{
            Text(
                text = "Дата выезда: "
            )
        }
        Row( verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(2.dp)
        ){
            Text(text = "Пункт выезда:")
            var expanded by remember { mutableStateOf(false) }
            var selectedItem by remember { mutableStateOf("Выберите пункт") }
            val icon = if(expanded){
                Icons.Filled.KeyboardArrowUp
            }else{
                Icons.Filled.KeyboardArrowDown
            }
            Column{
                Row( modifier = Modifier
                    .border(1.dp, Color.Black)
                    .width(160.dp)
                    .padding(2.dp)
                    .clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = selectedItem)
                    Icon(icon, "")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(160.dp).heightIn(max = 200.dp)
                ) {
                    docViewModel.docState2.venues.forEach { label ->
                        DropdownMenuItem(
                            text  = { Text(text = label.name) },
                            onClick = {
                                selectedItem = label.name
                                docViewModel.updateVenue(label.id!!)
                                expanded= false
                            })
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =  Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(2.dp)
        ){
            Text(text = "Транспорт:")
            var expanded by remember { mutableStateOf(false) }
            var selectedItem by remember { mutableStateOf("Выберите транспорт") }
            val icon = if(expanded){
                Icons.Filled.KeyboardArrowUp
            }else{
                Icons.Filled.KeyboardArrowDown
            }
            Column{
                Row( modifier = Modifier
                    .border(1.dp, Color.Black)
                    .width(200.dp)
                    .padding(2.dp)
                    .clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = selectedItem)
                    Icon(icon, "")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(200.dp).heightIn(max = 200.dp)
                ) {
                    docViewModel.docState2.transport.forEach { label ->
                        DropdownMenuItem(
                            text  = { Text(text = label.name) },
                            onClick = {
                                selectedItem = label.name
                                expanded= false
                            })
                    }
                }
            }
        }
        Row( verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp)
        ){
            Text(text = "Пункт назначения:")
            if(docViewModel.docState2.id==0) {
                var expanded by remember { mutableStateOf(false) }
                var selectedItem by remember { mutableStateOf("Выберите пункт") }
                val icon = if (expanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                }
                Column {
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.Black)
                            .width(160.dp)
                            .padding(2.dp)
                            .clickable { expanded = !expanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedItem)
                        Icon(icon, "")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(160.dp).heightIn(max = 200.dp)
                    ) {
                        docViewModel.docState2.venues.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(text = label.name) },
                                onClick = {
                                    selectedItem = label.name
                                    docViewModel.updateVenue(label.id!!)
                                    expanded = false
                                })
                        }
                    }
                }
            }else{
                if(docViewModel.docState2.destination.isNotEmpty())
                    Text(text = docViewModel.docState2.destination[0].name)
                else
                    Text(text = "Пусто")
            }
        }
    }
}

@Composable
fun SotrInfo(docViewModel: DocViewModel){
    val infoState = docViewModel.infoState
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .border(1.dp, Color.Black)
            .fillMaxHeight(0.9f)
            .fillMaxWidth(0.5f)
            .padding(5.dp)
    ){
        if(infoState.uid!=null) {
            Text(text = "Пропуск: №" + infoState.uid)
            if (infoState.sotrudnik != null) {
                val fio = "ФИО: " + infoState.sotrudnik.surname + " " + infoState.sotrudnik.name + " " + infoState.sotrudnik.patronymic
                val org = "Организация: " + infoState.sotrudnik.company
                val maxTextLength = 28
                Text(text = fio,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth())
                Text(text = org,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { docViewModel.changeMark(true,infoState.sotrudnik.id.toInt(), if(docViewModel.docState2.selectedVenueId!=null){docViewModel.docState2.selectedVenueId!!}else{0}) },
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text(text = "Принять")
                    }
                    Button(
                        onClick = {
                            docViewModel.clearInfo()
                            docViewModel.changeMark(false,infoState.sotrudnik.id.toInt(),0)
//                            coroutineScope.launch {
//                                if(docViewModel.getSotrInDoc(infoState.sotrudnik.uid!!)>0)
//
//                                else
//                                    docViewModel.delete(infoState.sotrudnik.uid)
//                            }
                                  },
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "Отклонить")
                    }
                }
            } else {
                Text(text = "Сотрудник не найден")
            }
        }
    }

}

@Composable
fun Table2(docViewModel: DocViewModel,context: Context){
    val sotrState = docViewModel.sotrudnikiViewState
    var showDialog by remember { mutableStateOf(false) }
    var f by remember { mutableStateOf(false) }
    if (showDialog) {
        if(f) {
            ConfirmationDialog(
                onDismiss = {
                    showDialog = false
                },
                onConfirm = {
                    docViewModel.clearAll()
                },
                text = "Очистить таблицу?"
            )
        }else{
            ConfirmationDialog(
                onDismiss = {
                    showDialog = false
                },
                onConfirm = {
                    docViewModel.save(context)
                },
                text = "Сформировать отчет?"
            )
        }
    }
    Column(modifier = Modifier.padding(1.dp)) {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .border(1.dp, Color.Black)
                .height(40.dp)
        ) {
            Cell(
                text = "ФИО", modifier = Modifier
                    .weight(3f)
                    .border(1.dp, Color.Black)
            )
            Cell(
                text = "Точка сбора", modifier = Modifier
                    .weight(2f)
                    .border(1.dp, Color.Black)
            )
            Cell(
                text = "Рейс", modifier = Modifier
                    .weight(2f)
                    .border(1.dp, Color.Black)
            )
            Cell(
                text = "Отметка", modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Black)
            )
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            items(sotrState.sotrudniki) { sotrudnik ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black)
                        .height(40.dp)
                        .background(
                            if (!sotrudnik.mark||docViewModel.docState2.id==0) {
                                Color.White
                            } else {
                                if (!sotrudnik.available_in_doc) {
                                    Color.Red
                                } else {
                                    if (sotrudnik.id_venue_fact != sotrudnik.id_venue_in_doc) {
                                        Color(0xff03a9f4)
                                    } else {
                                        Color.Green
                                    }
                                }
                            }
                        )
                ) {
                    val fio = sotrudnik.surname + " " +sotrudnik.name + " " + sotrudnik.patronymic + " №" + sotrudnik.uid
                    val max = 40
                    Cell(
                        text = if(fio.length>max)fio.substring(0,max) + "..." else fio,
                        modifier = Modifier
                            .weight(3f)
                            .border(1.dp, Color.Black)
                            .height(40.dp)
                            .clickable {
                                docViewModel.getInfo(sotrudnik.uid)
                            }
                    )
                    Cell(
                        text = sotrudnik.id_venue_fact.toString(), modifier = Modifier
                            .weight(2f)
                            .border(1.dp, Color.Black)
                            .height(40.dp)
                    )
                    Cell(
                        text = sotrudnik.route, modifier = Modifier
                            .weight(2f)
                            .border(1.dp, Color.Black)
                            .height(40.dp)
                    )
                    Checkbox(
                        checked = sotrudnik.mark,
                        onCheckedChange = {
                            if(!sotrudnik.mark)
                                docViewModel.changeMark(false,sotrudnik.id!!,if(docViewModel.docState2.selectedVenueId!=null){docViewModel.docState2.selectedVenueId!!}else{0})
                            else
                                docViewModel.changeMark(true,sotrudnik.id!!,0)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Black)
                            .height(40.dp)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Button(onClick = {
                showDialog = true
                f = true
            }) {
                Text(text = "Очистить")
            }
            Button(
                onClick = {
                    showDialog = true
                    f = false
                },
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                Text(text = "Сформировать")
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    text:String
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text) },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text("Да")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Нет")
            }
        }
    )
}

@Composable
fun SearchField(docViewModel: DocViewModel){
    var searchQuery by remember { mutableStateOf("") }
    TextField(
        value = searchQuery,
        onValueChange = {newText ->
            docViewModel.search(newText)
            searchQuery = newText
        },
        placeholder = { Text(text = "поиск") },
        modifier = Modifier
            .fillMaxWidth(0.3f)
    )
}