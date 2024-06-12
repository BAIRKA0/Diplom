package com.example.uchet.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sotrudniki_in_document")
data class SotrudnikiInDocument(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "id_sotrudnik")
    var id_sotrudnik: String,
    @ColumnInfo(name = "id_doc")
    var id_doc: Int,
    @ColumnInfo(name = "id_venue_in_doc")
    var id_venue_in_doc: Int,
    @ColumnInfo(name = "id_venue_fact")
    var id_venue_fact: Int,
    @ColumnInfo(name = "route")
    var route: String,
    @ColumnInfo(name = "mark")
    var mark: Int,
    @ColumnInfo(name = "available_in_doc")
    var available_in_doc: Int
)
