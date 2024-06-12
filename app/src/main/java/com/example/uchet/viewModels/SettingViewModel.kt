package com.example.uchet.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.Acc
import com.example.uchet.entities.Departure
import com.example.uchet.entities.Document
import com.example.uchet.entities.SotrudnikiInDocument
import com.example.uchet.entities.Transport
import com.example.uchet.network.PersonsFetcher
import com.example.uchet.network.asModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Random

class SettingViewModel(
    private val repository: Repository = Graph.repository
): ViewModel() {
    private val _isDownloadOpen = MutableStateFlow(false)

    var AccState by mutableStateOf(AccState())
        private set

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
    private val _lastUpdate = MutableStateFlow("")
    val lastUpdate: StateFlow<String> get() = _lastUpdate

    private val _lastUpload = MutableStateFlow("")
    val lastUpload: StateFlow<String> get() = _lastUpload

    private val _url = MutableStateFlow("")
    val url: StateFlow<String> get() = _url

    private val _adminPass = MutableStateFlow("")
    val adminPass: StateFlow<String> get() = _adminPass

    private val _uniqueId = MutableStateFlow("")
    val uniqueId: StateFlow<String> get() = _uniqueId

    fun updateLastUpdate(newLastUpdate: String) { _lastUpdate.value = newLastUpdate }
    fun updateLastUpload(newLastUpload: String) { _lastUpload.value = newLastUpload }
    fun updateUrl(newUrl: String) { _url.value = newUrl }
    fun updateAdminPass(newAdminPass: String) { _adminPass.value = newAdminPass }
    fun updateUniqueId(newUniqueId: String) { _uniqueId.value = newUniqueId }
    init {
        getAcc()
    }
    fun getAcc(){
        viewModelScope.launch {
            repository.readAllAcc.collectLatest { accs ->
                AccState = AccState.copy(
                    accs
                )
            }
        }
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
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))
            repository.insertDeparture(Departure(name = "Точка "))

            repository.insertTransport(Transport(name = "Автобус с343сс"))
            repository.insertTransport(Transport(name = "Автобус а123бв"))
            repository.insertTransport(Transport(name = "Автобус м567мм"))
            repository.insertTransport(Transport(name = "Автобус к567ун"))
            repository.insertTransport(Transport(name = "Автобус д345тп"))
            repository.insertTransport(Transport(name = "Автобус о039оо"))
            repository.insertTransport(Transport(name = "Автобус т999тт"))
            repository.insertTransport(Transport(name = "Автобус у435сс"))
            repository.insertTransport(Transport(name = "Автобус л234ох"))
            repository.insertTransport(Transport(name = "Автобус т930пр"))

            repository.deleteAllDocument()
            repository.deleteAllSotrInDoc()
            repository.insertDocument(
                Document(
                    id = 0,
                    distribution = "Без распределения",
                    departure_date = Date(2024-1900,5,12),
                    id_transport = 1,
                    id_destination = 1,
                    id_venues = "1,2,3"
                )
            )
            for (i in 1..30) {
                val venues = intArrayOf(Random().nextInt(10),Random().nextInt(10),Random().nextInt(10))
                repository.insertDocument(
                    Document(
                        id = i,
                        distribution = "Распределение " + i,
                        departure_date = Date(2024-1900,5,Random().nextInt(30)),
                        id_transport = Random().nextInt(10)+1,
                        id_destination = Random().nextInt(10)+1,
                        id_venues = venues[0].toString()+","+venues[1].toString()+","+venues[2].toString()
                    )
                )
                val ids = repository.getIds(30)
                for (j in 0..29) {
                    val r = venues[Random().nextInt(3)]
                    repository.insertSotrInDoc(
                        SotrudnikiInDocument(
                            id_sotrudnik = ids.get(j),
                            id_doc = i,
                            id_venue_in_doc = r,
                            id_venue_fact = 0,
                            route = "Рейс №" + r,
                            mark = 0,
                            available_in_doc = 1
                        )
                    )
                }
            }
        }
    }
}

data class AccState(
    val acc:List<Acc> = emptyList()
)