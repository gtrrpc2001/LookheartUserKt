package com.mcuhq.simplebluetooth.signup

import android.app.AlertDialog
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
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.auth.sendEmail.SendMail
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.ServerTaskCallback
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.getInstance
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class Activity_Signup_Third : AppCompatActivity() {
    // 이메일/비밀번호 정규식
    val emailPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$"
    val passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{10,}$"
    var retrofitServerManager: RetrofitServerManager? = null
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var reEnterPasswordEditText: EditText
    var editChecknum: EditText? = null
    lateinit var emailHelp: TextView
    lateinit var passwordHelp: TextView
    lateinit var reEnterPasswordHelp: TextView
    var nextButton: Button? = null
    var backButton: Button? = null
    var signup_idcheck: Button? = null
    var signup_numcheck: Button? = null
    var emailCheckBox: CheckBox? = null
    var numCheckBox: CheckBox? = null
    var sv: ScrollView? = null
    var numlayout: View? = null
    var getCode = ""

    // 입력 체크
    lateinit var dataCheck: MutableMap<String, Boolean>
    var emailCheck: Boolean = false
    var passwordCheck: Boolean = false
    var reEnterpasswordCheck: Boolean = false
    private var email: String? = null
    private var password: String? = null
    private var reEnterPassword: String? = null
    private var encPw: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_third)
        retrofitServerManager = getInstance()

        // editText 입력창
        emailEditText = findViewById(R.id.editEmail)
        passwordEditText = findViewById(R.id.editPassword)
        reEnterPasswordEditText = findViewById(R.id.editReEnterPassword)
        editChecknum = findViewById(R.id.editChecknum)

        // editText Color
        val notMatchColor = ColorStateList.valueOf(Color.RED)
        val highlightColor = ColorStateList.valueOf(Color.parseColor("#62AFFF"))

        // 유효성 도움말
        emailHelp = findViewById(R.id.emailHelp)
        passwordHelp = findViewById(R.id.passwordHelp)
        reEnterPasswordHelp = findViewById(R.id.reEnterPasswordHelp)

        // 뒤로가기, 다음으로 버튼
        nextButton = findViewById(R.id.signup_third_next)
        backButton = findViewById(R.id.signup_third_back)
        signup_numcheck = findViewById(R.id.signup_numcheck)
        signup_idcheck = findViewById(R.id.signup_idcheck)
        emailCheckBox = findViewById(R.id.emailCheckBox)
        numCheckBox = findViewById(R.id.numCheckBox)
        numlayout = findViewById(R.id.numberlayout)
        numlayout?.setVisibility(View.GONE)
        sv = findViewById(R.id.scrollView)
        emailCheckBox?.setEnabled(false)
        numCheckBox?.setEnabled(false)

        // 인스턴스 얻기
        val sharedPref = getSharedPreferences("UserDetails", MODE_PRIVATE)
        // 값 저장 객체 얻기
        val editor = sharedPref.edit()

        // 입력 데이터 초기화
        dataCheck = HashMap()
        dataCheck["email"] = false
        dataCheck["password"] = false
        dataCheck["reEnterPassword"] = false

        // 초기화
        emailCheck = false
        passwordCheck = false
        reEnterpasswordCheck = false

        // hint Text 설정
        setHintText()

        // email event
        emailEditText?.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 포커스가 없어질 때
                if (emailCheck) {
                    // 입력 값이 유효한 경우
                } else {
                    // 입력 값이 유효하지 않은 경우
                    emailHelp?.setVisibility(View.VISIBLE)
                    ViewCompat.setBackgroundTintList(emailEditText, notMatchColor)
                }
            } else {
                // 포커스를 얻었을 때
                ViewCompat.setBackgroundTintList(emailEditText, highlightColor)
                KeyboardUp(300)
            }
        })
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                email = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck["email"] = true
                    emailHelp.setVisibility(View.GONE)
                } else {
                    // 매칭되지 않는 경우
                    dataCheck["email"] = false
                }
                // 유효성 체크
                emailCheck = dataCheck.get("email")!!
            }
        })
        signup_idcheck?.setOnClickListener(View.OnClickListener { v ->
            signup_idcheck?.setEnabled(false)
            val inputText = emailEditText.getText().toString().trim { it <= ' ' }
            hideKeyboard()
            if (inputText.isEmpty()) {
                OnClickHandler(v)
            } else if (emailCheck == false) {
                emailOnClickHandler()
            } else if (emailCheck) {

                // server에 email 중복 확인 요청
                retrofitServerManager!!.checkIdTask(email!!, object : ServerTaskCallback {
                    override fun onSuccess(result: String?) {
                        if (result!!.lowercase(Locale.getDefault()).contains("true")) {
                            val sharedPreferences =
                                getSharedPreferences("UserDetails", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("email", email)
                            editor.apply()

                            //send();
                            val getEmail = emailEditText.getText().toString()
                            val mailServer = SendMail(getEmail)
                            val title = "msl"
                            val content = "lookheart"
                            if (getEmail.length != 0 && getEmail.contains("@")) {
                                runOnUiThread { numOnClickHandler() }
                                val emailChecked = mailServer.sendSecurityCode(
                                    applicationContext, title, content
                                )
                                Log.e("emailChecked", emailChecked.toString())
                                getCode = mailServer.code
                                // code.requestFocus();
                                if (getCode == "") {
                                }
                            }
                        } else {
                            runOnUiThread { isIdDuplicate(v) } // 알림
                        }
                    }

                    override fun onFailure(e: Exception?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@Activity_Signup_Third,
                                "서버 응답 없음",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.e("err", e.toString())
                    }
                })
            }
        })
        signup_numcheck?.setOnClickListener(View.OnClickListener { v ->
            var codeStr = editChecknum?.getText().toString()
            if (codeStr.length != 0) {
                if (codeStr == getCode) {
                    codeStr = ""
                    //                        editChecknum.setText("");
//                        emailEditText.setText("");
                    result(resources.getString(R.string.authSuccess))
                    numCheckBox?.setChecked(true)
                } else {
                    failOnClickHandler(v)
                }
            }
        })


        // password event
        passwordEditText?.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 포커스가 없어질 때
                if (passwordCheck!!) {
                    // 입력 값이 유효한 경우
                } else {
                    // 입력 값이 유효하지 않은 경우
                    passwordHelp?.setVisibility(View.VISIBLE)
                    ViewCompat.setBackgroundTintList(passwordEditText, notMatchColor)
                }
            } else {
                // 포커스를 얻었을 때
                ViewCompat.setBackgroundTintList(passwordEditText, highlightColor)
                KeyboardUp(300)
            }
        })
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                password = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(passwordPattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck["password"] = true
                    passwordHelp.setVisibility(View.GONE)
                } else {
                    // 매칭되지 않는 경우
                    dataCheck["password"] = false
                }
                // 유효성 체크
                passwordCheck = dataCheck.get("password")!!
            }
        })

        // reEnterPassword Event
        reEnterPasswordEditText?.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 포커스가 없어질 때
                if (reEnterpasswordCheck!!) {
                    // 입력 값이 유효한 경우
                } else {
                    // 입력 값이 유효하지 않은 경우
                    reEnterPasswordHelp?.setVisibility(View.VISIBLE)
                    ViewCompat.setBackgroundTintList(reEnterPasswordEditText, notMatchColor)
                }
            } else {
                // 포커스를 얻었을 때
                ViewCompat.setBackgroundTintList(reEnterPasswordEditText, highlightColor)
                KeyboardUp(400)
            }
        })
        reEnterPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                reEnterPassword = s.toString()
                if (reEnterPassword == password) {
                    // 매칭되는 경우
                    dataCheck["reEnterPassword"] = true
                    reEnterPasswordHelp.setVisibility(View.GONE)
                } else {
                    // 매칭되지 않는 경우
                    dataCheck["reEnterPassword"] = false
                }
                // 유효성 체크
                reEnterpasswordCheck = dataCheck.get("reEnterPassword")!!
            }
        })

        // 뒤로가기
        backButton?.setOnClickListener(View.OnClickListener { onBackPressed() })

        // 다음으로
        nextButton?.setOnClickListener(View.OnClickListener { v ->
            // 입력값 유효성 체크
            if (emailCheck!! && passwordCheck!! && reEnterpasswordCheck!!) {
                try {
                    encPw = encryptECB("MEDSYSLAB.CO.KR.LOOKHEART.ENCKEY", password)
                    encPw = encPw!!.trim { it <= ' ' }.replace("\\s".toRegex(), "")
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }

                // server에 email 중복 확인 요청
                retrofitServerManager!!.checkIdTask(email!!, object : ServerTaskCallback {
                    override fun onSuccess(result: String?) {
                        if (result!!.lowercase(Locale.getDefault()).contains("true")) {
                            // email, password 저장
                            editor.putString("email", email)
                            editor.putString("password", encPw)
                            editor.apply()

                            // activiry 이동
                            val signupIntent = Intent(
                                this@Activity_Signup_Third,
                                Activity_Signup_Fourth::class.java
                            )
                            startActivity(signupIntent)
                        } else {
                            // email 중복
                            runOnUiThread { isIdDuplicate(v) } // 알림
                        }
                    }

                    override fun onFailure(e: Exception?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@Activity_Signup_Third,
                                "서버 응답 없음",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.e("err", e.toString())
                    }
                })
            } else {
                OnClickHandler(v)
            }
        })
    }

    fun KeyboardUp(size: Int) {
        sv!!.postDelayed({ sv!!.smoothScrollTo(0, size) }, 200)
    }

    fun OnClickHandler(view: View?) {
        val builder = AlertDialog.Builder(this)
        var alertString = ""
        if (!emailCheck!!) {
            alertString += resources.getString(R.string.email_Label) + ","
        }
        if (!passwordCheck!!) {
            alertString += resources.getString(R.string.password_Label) + ","
        }
        if (!reEnterpasswordCheck!!) {
            alertString += resources.getString(R.string.rpw_Label) + ","
        }
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(
                alertString.substring(
                    0,
                    alertString.length - 1
                ) + resources.getString(R.string.enterAgain)
            )
            .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                dialog.cancel() // 팝업 닫기
            }
            .show()
    }

    fun isIdDuplicate(view: View?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.dupID))
            .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                // 취소 버튼 클릭 시 수행할 동작
                dialog.cancel() // 팝업창 닫기
            }
            .show()
    }

    fun setHintText() {
        // EditText에 힌트 텍스트 스타일을 적용
        val emailHintText = resources.getString(R.string.id_Label)
        val passwordHintText = resources.getString(R.string.pw_Label)
        val reEnterPasswordHintText = resources.getString(R.string.rpw_Label)

        // 힌트 텍스트에 스타일을 적용
        val ssEmail = SpannableString(emailHintText)
        val ssPassword = SpannableString(passwordHintText)
        val ssReEnterPassword = SpannableString(reEnterPasswordHintText)
        val assEmail = AbsoluteSizeSpan(13, true) // 힌트 텍스트 크기 설정
        val assPassword = AbsoluteSizeSpan(13, true)
        val assReEnterPassword = AbsoluteSizeSpan(13, true)
        ssEmail.setSpan(assEmail, 0, ssEmail.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // 크기 적용
        ssPassword.setSpan(assPassword, 0, ssPassword.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssReEnterPassword.setSpan(
            assReEnterPassword,
            0,
            ssReEnterPassword.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 힌트 텍스트 굵기 설정
        ssEmail.setSpan(StyleSpan(Typeface.NORMAL), 0, ssEmail.length, 0) // 굵게
        ssPassword.setSpan(StyleSpan(Typeface.NORMAL), 0, ssPassword.length, 0)
        ssReEnterPassword.setSpan(StyleSpan(Typeface.NORMAL), 0, ssReEnterPassword.length, 0)

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        val emailText = findViewById<View>(R.id.editEmail) as EditText
        val passwordText = findViewById<View>(R.id.editPassword) as EditText
        val reEnterPasswordText = findViewById<View>(R.id.editReEnterPassword) as EditText
        emailText.hint = SpannedString(ssEmail) // 크기가 적용된 힌트 텍스트 설정
        passwordText.hint = SpannedString(ssPassword)
        reEnterPasswordText.hint = SpannedString(ssReEnterPassword)
        emailText.setHintTextColor(Color.parseColor("#555555")) // 색 변경
        passwordText.setHintTextColor(Color.parseColor("#555555"))
        reEnterPasswordText.setHintTextColor(Color.parseColor("#555555"))
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun emailOnClickHandler() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.checkEmail))
            .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                // 버튼 클릭 시 수행할 동작
                signup_idcheck!!.isEnabled = true
                dialog.cancel() // 팝업창 닫기
            }
            .show()
    }

    fun numOnClickHandler() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.sendVerification))
            .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                // 확인 버튼 클릭 시 수행할 동작
                emailCheckBox!!.isChecked = true
                signup_idcheck!!.isEnabled = true
                numlayout!!.visibility = View.VISIBLE
                dialog.cancel() // 팝업창 닫기
            }
            .show()
    }

    fun failOnClickHandler(view: View?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.confirmVerification))
            .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                // 버튼 클릭 시 수행할 동작
                dialog.cancel() // 팝업창 닫기
            }
            .show()
    }

    private fun result(Text: String) {
        Toast.makeText(this@Activity_Signup_Third, Text, Toast.LENGTH_LONG).show()
    } //    public static String decryptECB(String key, String encrypted) {

    //        try {
    //            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
    //            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    //            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
    //
    //            // Base64 디코딩을 먼저 수행합니다.
    //            byte[] decodedBytes = Base64.decode(encrypted, Base64.DEFAULT);
    //
    //            byte[] original = cipher.doFinal(decodedBytes);
    //            return new String(original, StandardCharsets.UTF_8);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        return null;
    //    }
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