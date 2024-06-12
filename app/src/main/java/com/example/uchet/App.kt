package com.example.uchet

import RfidBleManager
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App : Application() {
    lateinit var handler: Handler
    lateinit var rfidBleManager: RfidBleManager
    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        Napier.base(DebugAntilog())
        rfidBleManager= RfidBleManager(applicationContext)
        handler = Handler(Looper.getMainLooper())
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val pairedDevices = bluetoothAdapter.bondedDevices
        attemptConnection(pairedDevices)

    }

    private fun attemptConnection(pairedDevices: Set<BluetoothDevice>) {
        handler.postDelayed(object : Runnable {
            @SuppressLint("MissingPermission")
            override fun run() {
                val device =
                    pairedDevices.find { it.name == "ESP32_RFID" } // Replace with your device's name
                device?.let {
                    rfidBleManager.connect(it)
                }
                handler.postDelayed(this, 5000) // Retry every 5 seconds
            }
        }, 5000)
    }
}