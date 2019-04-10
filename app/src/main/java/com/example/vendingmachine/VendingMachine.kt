package com.example.vendingmachine

import android.util.Log
import com.harrysoft.androidbluetoothserial.BluetoothManager
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.widget.Toast
import com.example.vendingmachine.`object`.Product
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice
import java.util.*
import kotlin.collections.ArrayList

val TAG = "VendingMachine"


object VendingMachine {
    private var deviceInterface: SimpleBluetoothDeviceInterface? = null
    var vendingMachineListeners = ArrayList<VendingMachineListener>()
    var products = mutableListOf<Product>()

    public fun connectDevice(mac:String) {
        BluetoothManager.getInstance().openSerialDevice(mac)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onConnected, this::onError);
    }



    private fun onConnected(connectedDevice: BluetoothSerialDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        deviceInterface = connectedDevice.toSimpleDeviceInterface()

        // Listen to bluetooth events
        deviceInterface?.setListeners(this::onMessageReceived, this::onMessageSent, this::onError)

        for (listener in vendingMachineListeners){
            if (listener == null)
                continue
            listener.onConnected(connectedDevice)
        }

//        sendTest()
    }

    public fun sendTest(){// Let's send a message:
        deviceInterface?.sendMessage("chk")}


    private fun onMessageSent(message: String) {
      Log.d(TAG,"Sent message ${message}")

        for (listener in vendingMachineListeners){
            if (listener == null)
                continue
            listener.onMessageSent(message)
        }
    }

    private fun onMessageReceived(message: String) {
        Log.d(TAG,"Receive message ${message}")

        for (listener in vendingMachineListeners){
            if (listener == null)
                continue
            listener.onMessageReceived(message)
        }

    }

    fun sendMessage(message: String){
        deviceInterface?.sendMessage(message);
}

    private fun onError(error: Throwable) {
        Log.d(TAG,"Error  ${error.toString()}")


        for (listener in vendingMachineListeners){
            if (listener == null)
                continue
            listener.onError(error)
        }

    }

     fun addListener(listener: VendingMachineListener)= vendingMachineListeners.add(listener)


     fun removeListener(listener: VendingMachineListener) = vendingMachineListeners.remove(listener)



}