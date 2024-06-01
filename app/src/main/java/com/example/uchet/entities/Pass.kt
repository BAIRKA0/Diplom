package com.example.uchet.entities

import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable

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

fun SinglePassDto.asModel(): PersonNew? {
    val passIdLong = this.passId.trim().toLongOrNull() ?: run {
        Napier.e("${this.uuid} passId parse error: ${this.passId}")
        return null
    }

    return PersonNew(
        uuid = uuid,
        passId = passIdLong,
        lastName = lastName,
        firstName = firstName,
        middleName = middleName,
        organization = organization,
    )
}
