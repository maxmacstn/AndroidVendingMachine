package com.example.vendingmachine

import android.util.Log
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice

interface VendingMachineListener {


    fun onMessageSent(message: String)
    fun onMessageReceived(message: String)
    fun onConnected(connectedDevice: BluetoothSerialDevice)
    fun onError(error: Throwable)
}
