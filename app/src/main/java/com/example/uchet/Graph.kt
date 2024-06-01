package com.example.uchet

import android.content.Context
import com.example.uchet.db.MainDB

object Graph {
    lateinit var db:MainDB
        private set

    val repository by lazy {
        Repository(
            documentDao = db.documentDao(),
            accDao = db.accDao(),
            sotrudnikDao = db.sotrudnikDao(),
            sotrInDocDao = db.sotrInDocDao(),
            departureDao = db.departureDao(),
            transportDao = db.transportDao()
        )
    }

    fun provide(context: Context){
        db=MainDB.createUchetDB(context)
    }
}