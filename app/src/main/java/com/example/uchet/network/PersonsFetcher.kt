package com.example.uchet.network

import com.example.uchet.entities.Sotrudnik
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object PersonsFetcher {
    val client: HttpClient = NetworkModule.provideHttpClient(NetworkModule.provideJson())
    var URL: String = "https://check-dev.irkutskoil.ru/hotels_cydypov_ac_1/hs/laundries"

    suspend fun getLaundryPersonsWithPasses(deviceId: String, isResetNeeded: Boolean): PassesDto? {
        return try {
            client.get("$URL/passes") {
                basicAuth("Laundries", "Laundries")
                header("id", deviceId)
                if (isResetNeeded) {
                    header("fullexchange", "true")
                }
            }.let {response ->
                Json.decodeFromString(response.body())
            }
        } catch (e: Exception) {
            Napier.e("PF1 " + e.toString())
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
            Napier.e("PF2 " + e.toString())
            null
        }
    }
}

@Serializable
data class PassesDto(
    val passes: List<SinglePassDto>,
    val maxTransferredRevision: UInt,
    val isFullySynchronized: Boolean,
)

@Serializable
data class SinglePassDto(
    val uuid: String,
    val passId: String,
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val organization: String,
)

fun SinglePassDto.asModel(): Sotrudnik? {
    val passIdLong = this.passId.trim().toLongOrNull() ?: run {
        return null
    }

    return Sotrudnik(
        id = uuid,
        name = firstName,
        surname = lastName,
        patronymic = middleName,
        company = organization,
        uid = passIdLong
    )
}