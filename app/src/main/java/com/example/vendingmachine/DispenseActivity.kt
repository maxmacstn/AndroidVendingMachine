package com.example.vendingmachine

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dispense.*
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice


class DispenseActivity : AppCompatActivity(), VendingMachineListener {
    override fun onMessageSent(message: String) {
        Log.d(TAG, "Sent message "+ message)
    }

    override fun onMessageReceived(message: String) {
        setDispensingProgress(message.toInt())
    }

    override fun onConnected(connectedDevice: BluetoothSerialDevice) {
    }

    override fun onError(error: Throwable) {
        Toast.makeText(this,"Machine Disconnected", Toast.LENGTH_LONG).show()
        finish()
    }

    var selectedProduct = 0
    var selectedProductMl = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispense)
        waveLoadingView.progressValue = 0
        waveLoadingView.cancelAnimation()
        waveLoadingView.setOnClickListener {
            waveLoadingView.startAnimation();
            startDispensing()
        }
        VendingMachine.addListener(this);
        selectedProduct = intent.getIntExtra("selectedProduct",0)
        selectedProductMl = intent.getIntExtra("selectedProductMl",0)
    }

    private fun startDispensing(){
//        object : CountDownTimer(10000, 100) {
//
//            override fun onTick(millisUntilFinished: Long) {
//                 setDispensingProgress(((1 - (millisUntilFinished / 10000.0))*100).toInt())
//            }
//
//            override fun onFinish() {
//                setDispensingProgress(100)
//            }
//        }.start()
        waveLoadingView.isClickable = false;
        waveLoadingView.centerTitle = "Communicating.."
        var componentQuantity = mutableListOf<Int>(0,0,0)
        for( r in VendingMachine.products[selectedProduct].ratio){
            Log.d(TAG,"r = ${r.key -1} : ${(selectedProductMl * r.value).toInt()}")
            componentQuantity[r.key -1] = (selectedProductMl * r.value).toInt()
        }
        VendingMachine.sendMessage("dsp ${componentQuantity[0]} ${componentQuantity[1]} ${componentQuantity[2]}")

    }


    private fun setDispensingProgress(progress:Int){
        Log.d("dispensing", progress.toString())
        waveLoadingView.progressValue = progress
        waveLoadingView.centerTitle = "${progress}%"
        if (progress == 100){
            dispenseFinished()

        }
    }

    private fun dispenseFinished(){
        waveLoadingView.centerTitle = "Done!"
        waveLoadingView.endAnimation()

        object : CountDownTimer(5000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                this.cancel()
                endProcess()
            }
        }.start()

    }

    private fun endProcess(){
        VendingMachine.removeListener(this);
        val intent = Intent(this, MenuActivity::class.java)
        Log.d(TAG,"Create Menu")
        startActivity(intent)
        finish();

    }
}


