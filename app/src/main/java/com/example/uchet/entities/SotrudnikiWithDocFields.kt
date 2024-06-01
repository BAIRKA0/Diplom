package com.example.uchet.entities

data class SotrudnikiWithDocFields(
    var id: Int?,
    var id_sotrudnik: Int,
    var id_doc: Int,
    var id_venue_in_doc: Int,
    var id_venue_fact: Int,
    var route: String,
    var mark: Boolean,
    var available_in_doc: Boolean,
    var name: String,
    var surname: String,
    var patronymic: String,
    var uid: Long,
)
