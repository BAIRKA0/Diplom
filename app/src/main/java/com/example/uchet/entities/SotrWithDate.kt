package com.example.uchet.entities

import java.util.Date

data class SotrWithDate(
    val id: Int?,
    val id_sotrudnik: String,
    val id_doc: Int,
    val id_venue_in_doc: Int,
    val id_venue_fact: Int,
    val route: String,
    val mark: Boolean,
    val available_in_doc: Boolean,
    val name: String,
    val surname: String,
    val patronymic: String,
    val uid: Long,
    val date: Date,
    val destination: String
)
