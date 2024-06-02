package com.example.uchet

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        Napier.base(DebugAntilog())
    }
}