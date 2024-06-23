package com.example.uchet.viewModels

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.Departure
import com.example.uchet.entities.SotrWithDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class reportViewModel(private val repository: Repository = Graph.repository)  : ViewModel() {
    fun saveByDate(start: Date, end: Date,context: Context){
        viewModelScope.launch {
            val start2 = Date(start.time + 57600000)
            val end2 = Date(end.time + 57600000)
            val dep:List<Departure> = repository.readAllDeparture()
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
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(start2),
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(end2)
            )
            val valueRow: Row = sheet.createRow(1)
            for ((index, i) in value.withIndex()) {
                val cell: Cell = valueRow.createCell(index)
                cell.setCellValue(i)
            }
            val headers = arrayOf(
                "Имя",
                "Фамилия",
                "Отчество",
                "Номер пропуска",
                "Компания",
                "Пункт выезда по документу",
                "Фактический пункт выезда",
                "Рейс",
                "Дата",
                "Пункт назначения"
            )
            val headerRow: Row = sheet.createRow(2)
            for ((index, i) in headers.withIndex()) {
                val cell: Cell = headerRow.createCell(index)
                cell.setCellValue(i)
            }
            val datesBetween = mutableListOf<Date>()
            var currentDate = start2.time
            var indexRow = 3
            while (currentDate <= end2.time) {
                datesBetween.add(Date(currentDate))
                currentDate += 86400000 // Adding one day in milliseconds
            }
            Log.d("start",start2.toString())
            Log.d("end",end2.toString())
            Log.d("datesBetween",datesBetween.toString())
            datesBetween.forEach {
                val date = it.time + 57600000
                val date2 = date.let { Date(date) }
                var sotrList: MutableList<SotrWithDate> = mutableListOf()
                val doc = repository.readAllDocByDate(it).first()
                Log.d("docs",doc.toString())
                val docs = repository.readAllDocByDate(it).first()
                Log.d("doc", docs.toString())
                if(docs.isNotEmpty()){
                    docs.forEach{
                        Log.d("docID",it.id.toString())
                        val sotrudniki = repository.readTest2(it.id!!).first()
                        var sotrList2 = sotrudniki.map { sotrudnik ->
                            SotrWithDate(
                                id = sotrudnik.id,
                                id_sotrudnik = sotrudnik.id_sotrudnik,
                                id_doc = sotrudnik.id_doc,
                                id_venue_in_doc = sotrudnik.id_venue_in_doc,
                                id_venue_fact = sotrudnik.id_venue_fact,
                                route = sotrudnik.route,
                                mark = sotrudnik.mark,
                                name = sotrudnik.name,
                                surname = sotrudnik.surname,
                                patronymic = sotrudnik.patronymic,
                                available_in_doc = sotrudnik.available_in_doc,
                                uid = sotrudnik.uid,
                                date = it.departure_date,
                                destination = repository.getDeparture(it.id_destination),
                                company = sotrudnik.company
                            )
                        }

                        sotrList.addAll(sotrList2)
                        Log.d("listIn",sotrList.toString())
                        for ((rowIndex,sotrudnik) in sotrList.withIndex()){
                            val row: Row = sheet.createRow(rowIndex + indexRow)
                            row.createCell(0).setCellValue(sotrudnik.name)
                            row.createCell(1).setCellValue(sotrudnik.surname)
                            row.createCell(2).setCellValue(sotrudnik.patronymic)
                            row.createCell(3).setCellValue(sotrudnik.uid.toDouble())
                            row.createCell(4).setCellValue(sotrudnik.company)
                            row.createCell(5).setCellValue(dep.find {it.id == sotrudnik.id_venue_in_doc }?.name)
                            row.createCell(6).setCellValue(dep.find {it.id == sotrudnik.id_venue_fact }?.name)
                            row.createCell(7).setCellValue(sotrudnik.route)
                            row.createCell(8).setCellValue(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                                sotrudnik.date
                            ))
                            row.createCell(9).setCellValue(sotrudnik.destination)
                        }
                        Log.d("index",indexRow.toString())
                        indexRow = indexRow + sotrList.count()
                        Log.d("index",indexRow.toString())
                    }
                }
                //Log.d("list", sotrList.toString())
            }
            val values = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    "Отчет за " + SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                        start
                    ) + " - " + SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                        end
                    )
                )
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri =
                context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            uri?.let { outputStream ->
                context.contentResolver.openOutputStream(outputStream).use { fileOutputStream ->
                    workBook.write(fileOutputStream)
                }
                workBook.close()
            }
        }
    }
}
