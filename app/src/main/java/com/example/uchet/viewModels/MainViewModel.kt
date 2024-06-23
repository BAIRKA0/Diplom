package com.example.uchet.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.DocWithFields
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MainViewModel(
    private val repository: Repository = Graph.repository
): ViewModel() {
    init {
        try {
            getAllDocWithFields()
        } catch (e: Exception) {
            Log.e("GetAllDoc", "Error", e)
        }
    }
    var docState by mutableStateOf(DocState())
        private set

    var dateState by mutableStateOf(DateState())
        private set

    var docViewState by mutableStateOf(docState)
        private set

    fun getDateList(){
        val currentDate = Calendar.getInstance()
        val dateList = mutableListOf<String>()
        currentDate.add(Calendar.DAY_OF_YEAR, -1)
        dateList.add(
            SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            ).format(currentDate.time)
        )
        for (i in 1..4) {
            currentDate.add(Calendar.DAY_OF_YEAR, 1)
            dateList.add(
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(currentDate.time)
            )
        }
        dateState = dateState.copy(
            dateList = dateList
        )
    }

    fun getAllDocWithFields(){
        viewModelScope.launch {
            repository.readAllDoc.collectLatest { documents ->
                val docWithFieldsList = documents.map { document ->
                    val transportName = repository.getTransportNameById(document.id_transport) ?: "Unknown"
                    val vanues = document.id_venues.split(",").map {it.toInt()}.map { repository.getDeparture(it)}.joinToString()
                    //val venuesNames = repository.getVenuesNamesByIds(document.id_venues)

                    DocWithFields(
                        id = document.id,
                        distribution = document.distribution,
                        departure_date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(document.departure_date),
                        transport = transportName,
                        id_vanues = document.id_venues,
                        venues = vanues,
                        destination = repository.getDeparture(document.id_destination)
                    )
                }
                val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val sortedDocs = docWithFieldsList.sortedBy { LocalDate.parse(it.departure_date, dateFormatter) }
                docState = docState.copy(
                    docs = sortedDocs
                )
                docViewState = docViewState.copy(
                    docs = docState.docs
                )
            }
        }
    }

    fun filterDocs(date: String){
        if (dateState.filteredDate==""){
            docViewState = docViewState.copy(
                docs = docViewState.docs.filter { doc ->
                    doc.departure_date == date
                }
            )
            dateState = dateState.copy(
                filteredDate = date
            )
        }
        else{

            if(dateState.filteredDate==date) {
                dateState = dateState.copy(
                    filteredDate = ""
                )
                docViewState = docViewState.copy(
                    docs = docState.docs
                )
            }else{
                dateState = dateState.copy(
                    filteredDate = date
                )
                docViewState = docViewState.copy(
                    docs = docState.docs
                )
                docViewState = docViewState.copy(
                    docs = docViewState.docs.filter { doc ->
                        doc.departure_date == date
                    }
                )
            }
        }
    }

    suspend fun getPass(passId: String): Boolean{
        if(repository.getAccByPass(passId)>0)
            return true
        else
            return false
    }
}

data class DocState(
    val docs:List<DocWithFields> = emptyList()
)

data class DateState(
    val dateList: List<String> = emptyList(),
    val filteredDate: String = ""
)