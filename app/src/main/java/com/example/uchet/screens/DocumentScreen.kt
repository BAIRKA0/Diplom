package com.example.uchet.screens

import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uchet.activity.AuthActivity
import com.example.uchet.activity.MainActivity
import com.example.uchet.activity.SettingActivity
import com.example.uchet.viewModels.DocViewModel
import com.example.uchet.viewModels.reportViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

@Composable
fun DocumentScreen(
    context: Context,
    docId: Int
) {
    Log.d("docId",docId.toString())
    val docViewModel = viewModel(modelClass = DocViewModel::class.java)
    val reposrtViewModel = viewModel(modelClass = reportViewModel::class.java)
    docViewModel.getDocState(docId)
    docViewModel.getAllSotr()
    //docViewModel.sort()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showDialog by remember { mutableStateOf(false) }
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
            DocumentHeader(context, docViewModel,drawerState)
            Table2(docViewModel, context)
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
    val coroutineScope = rememberCoroutineScope()
    docViewModel.changePage("doc")
    coroutineScope.launch {
        docViewModel.value.collectLatest {
            if(docViewModel.value.value == "1482513293" && docViewModel.page.value == "doc"){
                docViewModel.getInfo(docViewModel.value.value.toLong())
                docViewModel.changeValue("")
            }
        }
    }
}

@Composable
fun DocumentHeader(context: Context, docViewModel: DocViewModel, drawerState: DrawerState) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            //.height(IntrinsicSize.Min)
            .fillMaxHeight(0.2f)
            .background(color = Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Burger(drawerState)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                onClick = {
                    showDialog = true
                },
                title = "Добавить вручную",
                modifier = Modifier.padding(bottom = 4.dp),
            )
            SearchField(docViewModel)
        }
        DocInfo(docViewModel)
        SotrInfo(docViewModel)
        RFID("doc", context)
    }
    if (showDialog) {
        AddSotrDialog(
            onConfirm = { showDialog = false },
            docViewModel = docViewModel
        )
    }
}

@Composable
fun AddSotrDialog(
    onConfirm: () -> Unit,
    docViewModel: DocViewModel
){
    var fio by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { },
        title = {
            Column(){
                TextField(
                    value = fio,
                    onValueChange = { newText ->
                        fio = newText
                        docViewModel.searchSotr(newText)
                    },
                    label = { Text("ФИО") },
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(end = 6.dp)
                )
                LazyColumn(
                    modifier = Modifier.height(120.dp)
                ){
                    items(docViewModel.searchList.sotrList){ sotrudnik ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Cell(
                                text = sotrudnik.surname + " " +sotrudnik.name + " " + sotrudnik.patronymic + " №" + sotrudnik.uid,
                                modifier = Modifier
                                    .border(1.dp, Color.Black)
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clickable {
                                        docViewModel.getInfo(sotrudnik.uid)
                                    }
                            )
                        }
                    }
                }
            }
                },
        confirmButton = {
            CustomButton(
                onClick = {
                    onConfirm()
                },
                title = "Закрыть"
            )
        }
    )
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
                    Text(text = selectedItem, maxLines = 1)
                    Icon(icon, "")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(160.dp)
                        .heightIn(max = 200.dp)
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
            if(docViewModel.docState2.id==0) {
                Column {
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.Black)
                            .width(200.dp)
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
                        modifier = Modifier
                            .width(200.dp)
                            .heightIn(max = 200.dp)
                    ) {
                        docViewModel.docState2.transport.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(text = label.name) },
                                onClick = {
                                    selectedItem = label.name
                                    expanded = false
                                })
                        }
                    }
                }
            }else{
                if(docViewModel.docState2.transport.isNotEmpty())
                    Text(text = docViewModel.docState2.transport[0].name)
                else
                    Text(text = "Пусто")
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
                        modifier = Modifier
                            .width(160.dp)
                            .heightIn(max = 200.dp)
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
    Column(
        modifier = Modifier
            .border(1.dp, Color.Black)
            .fillMaxHeight(0.9f)
            .fillMaxWidth(0.5f)
            .padding(5.dp)
    ){
        if(infoState.uid!=null) {
            Text(text = "Пропуск: №" + infoState.uid,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1)
            if (infoState.sotrudnik != null) {
                val fio = "ФИО: " + infoState.sotrudnik.surname + " " + infoState.sotrudnik.name + " " + infoState.sotrudnik.patronymic
                val org = "Организация: " + infoState.sotrudnik.company
                Text(text = fio,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1)
                Text(text = org,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    CustomButton(
                        onClick = {
                            docViewModel.changeMark(false,infoState.sotrudnik.id,0)
                            docViewModel.clearInfo()
                                  },
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        title = "отмена",
                        buttonType = ButtonTypes.Negative,
                        fontSize = 10.sp
                    )
                    CustomButton(
                        onClick = {
                            docViewModel.changeMark(true,infoState.sotrudnik.id, if(docViewModel.docState2.selectedVenueId!=null){docViewModel.docState2.selectedVenueId!!}else{0})
                            docViewModel.clearInfo()
                                  },
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        title = "принять",
                        fontSize = 10.sp
                    )
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
                            if (!sotrudnik.mark || docViewModel.docState2.id == 0) {
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
                    var venue = if(sotrudnik.id_venue_fact == 0) {
                        docViewModel.getDepById(sotrudnik.id_venue_in_doc)
                    } else {
                        if(sotrudnik.available_in_doc) {
                            docViewModel.getDepById(sotrudnik.id_venue_in_doc) + " / " + docViewModel.getDepById(sotrudnik.id_venue_fact)
                        }else{
                            docViewModel.getDepById(sotrudnik.id_venue_fact)
                        }
                    }
                    Cell(
                        text = venue!!, modifier = Modifier
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
            CustomButton(onClick = {
                showDialog = true
                f = true
            },
                title = "Очистить",
                buttonType = ButtonTypes.Negative)
            CustomButton(
                onClick = {
                    showDialog = true
                    f = false
                },
                modifier = Modifier
                    .padding(start = 10.dp),
                title="Сформировать"
            )
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
            CustomButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                title = "Да"
            )
        },
        dismissButton = {
            CustomButton(
                onClick = {
                    onDismiss()
                },
                title = "Нет",
                buttonType = ButtonTypes.Negative
            )
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
        placeholder = { Text(text = "Поиск по ФИО") },
        modifier = Modifier
            .fillMaxWidth(0.3f)
    )
}