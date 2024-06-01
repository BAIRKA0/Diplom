package com.example.uchet

import android.app.Application
import com.example.uchet.db.MainDB

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}