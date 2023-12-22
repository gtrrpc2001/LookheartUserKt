package com.mcuhq.simplebluetooth.auth

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.activity.login.Activity_Login
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.ServerTaskCallback
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.getInstance
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class Find_PwChange() : AppCompatActivity() {
    /*EditText*/ //region
    var ch_passwordEditText: EditText? = null
    var ch_reEnterPasswordEditText: EditText? = null

    //endregion
    /*TextView*/ //region
    var ch_passwordHelp: TextView? = null
    var ch_reEnterPasswordHelp: TextView? = null

    //endregion
    /*check*/ //region
    lateinit var dataCheck: MutableMap<String, Boolean>
    var ch_passwordCheck: Boolean? = null
    var ch_reEnterpasswordCheck: Boolean? = null

    //endregion
    /*password*/ //region
    val passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{10,}$"
    private var password: String? = null
    private var reEnterPassword: String? = null

    //endregion
    var retrofitServerManager: RetrofitServerManager? = null
    var ChangePw: Button? = null
    var sv: ScrollView? = null
    var encPw: String? = ""
    var email: String? = ""
    private var alertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_pwchange)
        retrofitServerManager = getInstance()
        setViewID()

        // 인스턴스 얻기
        val sharedPref = getSharedPreferences("UserDetails", MODE_PRIVATE)

        // 값 저장 객체 얻기
        val editor = sharedPref.edit()
        email = sharedPref.getString("email", "null")
        setDataSetClear()

        // 초기화
        setCheckClear()

        // hint Text 설정
        setHintText()

        // password event
        setPasswordEvent()
        setButtonEvent()
    }

    fun setButtonEvent() {
        ChangePw!!.setOnClickListener(View.OnClickListener { v ->
            // 입력값 유효성 체크
            if ((ch_passwordCheck)!! && (ch_reEnterpasswordCheck)!!) {
                try {
                    encPw = encryptECB("MEDSYSLAB.CO.KR.LOOKHEART.ENCKEY", password)
                    encPw = encPw!!.trim { it <= ' ' }.replace("\\s".toRegex(), "")
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
                retrofitServerManager!!.updatePWD((email)!!, encPw!!, object : ServerTaskCallback {
                    override fun onSuccess(result: String?) {
                        if (result!!.lowercase(Locale.getDefault()).trim { it <= ' ' }
                                .contains("true")) completeLogin() // 성공
                        else pwOnClickHandler() // 실패
                    }

                    override fun onFailure(e: Exception?) {
                        runOnUiThread({
                            Toast.makeText(
                                this@Find_PwChange,
                                getResources().getString(R.string.serverErr),
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                        Log.e("err", e.toString())
                    }
                })
                runOnUiThread({ completeLogin() })
            } else {
                OnClickHandler(v)
            }
        })
    }

    fun setPasswordEvent() {
        val notMatchColor = ColorStateList.valueOf(Color.RED)
        val highlightColor = ColorStateList.valueOf(Color.parseColor("#62AFFF"))
        ch_passwordEditText!!.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if ((ch_passwordCheck)!!) {
                        // 입력 값이 유효한 경우
                    } else {
                        // 입력 값이 유효하지 않은 경우
                        ch_passwordHelp!!.visibility = View.VISIBLE
                        ViewCompat.setBackgroundTintList((ch_passwordEditText)!!, notMatchColor)
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList((ch_passwordEditText)!!, highlightColor)
                    KeyboardUp(300)
                }
            }
        }
        ch_passwordEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                password = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(passwordPattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck!!["password"] = true
                    ch_passwordHelp!!.visibility = View.GONE
                } else {
                    // 매칭되지 않는 경우
                    dataCheck!!["password"] = false
                }
                // 유효성 체크
                ch_passwordCheck = dataCheck!!["password"]
            }
        })

        // reEnterPassword Event
        ch_reEnterPasswordEditText!!.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if ((ch_reEnterpasswordCheck)!!) {
                        // 입력 값이 유효한 경우
                    } else {
                        // 입력 값이 유효하지 않은 경우
                        ch_reEnterPasswordHelp!!.visibility = View.VISIBLE
                        ViewCompat.setBackgroundTintList(
                            (ch_reEnterPasswordEditText)!!,
                            notMatchColor
                        )
                    }
                } else {
                    // 포커스를 얻었을 때
                    ViewCompat.setBackgroundTintList((ch_reEnterPasswordEditText)!!, highlightColor)
                    KeyboardUp(300)
                }
            }
        }
        ch_reEnterPasswordEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                reEnterPassword = s.toString()
                if ((reEnterPassword == password)) {
                    // 매칭되는 경우
                    dataCheck!!["reEnterPassword"] = true
                    ch_reEnterPasswordHelp!!.visibility = View.GONE
                } else {
                    // 매칭되지 않는 경우
                    dataCheck!!["reEnterPassword"] = false
                }
                // 유효성 체크
                ch_reEnterpasswordCheck = dataCheck!!["reEnterPassword"]
            }
        })
    }

    fun setCheckClear() {
        ch_passwordCheck = false
        ch_reEnterpasswordCheck = false
    }

    fun setDataSetClear() {
        // 입력 데이터 초기화
        dataCheck = HashMap()
        dataCheck["password"] = false
        dataCheck["reEnterPassword"] = false
    }

    fun setViewID() {
        // editText 입력창
        ch_passwordEditText = findViewById(R.id.ch_editPassword)
        ch_reEnterPasswordEditText = findViewById(R.id.ch_editReEnterPassword)
        // 유효성 도움말
        ch_passwordHelp = findViewById(R.id.ch_passwordHelp)
        ch_reEnterPasswordHelp = findViewById(R.id.ch_reEnterPasswordHelp)
        ChangePw = findViewById(R.id.changepw_btn)
        sv = findViewById(R.id.ch_ScrollView)
    }

    fun KeyboardUp(size: Int) {
        sv!!.postDelayed(object : Runnable {
            override fun run() {
                sv!!.smoothScrollTo(0, size)
            }
        }, 200)
    }

    fun OnClickHandler(view: View?) {
        val builder = AlertDialog.Builder(this)
        var alertString = ""
        if (!ch_passwordCheck!!) {
            alertString += resources.getString(R.string.password_Label) + ","
        }
        if (!ch_reEnterpasswordCheck!!) {
            alertString += resources.getString(R.string.rpw_Label) + ","
        }
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(
                alertString.substring(
                    0,
                    alertString.length - 1
                ) + resources.getString(R.string.enterAgain)
            )
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

    fun setHintText() {
        // EditText에 힌트 텍스트 스타일을 적용
        val passwordHintText = resources.getString(R.string.pw_Label)
        val reEnterPasswordHintText = resources.getString(R.string.rpw_Label)

        // 힌트 텍스트에 스타일을 적용
        val ssPassword = SpannableString(passwordHintText)
        val ssReEnterPassword = SpannableString(reEnterPasswordHintText)
        val assEmail = AbsoluteSizeSpan(13, true) // 힌트 텍스트 크기 설정
        val assPassword = AbsoluteSizeSpan(13, true)
        val assReEnterPassword = AbsoluteSizeSpan(13, true)
        ssPassword.setSpan(assPassword, 0, ssPassword.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssReEnterPassword.setSpan(
            assReEnterPassword,
            0,
            ssReEnterPassword.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 힌트 텍스트 굵기 설정
        ssPassword.setSpan(StyleSpan(Typeface.NORMAL), 0, ssPassword.length, 0)
        ssReEnterPassword.setSpan(StyleSpan(Typeface.NORMAL), 0, ssReEnterPassword.length, 0)

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        val passwordText = findViewById<View>(R.id.ch_editPassword) as EditText
        val reEnterPasswordText = findViewById<View>(R.id.ch_editReEnterPassword) as EditText
        passwordText.hint = SpannedString(ssPassword)
        reEnterPasswordText.hint = SpannedString(ssReEnterPassword)
        passwordText.setHintTextColor(Color.parseColor("#555555"))
        reEnterPasswordText.setHintTextColor(Color.parseColor("#555555"))
    }

    fun completeLogin() {
        alertDialog = AlertDialog.Builder(this@Find_PwChange)
            .setTitle(resources.getString(R.string.passwordComp))
            .setMessage(resources.getString(R.string.returnLogin))
            .setNegativeButton(
                resources.getString(R.string.ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.cancel() // 팝업창 닫기
                        val intent = Intent(applicationContext, Activity_Login::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }).create()
        alertDialog?.show()
    }

    fun pwOnClickHandler() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.reconfirmPW))
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

    override fun onDestroy() {
        super.onDestroy()
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
    }

    companion object {
        fun encryptECB(key: String, value: String?): String? {
            try {
                val skeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "AES")
                val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
                val encrypted = cipher.doFinal(value!!.toByteArray(StandardCharsets.UTF_8))
                return Base64.encodeToString(encrypted, Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}