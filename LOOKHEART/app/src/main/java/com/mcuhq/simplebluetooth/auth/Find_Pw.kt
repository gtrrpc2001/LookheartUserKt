package com.mcuhq.simplebluetooth.auth

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.auth.Find_PwChange
import com.mcuhq.simplebluetooth.auth.sendEmail.SendMail
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.ServerTaskCallback
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.getInstance
import java.util.Locale

class Find_Pw() : AppCompatActivity() {
    /*EditText*/ //region
    var editText: EditText? = null
    var code: EditText? = null

    //endregion
    private val emailPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$"
    var retrofitServerManager: RetrofitServerManager? = null
    private var findPw_btn: Button? = null
    private var findPw_btn2: Button? = null
    private var email: String? = null
    private var emailcheck = false
    var getCode = ""
    var hinttext: TextView? = null
    var numbertext: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_pw)
        retrofitServerManager = getInstance()
        setViewID()
        editText!!.requestFocus()
        findPw_btn2!!.visibility = View.INVISIBLE
        setEditTextEvent()
        setfindPwEvent()
    }

    fun setfindPwEvent() {
        findPw_btn!!.setOnClickListener(View.OnClickListener { v ->
            val inputText = editText!!.text.toString().trim { it <= ' ' }
            hideKeyboard()
            if (inputText.isEmpty()) {
                OnClickHandler(v)
            } else if (emailcheck == false) {
                emailOnClickHandler()
            } else if (emailcheck) {

                // server에 email 중복 확인 요청
                retrofitServerManager!!.checkIdTask((email)!!, object : ServerTaskCallback {
                    override fun onSuccess(result: String?) {
                        if (!result!!.lowercase(Locale.getDefault()).trim { it <= ' ' }
                                .contains("true")) {
                            val sharedPreferences =
                                getSharedPreferences("UserDetails", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("email", email)
                            editor.apply()

                            //send();
                            val getEmail = editText!!.text.toString()
                            val mailServer = SendMail(getEmail)
                            val title = "msl"
                            val content = "lookheart"
                            if (getEmail.length != 0 && getEmail.contains("@")) {
                                runOnUiThread({ numOnClickHandler() })
                                findPw_btn2!!.visibility = View.VISIBLE
                                val emailChecked =
                                    mailServer.sendSecurityCode(applicationContext, title, content)
                                Log.e("emailChecked", emailChecked.toString())
                                getCode = mailServer.code
                                // code.requestFocus();
                                if ((getCode == "")) {
                                    result(resources.getString(R.string.failedPW))
                                }
                            } else {
                                result(resources.getString(R.string.failedPW))
                            }
                        } else {
                            runOnUiThread({ isIdDuplicate(v) }) // 알림
                        }
                    }

                    override fun onFailure(e: Exception?) {
                        runOnUiThread({
                            Toast.makeText(
                                this@Find_Pw,
                                getResources().getString(R.string.serverErr),
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                        Log.e("err", e.toString())
                    }
                })
            }
        })
        findPw_btn2!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                var codeStr = code!!.text.toString()
                if (codeStr.length != 0) {
                    if ((codeStr == getCode)) {
                        codeStr = ""
                        code!!.setText("")
                        editText!!.setText("")
                        result(resources.getString(R.string.authSuccess))
                        val findemailIntent = Intent(this@Find_Pw, Find_PwChange::class.java)
                        startActivity(findemailIntent)
                    } else {
                        failOnClickHandler(v)
                    }
                }
            }
        })
    }

    fun setEditTextEvent() {
        editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                email = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                    // 매칭되는 경우
                    emailcheck = true
                } else {
                    // 매칭되지 않는 경우
                    emailcheck = false
                }
            }
        })
    }

    fun setViewID() {
        editText = findViewById(R.id.findPw_email)
        findPw_btn = findViewById(R.id.findPw_btn)
        code = findViewById<View>(R.id.findNumber_number) as EditText
        findPw_btn2 = findViewById(R.id.findNumber_btn)
        hinttext = findViewById(R.id.findPw_hint2)
        numbertext = findViewById(R.id.findPw_text2)
    }

    fun OnClickHandler(view: View?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.noEnteredID))
            .setNegativeButton(
                resources.getString(R.string.ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel() // 팝업창 닫기
                    }
                })
            .show()
    }

    fun emailOnClickHandler() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.checkEmail))
            .setNegativeButton(
                resources.getString(R.string.ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel() // 팝업창 닫기
                    }
                })
            .show()
    }

    fun failOnClickHandler(view: View?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.confirmVerification))
            .setNegativeButton(
                resources.getString(R.string.ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel() // 팝업창 닫기
                    }
                })
            .show()
    }

    fun numOnClickHandler() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.sendVerification))
            .setNegativeButton(
                resources.getString(R.string.ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel() // 팝업창 닫기
                    }
                })
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun isIdDuplicate(view: View?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.emailNotExist))
            .setNegativeButton(
                resources.getString(R.string.ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel() // 팝업창 닫기
                    }
                })
            .show()
    }

    private fun result(Text: String) {
        Toast.makeText(this@Find_Pw, Text, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}