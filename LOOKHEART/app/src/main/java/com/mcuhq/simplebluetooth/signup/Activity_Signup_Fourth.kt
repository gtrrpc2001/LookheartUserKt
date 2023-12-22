package com.mcuhq.simplebluetooth.signup

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.activity.login.Activity_Login
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.ServerTaskCallback
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.getInstance
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Activity_Signup_Fourth : AppCompatActivity() {
    /*pattern*/ //region
    val namePattern = "^[^\\d\\W]+$"
    val numberPattern = "^[0-9]{1,3}$"

    //endregion
    /*EditText*/ //region
    lateinit var nameEditText: EditText
    lateinit var heightEditText: EditText
    lateinit var weightEditText: EditText

    //endregion
    /*TextView*/ //region
    lateinit var nameHelp: TextView
    lateinit var heightHelp: TextView
    lateinit var weightHelp: TextView
    lateinit var birthdayText: TextView

    //endregion
    /*LinearLayout*/ //region
    var male: LinearLayout? = null
    var female: LinearLayout? = null

    //endregion
    /*ImageView*/ //region
    var maleImg: ImageView? = null
    var femaleImg: ImageView? = null

    //endregion
    /*button*/ //region
    var birthdayButton: Button? = null
    var completeButton: Button? = null
    var backButton: Button? = null

    //endregion
    /*입력 체크*/ //region
    lateinit var dataCheck: MutableMap<String, Boolean>
    var nameCheck: Boolean = false
    var heightCheck: Boolean = false
    var weightCheck: Boolean = false
    var genderCheck: Boolean = false
    var birthdayCheck: Boolean = false

    //endregion
    var age = 0
    var sv: ScrollView? = null
    private var name: String? = null
    private var height: String? = null
    private var weight: String? = null
    private var gender: String? = null // true : male, false : female
    private var birthday: String? = null
    var serverSignupCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_fourth)

        // EditText
        nameEditText = findViewById(R.id.editName)
        heightEditText = findViewById(R.id.editHeight)
        weightEditText = findViewById(R.id.editWeight)

        // 유효성 도움말
        nameHelp = findViewById(R.id.nameHelp)
        heightHelp = findViewById(R.id.heightHelp)
        weightHelp = findViewById(R.id.weightHelp)

        // male, female
        male = findViewById(R.id.male)
        female = findViewById(R.id.female)
        maleImg = findViewById(R.id.maleImg)
        femaleImg = findViewById(R.id.femaleImg)

        // 생년월일 버튼, 텍스트
        birthdayButton = findViewById(R.id.birthday)
        birthdayText = findViewById(R.id.birthdayText)

        // 뒤로가기, 가입완료 버튼
        backButton = findViewById(R.id.signup_fourth_back)
        completeButton = findViewById(R.id.signup_complete)
        sv = findViewById(R.id.scrollView)

        // editText Hint 설정
        setHintText()

        // 초기화
        nameCheck = false
        heightCheck = false
        weightCheck = false
        genderCheck = false
        birthdayCheck = false

        // 입력 데이터 초기화
        dataCheck = HashMap()
        dataCheck["name"] = false
        dataCheck["height"] = false
        dataCheck["weight"] = false
        dataCheck["birthday"] = false

        // editText Color
        val notMatchColor = ColorStateList.valueOf(Color.RED)
        val highlightColor = ColorStateList.valueOf(Color.parseColor("#62AFFF"))

        // Name etitText focus
        nameEditText?.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 포커스가 없어질 때
                if (nameCheck) {
                    // 입력 값이 유효한 경우
                } else {
                    // 입력 값이 유효하지 않은 경우
                    nameHelp.setVisibility(View.VISIBLE)
                    ViewCompat.setBackgroundTintList(nameEditText, notMatchColor)
                }
            } else {
                // 포커스를 얻었을 때
                ViewCompat.setBackgroundTintList(nameEditText, highlightColor)
                KeyboardUp(0)
            }
        })
        // Name etitText input
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                name = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(namePattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck["name"] = true
                    nameHelp.setVisibility(View.GONE)
                } else {
                    // 매칭되지 않는 경우
                    dataCheck["name"] = false
                }
                // 유효성 체크
                nameCheck = dataCheck.get("name")!!
            }
        })

        // Height etitText focus
        heightEditText.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 포커스가 없어질 때
                if (heightCheck) {
                    // 입력 값이 유효한 경우
                } else {
                    // 입력 값이 유효하지 않은 경우
                    heightHelp.setVisibility(View.VISIBLE)
                    ViewCompat.setBackgroundTintList(heightEditText, notMatchColor)
                }
            } else {
                // 포커스를 얻었을 때
                ViewCompat.setBackgroundTintList(heightEditText, highlightColor)
                KeyboardUp(0)
            }
        })
        // Height etitText input
        heightEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                height = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(numberPattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck["height"] = true
                    heightHelp.setVisibility(View.GONE)
                } else {
                    // 매칭되지 않는 경우
                    dataCheck["height"] = false
                }
                // 유효성 체크
                heightCheck = dataCheck.get("height")!!
            }
        })

        // Weight etitText focus
        weightEditText.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 포커스가 없어질 때
                if (weightCheck) {
                    // 입력 값이 유효한 경우
                } else {
                    // 입력 값이 유효하지 않은 경우
                    weightHelp.setVisibility(View.VISIBLE)
                    ViewCompat.setBackgroundTintList(weightEditText, notMatchColor)
                }
            } else {
                // 포커스를 얻었을 때
                ViewCompat.setBackgroundTintList(weightEditText, highlightColor)
                KeyboardUp(0)
            }
        })
        // Weight etitText input
        weightEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                weight = s.toString()
                if (s.toString().trim { it <= ' ' }.matches(numberPattern.toRegex())) {
                    // 매칭되는 경우
                    dataCheck["weight"] = true
                    weightHelp.setVisibility(View.GONE)
                } else {
                    // 매칭되지 않는 경우
                    dataCheck["weight"] = false
                }
                // 유효성 체크
                weightCheck = dataCheck.get("weight")!!
            }
        })

        // gender Button Click Event
        male?.setOnClickListener(View.OnClickListener {
            // 이미지 변경
            maleImg?.setImageResource(R.drawable.signup_radiobutton_press)
            femaleImg?.setImageResource(R.drawable.signup_radiobutton_normal)
            gender = "남자" // male
            genderCheck = true // 입력 체크
        })
        female?.setOnClickListener(View.OnClickListener {
            // 이미지 변경
            femaleImg?.setImageResource(R.drawable.signup_radiobutton_press)
            maleImg?.setImageResource(R.drawable.signup_radiobutton_normal)
            gender = "여자" // female
            genderCheck = true // 입력 체크
        })
        birthdayButton?.setOnClickListener(View.OnClickListener {
            val c = Calendar.getInstance()
            val year = 1968
            val month = 0
            val day = 1

            // Create a new instance of DatePickerDialog with spinner style
            val datePickerDialog = DatePickerDialog(
                this@Activity_Signup_Fourth,
                R.style.RoundedDatePickerDialog,  // 새로운 스타일 적용,
                { view, year, month, dayOfMonth -> // Do something with the chosen date
                    getAge(year, month + 1, dayOfMonth)
                    val formattedDate = resources.getString(
                        R.string.formatted_date,
                        year,
                        month + 1,  // 0부터 시작하므로 1을 더함
                        dayOfMonth
                    )
                    birthday =
                        year.toString() + "-" + (month + 1).toString() + "-" + dayOfMonth.toString()
                    birthdayText.setText(formattedDate)
                    birthdayCheck = true
                }, year, month, day
            )
            if (datePickerDialog.window != null) {
                datePickerDialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                datePickerDialog.window!!.setGravity(Gravity.CENTER)
            }
            // 데이트피커 제목을 설정합니다.
//                datePickerDialog.setTitle("생년월일을 선택하세요");

            // 데이트피커 확인 버튼을 설정합니다.
            datePickerDialog.show()
        })

        // 뒤로가기
        backButton?.setOnClickListener(View.OnClickListener { onBackPressed() })

        // 로그인 완료
        completeButton?.setOnClickListener(View.OnClickListener {
            // 입력값 유효성 체크
            if (nameCheck!! && heightCheck!! && weightCheck!! && genderCheck!! && birthdayCheck!!) {
                val sharedPref = getSharedPreferences("UserDetails", MODE_PRIVATE)
                val editor = sharedPref.edit()
                val email = sharedPref.getString("email", "null")
                val pw = sharedPref.getString("password", "null")
                val retrofitServerManager = getInstance()
                Log.e("pw", pw!!)

                // API 호출
                retrofitServerManager!!.setSignupData(
                    email!!,
                    pw,
                    name!!,
                    gender!!,
                    height!!,
                    weight!!,
                    age.toString(),
                    birthday!!,
                    object : ServerTaskCallback {
                        override fun onSuccess(result: String?) {
                            // 서버 응답 성공 처리
                            if (result!!.lowercase(Locale.getDefault()).contains("true")) {
                                // 회원가입 성공
                                runOnUiThread { completeLogin() }
                            } else {
                                // 회원가입 실패
                                Log.e("signupResult", result)
                                runOnUiThread {
                                    Toast.makeText(
                                        this@Activity_Signup_Fourth,
                                        "회원가입 실패",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(e: Exception?) {
                            // 서버 응답 실패 처리
                            // 에러 메시지 표시 등
                            runOnUiThread {
                                Toast.makeText(
                                    this@Activity_Signup_Fourth,
                                    "서버 응답 없음",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Log.e("err", e.toString())
                        }
                    })
            } else {
                var alertString = ""
                if (!nameCheck!!) {
                    alertString += resources.getString(R.string.name_Label) + ", "
                }
                if (!heightCheck!!) {
                    alertString += resources.getString(R.string.height) + ", "
                }
                if (!weightCheck!!) {
                    alertString += resources.getString(R.string.weight) + ", "
                }
                if (gender == null) {
                    alertString += resources.getString(R.string.gender) + ","
                }
                if (!birthdayCheck!!) {
                    alertString += resources.getString(R.string.birthday_Label) + ", "
                }

                // 알림창
                val builder = AlertDialog.Builder(this@Activity_Signup_Fourth)
                builder.setTitle(resources.getString(R.string.noti))
                    .setMessage(
                        alertString.substring(
                            0,
                            alertString.length - 2
                        ) + resources.getString(R.string.enterAgain)
                    )
                    .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                        // 취소 버튼 클릭 시 수행할 동작
                        dialog.cancel() // 팝업창 닫기
                    }
                    .show()
            }
        })
    }

    fun SaveUserData() {
        val currentDate = currentDate
        val sharedPref = getSharedPreferences("UserDetails", MODE_PRIVATE)
        val email = sharedPref.getString("email", "null")
        val emailSharedPref = getSharedPreferences(email, MODE_PRIVATE)
        val editor = emailSharedPref.edit()
        editor.putBoolean("agree", false)
        editor.putBoolean("privacy", false)

        // 기본 정보
        editor.putString("name", name)
        editor.putString("height", height)
        editor.putString("weight", weight)
        editor.putString("gender", gender)
        editor.putString("birthday", birthday)
        editor.putString("current_date", currentDate)
        editor.putString("sleep1", "23")
        editor.putString("sleep2", "7")

        // 기본 목표량
        editor.putString("o_bpm", "90") // 활동 기준 bpm
        editor.putString("o_cal", "3000") // 일일 목표 총 칼로리
        editor.putString("o_ecal", "500") // 일일 목표 활동 칼로리
        editor.putString("o_step", "2000") // 일일 목표 걸음수
        editor.putString("o_distance", "5") // 일일 목표 걸음거리

        // 기본 설정 알람
        editor.putBoolean("s_emergency", true) // 응급 상황
        editor.putBoolean("s_arr", true) // 비정상 맥박
        editor.putBoolean("s_drop", true) // 전극 떨어짐
        editor.putBoolean("s_muscle", false) // 근전도
        editor.putBoolean("s_slowarr", false) // 서맥
        editor.putBoolean("s_fastarr", false) // 빈맥
        editor.putBoolean("s_irregular", false) // 불규칙 맥박
        editor.apply() // 비동기 저장
    }

    private val currentDate: String
        private get() {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(calendar.time)
        }

    fun KeyboardUp(size: Int) {
        sv!!.postDelayed({ sv!!.smoothScrollTo(0, size) }, 200)
    }

    fun setHintText() {
        // EditText에 힌트 텍스트 스타일을 적용
        val nameHintText = resources.getString(R.string.nameHintText)
        val heightHintText = resources.getString(R.string.heightHintText)
        val weightHintText = resources.getString(R.string.weightHintText)

        // 힌트 텍스트에 스타일을 적용
        val ssName = SpannableString(nameHintText)
        val ssHeight = SpannableString(heightHintText)
        val ssWeight = SpannableString(weightHintText)
        val assName = AbsoluteSizeSpan(13, true) // 힌트 텍스트 크기 설정
        val assHeight = AbsoluteSizeSpan(13, true)
        val assWeight = AbsoluteSizeSpan(13, true)
        ssName.setSpan(assName, 0, ssName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // 크기 적용
        ssHeight.setSpan(assHeight, 0, ssHeight.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssWeight.setSpan(assWeight, 0, ssWeight.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 힌트 텍스트 굵기 설정
        ssName.setSpan(StyleSpan(Typeface.NORMAL), 0, ssName.length, 0) // 굵게
        ssHeight.setSpan(StyleSpan(Typeface.NORMAL), 0, ssHeight.length, 0)
        ssWeight.setSpan(StyleSpan(Typeface.NORMAL), 0, ssWeight.length, 0)

        // 스타일이 적용된 힌트 텍스트를 EditText에 설정
        val nameText = findViewById<View>(R.id.editName) as EditText
        val heightText = findViewById<View>(R.id.editHeight) as EditText
        val weightText = findViewById<View>(R.id.editWeight) as EditText
        nameText.hint = SpannedString(ssName) // 크기가 적용된 힌트 텍스트 설정
        heightText.hint = SpannedString(ssHeight)
        weightText.hint = SpannedString(ssWeight)
        nameText.setHintTextColor(Color.parseColor("#555555")) // 색 변경
        heightText.setHintTextColor(Color.parseColor("#555555"))
        weightText.setHintTextColor(Color.parseColor("#555555"))
    }

    fun getAge(year: Int, month: Int, day: Int) {
        // 현재 시간
        val today = Calendar.getInstance()
        val currentYear = today[Calendar.YEAR]
        val currentMonth = today[Calendar.MONTH] + 1 // 0-based index
        val currentDay = today[Calendar.DAY_OF_MONTH]
        age = currentYear - year
        if (month > currentMonth || month == currentMonth && day > currentDay) {
            age = age - 1
        }
    }

    fun completeLogin() {
        val builder = AlertDialog.Builder(this@Activity_Signup_Fourth)
        builder.setTitle(resources.getString(R.string.signup_complete))
            .setMessage(resources.getString(R.string.returnLogin))
            .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                // 데이터 저장
                SaveUserData()

                // 기존 stack에 있는 activity 모두 종료 시키고 login Activity로 이동
                val intent = Intent(this@Activity_Signup_Fourth, Activity_Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                dialog.cancel() // 팝업창 닫기
            }
            .show()
    }
}