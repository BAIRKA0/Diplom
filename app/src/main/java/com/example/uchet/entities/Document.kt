package com.example.uchet.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "distribution")
    var distribution: String,
    @ColumnInfo(name = "departure_date")
    var departure_date: Date,
    @ColumnInfo(name = "transport")
    var id_transport: Int,
    @ColumnInfo(name = "venues")
    var id_venues: String,
    @ColumnInfo(name = "destination")
    var id_destination: Int
)
