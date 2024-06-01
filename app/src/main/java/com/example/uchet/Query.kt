package com.example.uchet

import com.example.uchet.entities.PassesDto
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json


class Query(
    val client: HttpClient = NetworkModule.provideHttpClient(NetworkModule.provideJson()),
    val URL: String = "https://check-dev.irkutskoil.ru/hotels_cydypov_ac_1/hs/laundries"
) {
    suspend fun getLaundryPersonsWithPasses(deviceId: String, isResetNeeded: Boolean): PassesDto? {
        return try {
            client.get("$URL/passes") {
                basicAuth("Laundries", "Laundries")
                header("id", deviceId)
                if (isResetNeeded) {
                    header("fullexchange", "true")
                }
            }.let { response ->
                Json.Default.decodeFromString(response.body())
            }
        } catch (e: Exception) {
            Napier.e("",e)
            null
        }
    }

    suspend fun confirmGetPersonsWithPasses(deviceId: String, maxTransferredRevision: UInt): Unit? {
        return try {
            client.post("$URL/ConfirmData") {
                basicAuth("Laundries", "Laundries")
                header("id", deviceId)
                header("maxtransferredrevision", maxTransferredRevision.toString())
            }
            Unit
        } catch (e: Exception) {
            Napier.e("",e)
            null
        }
    }

    private val isSyncing = MutableStateFlow(false)

    suspend fun sync() {
        if (isSyncing.value) {
            return
        }
        isSyncing.value = true

        while (true) {
//            val isFirstFetch = laundryDataStore.personSyncLastUpdate.observe.first() == null
//            Napier.d("Getting persons from network, isFirstFetch = $isFirstFetch")
//            val newData = irkutskOilNetwork.getLaundryPersonsWithPasses(laundryDataStore.getDeviceIdString(), isFirstFetch)
//            if (newData == null) {
//                laundryDataStore.personSyncLastError.update(Clock.System.now())
//                isSyncing.value = false
//                return
//            }
//
//            Napier.d("Got ${newData.passes.size} new records. Updating DB...")
//            personNewDao.setOrUpdate(newData.passes.map { it.asModel()?.asEntity() }.filterNotNull())
//            laundryDataStore.personSyncLastUpdate.update(Clock.System.now())
//
//            Napier.d("Sending confirm...")
//            val afsdf = irkutskOilNetwork.confirmGetPersonsWithPasses(
//                laundryDataStore.getDeviceIdString(),
//                newData.maxTransferredRevision,
//            )
//            Napier.d(if (afsdf != null) "Sent successfully" else "Didn't sent :(")
//            if (newData.isFullySynchronized) {
//                laundryDataStore.personSyncLastError.update(null)
//                isSyncing.value = false
//                Napier.d(
//                    "Synced! ${personNewDao.getPersonsCount()} persons in DB " +
//                            "(${personNewDao.getFiredPersonsCount()} is fired).",
//                )
//                return
//            }
            Napier.d("Not fully synchronized...")
        }
    }
}