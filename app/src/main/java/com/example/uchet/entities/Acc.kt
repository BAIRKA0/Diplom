package com.example.uchet.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Acc(
    @PrimaryKey()
    @ColumnInfo(name = "login")
    var login: String,
    @ColumnInfo(name = "password")
    var password: String,
    @ColumnInfo(name = "uid")
    var uid: String
)
