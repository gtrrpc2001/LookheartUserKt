package com.mcuhq.simplebluetooth.activity.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.activity.Activity_Main
import com.mcuhq.simplebluetooth.activity.login.BatteryOptimizationUtil.isIgnoringBatteryOptimizations
import com.mcuhq.simplebluetooth.activity.login.BatteryOptimizationUtil.showBatteryOptimizationDialog
import com.mcuhq.simplebluetooth.auth.Find_Select
import com.mcuhq.simplebluetooth.permission.PermissionManager
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.ServerTaskCallback
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.getInstance
import com.mcuhq.simplebluetooth.signup.Activity_Signup_First
import java.nio.charset.StandardCharsets
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class Activity_Login() : AppCompatActivity() {
    /*이메일/비밀번호 정규식*/ //region
    val emailPattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}$"
    val passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{10,}$"

    //endregion
    /*button*/ //region
    var autoLoginButton: Button? = null
    var signupButton: Button? = null
    var loginButton: Button? = null
    var findEmailPw: Button? = null

    //endregion
    /*check*/ //region
    var autoLoginCheck = false
    lateinit var dataCheck: MutableMap<String, Boolean>
    var emailCheck: Boolean? = null
    var passwordCheck: Boolean? = null

    //endregion
    /*editText*/ //region
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null

    //endregion
    /*login*/ //region
    private var email: String? = ""
    private var password: String? = ""
    var autoLogin = false

    //endregion
    var retrofitServerManager: RetrofitServerManager? = null
    var autoLoginImageButton: ImageButton? = null
    var sv: ScrollView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        retrofitServerManager = getInstance()
        setViewID()

        // 입력 데이터 초기화
        setDataCheckClear()

        // 초기화
        setCheckClear()
        setHintText() // edit Text hint set
        val autoLoginSP = getSharedPreferences("autoLogin", MODE_PRIVATE)
        val editor = autoLoginSP.edit()
        autoLogin = autoLoginSP.getBoolean("autologin", false)
        if (autoLogin) {
            val intent = Intent(this@Activity_Login, Activity_Main::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        if (Build.VERSION.SDK_INT >= 33) // notification permission
            PermissionManager(this) else {
            if (!isIgnoringBatteryOptimizations(this)) showBatteryOptimizationDialog(this) // 배터리 최적화 무시 요청
        }

        // email event
        setEmailEditTextEvent()

        // password event
        setPasswordEditTextEvent()

        // signup activity 전환
        setButtonEvent(editor)
    }

    fun setPasswordEditTextEvent() {
        passwordEditText!!.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if ((passwordCheck)!!) {
                        // 입력 값이 유효한 경우
                    } else {
                        // 입력 값이 유효하지 않은 경우
                    }
                } else {
                    // 포커스를 얻었을 때
                    KeyboardUp(400)
                }
            }
        }
        passwordEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                password = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(passwordPattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck!!["password"] = true
                } else {
                    // 매칭되지 않는 경우
                    dataCheck!!["password"] = false
                }
                // 유효성 체크
                passwordCheck = dataCheck!!["password"]
            }
        })
    }

    fun setButtonEvent(editor: SharedPreferences.Editor) {
        signupButton!!.setOnClickListener(View.OnClickListener {
            val signupIntent = Intent(this@Activity_Login, Activity_Signup_First::class.java)
            startActivity(signupIntent)
        })
        loginButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                // null 값 확인
                if ((email == null || email!!.isEmpty()) && (password == null || password!!.isEmpty())) {
                    Toast.makeText(
                        this@Activity_Login,
                        resources.getString(R.string.idAndPwHelp),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else if (email == null || email!!.isEmpty()) {
                    Toast.makeText(
                        this@Activity_Login,
                        resources.getString(R.string.email_Hint),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else if (password == null || password!!.isEmpty()) {
                    Toast.makeText(
                        this@Activity_Login,
                        resources.getString(R.string.password_Hint),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val encPw = encryptECB("MEDSYSLAB.CO.KR.LOOKHEART.ENCKEY", password!!)
                retrofitServerManager!!.loginTask(
                    email!!,
                    encPw!!.trim { it <= ' ' },
                    object : ServerTaskCallback {
                        override fun onSuccess(result: String?) {
                            Log.i("loginTask", (result)!!)
                            if (result.lowercase(Locale.getDefault()).contains("true")) {
                                val sharedPref = getSharedPreferences(email, MODE_PRIVATE)
                                val userEditor = sharedPref.edit()
                                val emailSharedPreferences =
                                    getSharedPreferences("User", MODE_PRIVATE)
                                val emailEditor = emailSharedPreferences.edit()
                                userEditor.putString("email", email)
                                userEditor.apply()
                                emailEditor.putString("email", email)
                                emailEditor.apply()
                                editor.putBoolean("autologin", autoLoginCheck)
                                editor.apply()
                                val intent = Intent(this@Activity_Login, Activity_Main::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                                runOnUiThread({
                                    Toast.makeText(
                                        this@Activity_Login,
                                        getResources().getString(R.string.loginSuccess),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            } else {
                                runOnUiThread(object : Runnable {
                                    override fun run() {
                                        val builder = AlertDialog.Builder(this@Activity_Login)
                                        builder.setTitle(resources.getString(R.string.loginFailed))
                                            .setMessage(resources.getString(R.string.incorrectlyLogin))
                                            .setNegativeButton(
                                                resources.getString(R.string.ok),
                                                object : DialogInterface.OnClickListener {
                                                    override fun onClick(
                                                        dialog: DialogInterface,
                                                        which: Int
                                                    ) {
                                                        // 취소 버튼 클릭 시 수행할 동작
                                                        dialog.cancel() // 팝업창 닫기
                                                    }
                                                })
                                            .show()
                                    }
                                })
                            }
                            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(v.windowToken, 0)
                        }

                        override fun onFailure(e: Exception?) {
                            runOnUiThread({
                                Toast.makeText(
                                    this@Activity_Login,
                                    getResources().getString(R.string.serverErr),
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        }
                    })
            }
        })
        findEmailPw!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val findIntent = Intent(this@Activity_Login, Find_Select::class.java)
                startActivity(findIntent)
            }
        })
    }

    fun setEmailEditTextEvent() {
        emailEditText!!.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    // 포커스가 없어질 때
                    if ((emailCheck)!!) {
                        // 입력 값이 유효한 경우
                    } else {
                        // 입력 값이 유효하지 않은 경우
                    }
                } else {
                    // 포커스를 얻었을 때
                    KeyboardUp(400)
                }
            }
        }
        emailEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                email = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck!!["email"] = true
                } else {
                    // 매칭되지 않는 경우
                    dataCheck!!["email"] = false
                }
                // 유효성 체크
                emailCheck = dataCheck!!["email"]
            }
        })
    }

    fun setDataCheckClear() {
        dataCheck = HashMap()
        dataCheck["email"] = false
        dataCheck["password"] = false
    }

    fun setCheckClear() {
        autoLoginCheck = false
        emailCheck = false
        passwordCheck = false
    }

    fun setViewID() {
        // edit text
        emailEditText = findViewById(R.id.editEmail)
        passwordEditText = findViewById(R.id.editPassword)
        // auto Login Button
        autoLoginButton = findViewById(R.id.autoLogin)
        autoLoginImageButton = findViewById(R.id.autoLoginImage)
        // signup Button
        signupButton = findViewById(R.id.singup)
        loginButton = findViewById(R.id.loginButton)
        // auth
        findEmailPw = findViewById(R.id.findEmailPw)
        sv = findViewById(R.id.scrollView)
    }

    fun KeyboardUp(size: Int) {
        sv!!.postDelayed(object : Runnable {
            override fun run() {
                sv!!.smoothScrollTo(0, size)
            }
        }, 200)
    }

    // 자동 로그인 클릭 이벤트
    fun autoLoginClickEvent(v: View?) {
        autoLoginCheck = !autoLoginCheck
        if (autoLoginCheck) {
            autoLoginImageButton!!.setImageResource(R.drawable.login_autologin_press)
        } else {
            autoLoginImageButton!!.setImageResource(R.drawable.login_autologin_normal)
        }
    }

    fun setHintText() {
        // EditText에 힌트 텍스트 스타일을 적용
        val emailHintText = resources.getString(R.string.email_Hint)
        val passwordHintText = resources.getString(R.string.password_Hint)

        // 힌트 텍스트에 스타일을 적용
        val ssEmail = SpannableString(emailHintText)
        val ssPassword = SpannableString(passwordHintText)
        val assEmail = AbsoluteSizeSpan(12, true) // 힌트 텍스트 크기 설정
        val assPassword = AbsoluteSizeSpan(12, true)
        ssEmail.setSpan(assEmail, 0, ssEmail.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // 크기 적용
        ssPassword.setSpan(assPassword, 0, ssPassword.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 힌트 텍스트 굵기 설정
        ssEmail.setSpan(StyleSpan(Typeface.NORMAL), 0, ssEmail.length, 0) // 굵게
        ssPassword.setSpan(StyleSpan(Typeface.NORMAL), 0, ssPassword.length, 0)

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        val emailText = findViewById<View>(R.id.editEmail) as EditText
        val passwordText = findViewById<View>(R.id.editPassword) as EditText
        emailText.hint = SpannedString(ssEmail) // 크기가 적용된 힌트 텍스트 설정
        passwordText.hint = SpannedString(ssPassword)
        emailText.setHintTextColor(Color.parseColor("#555555")) // 색 변
        passwordText.setHintTextColor(Color.parseColor("#555555"))
    }

    // 권한 응답 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATIONS_REQUEST_CODE) { // notification
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
            if (!isIgnoringBatteryOptimizations(this)) showBatteryOptimizationDialog(this) // 배터리 최적화 무시 요청
        }
    }

    companion object {
        private val NOTIFICATIONS_REQUEST_CODE = 3000
        fun encryptECB(key: String, value: String): String? {
            try {
                val skeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "AES")
                val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
                val encrypted = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
                return Base64.encodeToString(encrypted, Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}