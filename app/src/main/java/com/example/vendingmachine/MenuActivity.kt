package com.example.vendingmachine

import android.os.Bundle
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.support.v4.view.ViewPager
import android.text.Editable
import android.util.Log
import android.widget.NumberPicker
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import ir.apend.slider.model.Slide
import ir.apend.slider.ui.Slider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_menu.*
import org.json.JSONException
import org.json.JSONObject


class MenuActivity : Activity() {
    lateinit var numberPicker: NumberPicker
    lateinit var customSlider: CustomSlider
    val mlValue = arrayOf("100","150","200","250","300","350","400","450","500")
    var currentMl = 3
    var currentProduct = 0
    var currentPrice =0.0
    lateinit var progress: ProgressDialog





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        customSlider = slider

        btn_increment.setOnClickListener {
            btn_decrement.isEnabled = true
            tv_volume.setText(mlValue.get(++currentMl))
        if (currentMl == mlValue.size-1){
            btn_increment.isEnabled = false
        }
            updateCurrentPrice()
        }

        btn_decrement.setOnClickListener {
            btn_increment.isEnabled = true
            tv_volume.setText(mlValue.get(--currentMl))
            if (currentMl == 0){
                btn_decrement.isEnabled = false
            }
            updateCurrentPrice()
        }

        btn_checkout.setOnClickListener {
            getTransactionCode { progress.hide() }
        }


//        numberPicker.displayedValues = mlValue

        val slideList = ArrayList<Slide>()
        for (i in 0 until VendingMachine.products.size){
            slideList.add(Slide(i,VendingMachine.products.get(i).imageURL, resources.getDimensionPixelSize(R.dimen.slider_image_corner) ) )
        }

        slider.addSlides(slideList)
        slider.setPageChange(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageSelected(p0: Int) {
                if (p0 > 0 && p0 <VendingMachine.products.size+1) {
                    val index = p0 - 1 % VendingMachine.products.size
                    Log.d(TAG, "Index ${index}")
                    tv_product_name.text = VendingMachine.products.get(index).name
                    tv_product_description.text = VendingMachine.products.get(index).description
                    currentProduct = index
                    updateCurrentPrice()
                }
            }
        })




    }

    fun getArrayWithSteps(iMinValue: Int, iMaxValue: Int, iStep: Int): Array<String> {
        val iStepsArray = (iMaxValue - iMinValue) / iStep + 1 //get the lenght array that will return

        val arrayValues = mutableListOf<String>() //Create array with length of iStepsArray

        for (i in 0 until iStepsArray) {
            arrayValues[i] = (iMinValue + i * iStep).toString()
        }

        return arrayValues.toTypedArray()
    }

    fun updateCurrentPrice(){
        currentPrice =  mlValue.get(currentMl).toInt() * VendingMachine.products.get(currentProduct).price
        tv_price.text = "${currentPrice} THB"
    }

    private fun getTransactionCode(callback:(String) -> Unit){

        progress = ProgressDialog.show(this,"Loading","Getting transaction..")

        val url = "https://us-central1-vending-machine-webhook.cloudfunctions.net/getTransactionNo"

        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                progress?.hide()

                try {
                    val data = JSONObject(response.get(0).toString())
                    val transactionCode =  JSONObject(data.toString()).get("transactionNo")
                    Log.d(TAG,"Transactioncode = ${transactionCode}")

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
