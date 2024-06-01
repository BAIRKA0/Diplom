package com.example.uchet.entities

data class PersonNew(
    val uuid: String,
    val passId: Long,
    val organization: String,
    val lastName: String,
    val firstName: String,
    val middleName: String,
) {
    val fullName: String
        get() = "$lastName $firstName $middleName"

    val initialsName: String
        get() = "$lastName ${firstName.firstOrNull()}.${middleName.firstOrNull()}."
}
