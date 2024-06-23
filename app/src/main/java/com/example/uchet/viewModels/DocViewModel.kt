package com.example.uchet.viewModels

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.Departure
import com.example.uchet.entities.Sotrudnik
import com.example.uchet.entities.SotrudnikiInDocument
import com.example.uchet.entities.SotrudnikiWithDocFields
import com.example.uchet.entities.Transport
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DocViewModel(
    private val repository: Repository = Graph.repository
): ViewModel() {
    val value = repository.cardValue
    val page = repository.page
    fun changeValue(s:String){
        repository.changeCardValue(s)
    }
    fun changePage(s: String){
        repository.changePage(s)
    }
    var sotrudnikiState by mutableStateOf(SotrudnikiState())
        private set
    var sotrudnikiViewState by mutableStateOf(sotrudnikiState)
        private set
    var docState2 by mutableStateOf(DocState2())
        private set
    var infoState by mutableStateOf(InfoState())
        private set
    var sotrList by mutableStateOf(SotrList())
        private set
    var searchList by mutableStateOf(SotrList())
        private set
    var d by mutableStateOf(DepState())
        private set
    init {
        viewModelScope.launch {
            d = d.copy(repository.readAllDeparture())
        }
    }
    fun getDepById(id: Int): String? {
        return d.dep.find { it.id == id }?.name
    }
    fun getAllSotr(){
        viewModelScope.launch {
            repository.readAllSotrudnik.collect { sotr ->
                sotrList = sotrList.copy(
                    sotrList = sotr
                )
            }
        }
    }
    fun searchSotr(text: String){
        if(text!=null && text!="") {
            searchList = searchList.copy(
                sotrList = sotrList.sotrList.filter { employee ->
                    employee.name.startsWith(text, ignoreCase = true) ||
                            employee.surname.startsWith(text, ignoreCase = true) ||
                            employee.patronymic.startsWith(text, ignoreCase = true)
                },
                search = text
            )
        }else{
            searchList = searchList.copy(
                sotrList = sotrList.sotrList,
//                search = text
            )
        }
    }

    fun getDocState(id: Int){
        viewModelScope.launch {
            val doc = repository.getDocById(id)
            if(doc!=null) {
                if(id!=0) {
                    val venues_id = doc.id_venues.split(",").map { it.toInt() }
                    docState2 = docState2.copy(
                        id = id,
                        departure_date = doc.departure_date,
                        transport = listOf(Transport(doc.id_transport, repository.getTransportNameById(doc.id_transport))),
                        venues = venues_id.map { id -> Departure(id, repository.getDeparture(id)) },
                        destination = listOf(Departure(doc.id_destination,repository.getDeparture(doc.id_destination)))
                    )
                }else{
                    docState2 = docState2.copy(
                        id = id,
                        departure_date = Calendar.getInstance().time,
                        transport = repository.readAllTransport(),
                        venues = repository.readAllDeparture(),
                        destination = repository.readAllDeparture()
                    )
                }
            }
            getSotrudniki(docState2)
        }
    }

    private fun getSotrudniki(docState2: DocState2){
        viewModelScope.launch{
            repository.readTest(docState2.id!!).collectLatest { sotrudniki ->
                var sotrudnikiWithDocFields :List<SotrudnikiWithDocFields> = emptyList()
                if(sotrudniki.isNotEmpty()) {
                    sotrudnikiWithDocFields = sotrudniki.map { sotrudnik ->
                        SotrudnikiWithDocFields(
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
                            company = sotrudnik.company,
                            uid = sotrudnik.uid
                        )
                    }
                }else Log.d("s2", "empty")
                sotrudnikiState = sotrudnikiState.copy(
                    sotrudniki = sotrudnikiWithDocFields
                )
                sotrudnikiViewState = sotrudnikiViewState.copy(
                    sotrudniki = sotrudnikiState.sotrudniki
                )
                sort()
            }
        }

    }

    fun changeMark(mark: Boolean, id: String, id_venue: Int){
        viewModelScope.launch {
            if(sotrudnikiViewState.sotrudniki.any{ it.id_sotrudnik == id && it.available_in_doc}) {
                repository.changeMark(mark, id)
                repository.updateVenue(id, id_venue)
            }else{
                if(mark) {
                    val sotrudnik = repository.getByID(id)
                    if(sotrudnik!=null) {
                        repository.insertSotrInDoc(
                            SotrudnikiInDocument(
                                id_sotrudnik = sotrudnik.id,
                                id_doc = docState2.id!!,
                                id_venue_in_doc = 0,
                                id_venue_fact = id_venue,
                                route = "Рейс ",
                                mark = 1,
                                available_in_doc = 0
                            )
                        )
                    }
                }else{
                    repository.deleteSotrInDoc(id)
                }
            }
        }
        sort()
    }

    fun sort(){
        sotrudnikiViewState = sotrudnikiViewState.copy(
            sotrudniki = sotrudnikiViewState.sotrudniki.sortedWith(
                compareBy<SotrudnikiWithDocFields> { !it.available_in_doc }
                    .thenBy{ it.mark }
                    .thenBy{ it.surname }
            )
        )
    }

    fun getInfo(uid:Long){
        viewModelScope.launch{
            val sotrudnik = repository.getByUID(uid)
            if(sotrudnik!=null){
                infoState = infoState.copy(
                    uid = uid,
                    sotrudnik = sotrudnik
                )
            }else{
                infoState = infoState.copy(
                    uid = uid
                )
            }
        }
    }

    suspend fun getSotrInDoc(uid:Long): Int{
        return repository.getSotrInDoc(uid)
    }

    fun clearInfo(){
        infoState = infoState.copy(
            uid = 0,
            sotrudnik = null
        )
    }

    fun delete(uid: Long){
//        viewModelScope.launch {
//            repository.delete(uid)
//        }
    }

    fun clearAll(){
        viewModelScope.launch {
            repository.clearMarks(docState2.id!!)

            repository.delete(docState2.id!!)
        }
    }

    fun search(text: String){
        if(text!=null && text!="") {
            sotrudnikiViewState = sotrudnikiViewState.copy(
                search = text,
                sotrudniki = sotrudnikiViewState.sotrudniki.filter { employee ->
                    employee.name.startsWith(text, ignoreCase = true) ||
                            employee.surname.startsWith(text, ignoreCase = true) ||
                            employee.patronymic.startsWith(text, ignoreCase = true) }
            )
        }else{
            sotrudnikiViewState = sotrudnikiViewState.copy(
                sotrudniki = sotrudnikiState.sotrudniki
            )
        }
    }

    fun addSotr(sotrudnik: SotrudnikiInDocument){
        viewModelScope.launch {
            repository.insertSotrInDoc(sotrudnik)
        }
    }

    suspend fun getDeparture(id: Int){
        repository.getDeparture(id)
    }

    fun updateVenue(id: Int){
        docState2 = docState2.copy(
            selectedVenueId = id
        )
    }

    fun save(context: Context){
        viewModelScope.launch {
            val sotrudnikiList = sotrudnikiState.sotrudniki.filter { employee ->
                employee.mark == true
            }
            val dep:List<Departure> = repository.readAllDeparture()
            val workBook = XSSFWorkbook()
            val sheet = workBook.createSheet("Посаженные сотрудники")
            val info = arrayOf(
                "Пункт назначения", "Дата выезда", "Транспорт"
            )
            val infoRow: Row = sheet.createRow(0)
            for ((index, i) in info.withIndex()) {
                val cell: Cell = infoRow.createCell(index)
                cell.setCellValue(i)
            }
            val value = arrayOf(
                docState2.destination[0].name,
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(docState2.departure_date),
                docState2.transport[0].name
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
                "Рейс"
            )
            val headerRow: Row = sheet.createRow(2)
            for ((index, i) in headers.withIndex()) {
                val cell: Cell = headerRow.createCell(index)
                cell.setCellValue(i)
            }
            for ((rowIndex, sotrudnik) in sotrudnikiList.withIndex()) {
                val row: Row = sheet.createRow(rowIndex + 3)
                row.createCell(0).setCellValue(sotrudnik.name)
                row.createCell(1).setCellValue(sotrudnik.surname)
                row.createCell(2).setCellValue(sotrudnik.patronymic)
                row.createCell(3).setCellValue(sotrudnik.uid.toDouble())
                row.createCell(4).setCellValue(sotrudnik.company)
                row.createCell(5).setCellValue(dep.find{ it.id == sotrudnik.id_venue_in_doc}?.name )
                row.createCell(6).setCellValue(dep.find{ it.id == sotrudnik.id_venue_fact}?.name)
                row.createCell(7).setCellValue(sotrudnik.route)
            }
            val values = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    "Отчет за " + SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                        docState2.departure_date
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

data class SotrInDoc(
    var id: Int?,
    var id_sotrudnik: String,
    var id_doc: Int,
    var id_venue_in_doc: Int,
    var id_venue_fact: Int,
    var venue_in_doc: String,
    var venue_fact: String,
    var route: String,
    var mark: Boolean,
    var available_in_doc: Boolean,
    var name: String,
    var surname: String,
    var patronymic: String,
    var uid: Long,
)

data class DocState2(
    val id:Int?=null,
    val departure_date:Date?=null,
    val transport:List<Transport> = emptyList(),
    val venues: List<Departure> = emptyList(),
    val selectedVenueId: Int? = null,
    val destination: List<Departure> = emptyList(),
    //val name: String
)

data class InfoState(
    val uid: Long = 0,
    val sotrudnik: Sotrudnik? = null
)

data class SotrudnikiState(
    val sotrudniki:List<SotrudnikiWithDocFields> = emptyList(),
    val search:String?=null
)

data class SotrList(
    val sotrList: List<Sotrudnik> = emptyList(),
    val search: String?=null
)

data class DepState(
    val dep: List<Departure> = emptyList()
)

