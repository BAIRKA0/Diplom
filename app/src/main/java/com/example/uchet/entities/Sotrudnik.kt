package com.example.uchet.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sotrudniki",
    indices = [Index(value = ["uid"], unique = true)]
)
data class Sotrudnik(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "surname")
    var surname: String,
    @ColumnInfo(name = "patronymic")
    var patronymic: String,
    @ColumnInfo(name = "company")
    var company: String,
    @ColumnInfo(name = "uid")
    var uid: Long
)
