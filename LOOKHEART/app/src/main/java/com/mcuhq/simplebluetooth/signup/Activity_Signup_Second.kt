package com.mcuhq.simplebluetooth.signup

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.language.LanguageCheck
import com.mcuhq.simplebluetooth.signup.Activity_Signup_Third
import java.io.IOException
import java.io.InputStream

class Activity_Signup_Second : AppCompatActivity() {
    var privacyTxt: TextView? = null
    var agreeImageButton: ImageButton? = null
    var backButton: Button? = null
    var nextButton: Button? = null
    var privacyCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_second)
        privacyTxt = findViewById(R.id.agreeTxt)
        agreeImageButton = findViewById(R.id.agreeImage)
        backButton = findViewById(R.id.signup_second_back)
        nextButton = findViewById(R.id.signup_second_next)

        // 데이터 읽기
        val sharedPref = getSharedPreferences("UserDetails", MODE_PRIVATE)
        privacyCheck = sharedPref.getBoolean("privacy", false)

        // img set
        if (!privacyCheck) {
            agreeImageButton?.setImageResource(R.drawable.signup_agreebutton_normal)
        } else {
            agreeImageButton?.setImageResource(R.drawable.login_autologin_press)
        }
        try {
            val `in`: InputStream
            `in` = when (LanguageCheck.checklanguage(this)) {
                "en" -> resources.openRawResource(R.raw.privacy_en)
                "ko" -> resources.openRawResource(R.raw.privacy)
                "zh" -> resources.openRawResource(R.raw.privacy_cn)
                else -> resources.openRawResource(R.raw.privacy_en)
            }
            // 현재 읽어올 수 있는 바이트 수 반환
            val b = ByteArray(`in`.available())
            // byte 만큼 데이터를 읽어 b에 저장
            `in`.read(b)
            // 읽어온 byte를 문자열 형태로 바꿈
            val s = String(b)
            privacyTxt?.setText(s)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 뒤로가기
        backButton?.setOnClickListener(View.OnClickListener { onBackPressed() })

        // 다음으로
        nextButton?.setOnClickListener(View.OnClickListener { v ->
            privacyCheck = sharedPref.getBoolean("privacy", false)
            if (!privacyCheck) {
                OnClickHandler(v)
            } else {
                val signupIntent =
                    Intent(this@Activity_Signup_Second, Activity_Signup_Third::class.java)
                startActivity(signupIntent)
            }
        })
    }

    fun OnClickHandler(view: View?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.notAgree))
            .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                // 취소 버튼 클릭 시 수행할 동작
                dialog.cancel() // 팝업창 닫기
            }
            .show()
    }

    fun privacyButtonClickEvent(v: View?) {
        // 인스턴스 얻기(이름,모드)
        val sharedPref = getSharedPreferences("UserDetails", MODE_PRIVATE)
        // 값 저장 객체 얻기
        val editor = sharedPref.edit()
        // 데이터 읽기
        privacyCheck = !privacyCheck
        if (privacyCheck) {
            // 데이터 저장
            editor.putBoolean("privacy", privacyCheck)
            agreeImageButton!!.setImageResource(R.drawable.login_autologin_press)
        } else {
            editor.putBoolean("privacy", privacyCheck)
            agreeImageButton!!.setImageResource(R.drawable.signup_agreebutton_normal)
        }
        editor.apply() // 비동기 저장
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}