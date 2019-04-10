package com.example.vendingmachine

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_pay.*
import net.glxn.qrgen.android.QRCode
import java.util.*

class PayActivity : AppCompatActivity() {
    var refNo = ""
    lateinit var countDownTimer:CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        btn_back_to_menu.setOnClickListener { finish() }
        refNo = intent.getStringExtra("refno")
        setData(intent.getStringExtra("qrcode"),intent.getDoubleExtra("productprice",0.0),
            intent.getStringExtra("productdesc"),refNo)
        setupFirebase()

        btn_back_to_menu.setOnClickListener {
          backToMenu()
         }

        tv_pay_price.setOnClickListener { onTransactionAdded() }

        countDownTimer = object: CountDownTimer(60 *1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                btn_back_to_menu.text = "CANCEL (${millisUntilFinished/1000}s)"
            }

            override fun onFinish() {
                backToMenu()
            }
        }.start()

    }

    private fun backToMenu(){
        countDownTimer.cancel()
        val intent = Intent(this, MenuActivity::class.java)
        this.finish()
        startActivity(intent)
    }

    private fun setData(qrdata:String, productPrice:Double, productDescription:String, refNo:String){
        val bitmap = QRCode.from(qrdata).bitmap()
        iv_qr_image.setImageBitmap(bitmap)
        tv_pay_description.text = productDescription
        tv_pay_price.text = String.format("%.2fTHB",productPrice)
        tv_ref_id.text = "Ref. no :" + refNo


    }

    private fun setupFirebase(){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("transaction")
        myRef.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d("Firebase OCA Key:",p0.key )
                if(p0.key == refNo)
                    onTransactionAdded()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    private fun onTransactionAdded(){
        countDownTimer.cancel();
        val newIntent = Intent(this, DispenseActivity::class.java)
        newIntent.putExtra("selectedProduct",intent.getIntExtra("selectedProduct",0) )
        newIntent.putExtra("selectedProductMl",intent.getIntExtra("selectedProductMl",0))
        startActivity(newIntent)
        finish()
    }
}
