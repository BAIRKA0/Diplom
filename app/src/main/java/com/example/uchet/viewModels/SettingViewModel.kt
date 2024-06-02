package com.example.uchet.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.Departure
import com.example.uchet.entities.Document
import com.example.uchet.entities.SotrudnikiInDocument
import com.example.uchet.entities.Transport
import com.example.uchet.network.PersonsFetcher
import com.example.uchet.network.asModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Random

class SettingViewModel(
    private val repository: Repository = Graph.repository
): ViewModel() {
    fun updateAdminPassword(){

    }
    private val _isDownloadOpen = MutableStateFlow(false)
    val isDownloadOpen: StateFlow<Boolean> get() = _isDownloadOpen
    private val _page = MutableStateFlow(1)
    val page: StateFlow<Int> get() = _page
    fun changePage(i: Int) {
        _page.value = i
    }
    fun showDownload() {
        _isDownloadOpen.value = true
    }
    fun hideDownload() {
        _isDownloadOpen.value = false
    }
    fun updateSotrudnik(){
        viewModelScope.launch {
            try{
                showDownload()
                val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val currentDateTime = LocalDateTime.now().format(dateFormatter)
                var toastMsg = "Обновление завершено"
                var updated = false
                var lastUpdate = ""
                val deviceId = "233566627400021" //dataStore.getUniqueId.first()

                while (true) {
                    if (deviceId.isEmpty()) {
                        toastMsg = "ВЖК не установлен"
                        Log.d("DEBUG", "VZHK ")
                        return@launch
                    }
                    var isFirstFetch = false
                    if(lastUpdate == "") isFirstFetch = true else isFirstFetch = false
                    val newData = PersonsFetcher.getLaundryPersonsWithPasses(
                        deviceId,
                        isFirstFetch
                    )
                    if (newData == null) {
                        throw Exception()
                    }
                    if (newData.passes.isEmpty()) {
                        updated = true
                        toastMsg = "Данные уже актуальны"
                        return@launch
                    }
                    Graph.repository.insertSotrudniks(newData.passes.mapNotNull { it.asModel() })
                    lastUpdate = currentDateTime.toString()
                    PersonsFetcher.confirmGetPersonsWithPasses(
                        deviceId,
                        newData.maxTransferredRevision
                    )
                    if (newData.isFullySynchronized) {
                        updated = true
                        Napier.e("launch finish")
                        hideDownload()
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.d("ApiError",e.toString())
            }
        }
    }

    fun updateDoc(){
        viewModelScope.launch {
            if(repository.getDepartureRow()==0){
                for (i in 1..20){
                    repository.insertDeparture(
                        Departure(
                            name = "Точка "+i
                        )
                    )
                }
            }
            if(repository.getTransportRow()==0){
                for (i in 1..10){
                    repository.insertTransport(
                        Transport(
                            name = "Транспорт "+i
                        )
                    )
                }
            }
            if(repository.getDocRow()==0){
                for (i in 1..30) {
                    repository.insertDocument(
                        Document(
                            distribution = "Распределение " + i,
                            departure_date = Date(2024-1900,4,i),
                            id_transport = Random().nextInt(10)+1,
                            id_destination = Random().nextInt(20)+1,
                            id_venues = (Random().nextInt(20)+1).toString()+","+(Random().nextInt(20)+1)+","+(Random().nextInt(20)+1)
                        )
                    )
                }
                repository.insertDocument(
                    Document(
                        id = 0,
                        distribution = "Без распределения",
                        departure_date = Date(2024-1900,2-1,9),
                        id_transport = 1,
                        id_destination = 1,
                        id_venues = "1,2,3"
                    )
                )
            }
            if(repository.getSotrInDocRow()==0){
                for (i in 1..30) {
                    val ids = repository.getIds(30)
                    Log.e("ids "+i,ids.toString())
                    for (j in 1..30) {
                        repository.insertSotrInDoc(
                            SotrudnikiInDocument(
                                id_sotrudnik = ids.get(j),
                                id_doc = Random().nextInt(30)+1,
                                id_venue_in_doc = Random().nextInt(20)+1,
                                id_venue_fact = 0,
                                route = "Рейс ",
                                mark = 0,
                                available_in_doc = 1
                            )
                        )
                    }
                }
            }
        }
    }
}