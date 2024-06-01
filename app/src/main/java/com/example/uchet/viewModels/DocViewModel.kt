package com.example.uchet.viewModels

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
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
import java.util.Calendar
import java.util.Date

class DocViewModel(
    private val repository: Repository = Graph.repository
): ViewModel() {

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
            repository.readAllSotrInDoc(docState2.id!!).collectLatest { sotrudniki ->
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
                            uid = sotrudnik.uid
                        )
                    }
                }
                sotrudnikiState = sotrudnikiState.copy(
                    sotrudniki = sotrudnikiWithDocFields
                )
                sotrudnikiViewState = sotrudnikiViewState.copy(
                    sotrudniki = sotrudnikiState.sotrudniki
                )
            }
        }
    }

    fun changeMark(mark: Boolean, id: Int, id_venue: Int){
        viewModelScope.launch {
            repository.changeMark(mark, id)
            repository.updateVenue(id,id_venue)
        }
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

    fun updateVenue(id: Int){
        docState2 = docState2.copy(
            selectedVenueId = id
        )
    }

    fun save(context: Context){
        val sotrudnikiList = sotrudnikiState.sotrudniki.filter { employee ->
            employee.mark==true
        }
        val workBook = XSSFWorkbook()
        val filename = "По распределению от <дата>"
        val sheet = workBook.createSheet("Посаженные сотрудники")
        val headers = arrayOf(
             "Имя", "Фамилия", "Отчество", "uid", "company", "id_venue_fact", "route"
        )
        val headerRow: Row = sheet.createRow(0)
        for ((index, header) in headers.withIndex()) {
            val cell: Cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }
        for ((rowIndex, sotrudnik) in sotrudnikiList.withIndex()) {
            val row: Row = sheet.createRow(rowIndex + 1)
            row.createCell(0).setCellValue(sotrudnik.name)
            row.createCell(1).setCellValue(sotrudnik.surname)
            row.createCell(2).setCellValue(sotrudnik.patronymic)
            row.createCell(3).setCellValue(sotrudnik.uid.toDouble())
            row.createCell(4).setCellValue(sotrudnik.id_venue_fact.toDouble())
            row.createCell(5).setCellValue(sotrudnik.route)
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "example.xlsx")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        uri?.let { outputStream ->
            context.contentResolver.openOutputStream(outputStream).use { fileOutputStream ->
                workBook.write(fileOutputStream)
            }
            workBook.close()
        }
    }
}

data class DocState2(
    val id:Int?=null,
    val departure_date:Date?=null,
    val transport:List<Transport> = emptyList(),
    val venues: List<Departure> = emptyList(),
    val selectedVenueId: Int? = null,
    val destination: List<Departure> = emptyList()
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