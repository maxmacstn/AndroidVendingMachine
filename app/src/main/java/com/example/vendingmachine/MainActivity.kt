package com.example.vendingmachine

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.view.Window
import android.widget.Toast
import android.app.ProgressDialog
import java.util.*
import android.content.DialogInterface
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.example.vendingmachine.`object`.Product
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray






class MainActivity : AppCompatActivity(), VendingMachineListener {
    lateinit var bluetoothManager: BluetoothManager
    var myBluetooth = BluetoothAdapter.getDefaultAdapter()
    //    val  pairedDevices = myBluetooth.getBondedDevices()
    lateinit var progress: ProgressDialog
    private val isBtConnected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_main)
        progress = ProgressDialog.show(this,"Loading","Connecting to server")
        checkBluetooth()


        getServerData()
        btn_connect_to_machine.setOnClickListener {
            listDevice()
        }
        btn_start_vending.setOnClickListener {
            next()
        }


    }

    private fun checkBluetooth() {

        if (myBluetooth == null) {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(applicationContext, "Bluetooth Device Not Available", Toast.LENGTH_LONG).show()
            //finish apk
//            finish()
        } else {
            if (myBluetooth.isEnabled()) {
                bluetoothManager = BluetoothManager.getInstance()
                if (bluetoothManager == null) {
                    // Bluetooth unavailable on this device :( tell the user
                    Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG)
                        .show() // Replace context with your context instance.
//                    finish()
                }
            } else {
                //Ask to the user turn the bluetooth on
                val turnBTon = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(turnBTon, 1)
            }
        }


    }

    private fun listDevice() {
//        val list = ArrayList<String>();
//
//        if (pairedDevices.size > 0) {
//            for (bt in pairedDevices) {
//                list.add(bt.getName() + "\n" + bt.getAddress()) //Get the device's name and the address
//            }
//        } else {
//            Toast.makeText(applicationContext, "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show()
//        }

        val pairedDevices = bluetoothManager.pairedDevicesList
        val list = ArrayList<String>();
        for (device in pairedDevices) {
            Log.d("My Bluetooth App", "Device name: " + device.name)
            Log.d("My Bluetooth App", "Device MAC Address: " + device.address)
            list.add(device.name + "\n" + device.address) //Get the device's name and the address

        }
        if (pairedDevices.isEmpty()) {
            Toast.makeText(applicationContext, "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show()
        }

        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select device")

        val cs = list.toArray(arrayOfNulls<CharSequence>(list.size))

        builder.setItems(cs, DialogInterface.OnClickListener { dialog, which ->
            // Get the device MAC address, the last 17 chars in the View
            val info = list[which]
            val address = info.substring(info.length - 17)
            connectBluetooth(address)
        })

        builder.setNegativeButton("Cancel", null)

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()


    }


    private fun connectBluetooth(address: String) {

        VendingMachine.addListener(this)
        VendingMachine.connectDevice(address)
//


    }

    private fun next() {
//        if (btSocket != null) {
//            try {
//                btSocket?.getOutputStream()?.write("TO".toByteArray())
//                Toast.makeText(applicationContext, "Sent", Toast.LENGTH_LONG).show()
//
//            } catch (e: IOException) {
//                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
//            }
//
//        }
        VendingMachine.sendTest()

        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    override fun onMessageSent(message: String) {
    }

    override fun onMessageReceived(message: String) {

    }

    override fun onError(error: Throwable) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        tv_machine_status.text = error.message

    }

    override fun onConnected(connectedDevice: BluetoothSerialDevice) {
        tv_machine_status.text = "Connected to ${connectedDevice.mac}"
    }

    private fun getServerData() {

        progress?.show()
        val url = "https://us-central1-vending-machine-webhook.cloudfunctions.net/getProduct"

        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
//                Log.d(TAG,response.toString())
                tv_server_status.text = "Downloaded"
                progress?.hide()

                try {
                    val data = JSONObject(response.get(0).toString())
                    for (i in 1..data.length()+1){
                        val productJson =  JSONObject(data.get(i.toString()).toString())
                        val productRatio = JSONObject(productJson.get("ratio").toString())
                        val ratioMap =  mutableListOf<Map<Int,Double>>()
                        for (key in productRatio.keys()){
                            ratioMap.add(mapOf(key.toInt() to productRatio.getDouble(key)))
                        }

                        val product = Product(productJson.getString("name"),productJson.getString("description"), productJson.getDouble("price"), ratioMap,productJson.getString("image"))

                        VendingMachine.products.add(product)
                        Log.d(TAG,product.toString())




//                        VendingMachine.products.add(Product(productJson.getString("name")))
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            },
            Response.ErrorListener { error ->
                Log.d(TAG,"ERROR!" + error.toString())
                progress?.hide()

            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)


    }


}
