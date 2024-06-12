package com.example.uchet.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.SotrWithDate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class reportViewModel(private val repository: Repository = Graph.repository)  : ViewModel() {
    fun saveByDate(start: Date, end: Date,context: Context){
        val workBook = XSSFWorkbook()
        val sheet = workBook.createSheet("Посаженные сотрудники")
        val info = arrayOf(
            "Начальная дата", "Конечная дата"
        )
        val infoRow: Row = sheet.createRow(0)
        for ((index, i) in info.withIndex()) {
            val cell: Cell = infoRow.createCell(index)
            cell.setCellValue(i)
        }
        val value = arrayOf(
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(start),
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(end)
        )
        val valueRow: Row = sheet.createRow(1)
        for ((index, i) in value.withIndex()) {
            val cell: Cell = valueRow.createCell(index)
            cell.setCellValue(i)
        }
        val headers = arrayOf(
            "Имя", "Фамилия", "Отчество", "Номер пропуска", "Компания", "Пункт выезда", "Рейс","Дата","Пункт назначения"
        )
        val headerRow: Row = sheet.createRow(2)
        for ((index, i) in headers.withIndex()) {
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(i)
        }
        val datesBetween = mutableListOf<Date>()
        var currentDate = start.time
        var indexRow = 3
        while (currentDate <= end.time) {
            datesBetween.add(Date(currentDate))
            currentDate += 86400000 // Adding one day in milliseconds

        }
        datesBetween.forEach {
            Log.d("date", SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            ).format(it))
            Log.d("date", it.time.toString())
            val job = viewModelScope.launch {
                //var documents :List<Document> = emptyList()

                var sotrList: MutableList<SotrWithDate>  = mutableListOf()
                repository.readAllDocByDate(it).collectLatest { docs ->
                    Log.d("doc",docs.toString())
//                    if(docs.isNotEmpty()){
//                        docs.forEach{
//                            Log.d("docID",it.id.toString())
//                            viewModelScope.launch {
//                                repository.readTest(it.id!!).collectLatest { sotrudniki ->
//                                    var sotrList2 = sotrudniki.map { sotrudnik ->
//                                        SotrWithDate(
//                                            id = sotrudnik.id,
//                                            id_sotrudnik = sotrudnik.id_sotrudnik,
//                                            id_doc = sotrudnik.id_doc,
//                                            id_venue_in_doc = sotrudnik.id_venue_in_doc,
//                                            id_venue_fact = sotrudnik.id_venue_fact,
//                                            route = sotrudnik.route,
//                                            mark = sotrudnik.mark,
//                                            name = sotrudnik.name,
//                                            surname = sotrudnik.surname,
//                                            patronymic = sotrudnik.patronymic,
//                                            available_in_doc = sotrudnik.available_in_doc,
//                                            uid = sotrudnik.uid,
//                                            date = it.departure_date,
//                                            destination = repository.getDeparture(it.id_destination)
//                                        )
//                                    }
//                                    sotrList.addAll(sotrList2)
//                                    Log.d("listIn",sotrList.toString())
////                                    for ((rowIndex,sotrudnik) in sotrList.withIndex()){
////                                        val row: Row = sheet.createRow(rowIndex + indexRow)
////                                        row.createCell(0).setCellValue(sotrudnik.name)
////                                        row.createCell(1).setCellValue(sotrudnik.surname)
////                                        row.createCell(2).setCellValue(sotrudnik.patronymic)
////                                        row.createCell(3).setCellValue(sotrudnik.uid.toDouble())
////                                        row.createCell(4).setCellValue(sotrudnik.id_venue_fact.toDouble())
////                                        row.createCell(5).setCellValue(sotrudnik.route)
////                                        row.createCell(5).setCellValue(sotrudnik.date)
////                                        row.createCell(5).setCellValue(sotrudnik.destination)
////                                    }
////                                    Log.d("index",indexRow.toString())
////                                    indexRow = indexRow + sotrList.count()
//                                }
//                            }
//                        }
//                    }
                }
                Log.d("list",sotrList.toString())
            }

        }
    }
}
