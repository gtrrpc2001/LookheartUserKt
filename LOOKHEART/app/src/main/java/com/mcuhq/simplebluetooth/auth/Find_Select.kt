package com.mcuhq.simplebluetooth.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mcuhq.simplebluetooth.R

class Find_Select : AppCompatActivity() {
    var findEmail: Button? = null
    var findPw: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_select)
        findEmail = findViewById(R.id.findEmail)
        findPw = findViewById(R.id.findPw)
        findEmail?.setOnClickListener(View.OnClickListener {
            val findemailIntent = Intent(this@Find_Select, Find_Email::class.java)
            startActivity(findemailIntent)
        })
        findPw?.setOnClickListener(View.OnClickListener {
            val findpwIntent = Intent(this@Find_Select, Find_Pw::class.java)
            startActivity(findpwIntent)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}