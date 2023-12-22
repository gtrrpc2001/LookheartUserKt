package com.mcuhq.simplebluetooth.auth

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.ServerTaskCallback
import com.mcuhq.simplebluetooth.server.RetrofitServerManager.getInstance
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Find_Email : AppCompatActivity() {
    var retrofitServerManager: RetrofitServerManager? = null
    private var findEmail_name: EditText? = null
    private var findEmail_number: EditText? = null
    private var findEmail_birth_btn: ImageButton? = null
    private var findEmailBtn: Button? = null
    private var userName: String? = null
    private var userPhonenumber: String? = null
    private var userBirthday: String? = null
    private var find_birth_T: TextView? = null
    private var findEmail_age: TextView? = null
    private var finedID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_email)
        retrofitServerManager = getInstance()
        setViewID()
        setButtonEvent()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this@Find_Email,
            R.style.RoundedDatePickerDialog,
            { view, selectedYear, selectedMonth, selectedDayOfMonth ->
                calendar[Calendar.YEAR] = selectedYear
                calendar[Calendar.MONTH] = selectedMonth
                calendar[Calendar.DAY_OF_MONTH] = selectedDayOfMonth
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val selectedDate = sdf.format(calendar.time)

                // 선택된 날짜를 입력 필드에 표시
                find_birth_T!!.text = selectedDate

                // 나이 계산 및 표시
                val age = calculateAge(calendar.time)
                findEmail_age!!.text = resources.getString(R.string.age) + ": " + age
            },
            year, month, dayOfMonth
        )
        datePickerDialog.show()
    }

    fun setButtonEvent() {
        findEmail_birth_btn!!.setOnClickListener { showDatePickerDialog() }
        findEmailBtn!!.setOnClickListener {
            userName = findEmail_name!!.text.toString().trim { it <= ' ' } // EditText의 내용을 변수에 저장
            userPhonenumber = findEmail_number!!.text.toString().trim { it <= ' ' }
            userBirthday = find_birth_T!!.text.toString().trim { it <= ' ' }
            if (userName!!.isEmpty() || userPhonenumber!!.isEmpty() || userBirthday!!.isEmpty()) {
                showAlertDialog() // 비어있는 경우 알림창 띄우기
            } else {
                // 입력이 있으면 처리 로직 추가
                // userInput 변수에 저장된 내용 사용 가능
                try {
                    retrofitServerManager!!.findID(
                        userName!!,
                        userPhonenumber!!,
                        userBirthday!!,
                        object : ServerTaskCallback {
                            override fun onSuccess(id: String?) {
                                Log.e("resultemail", id!!)
                                finedID = id
                                runOnUiThread { showEmail() }
                            }

                            override fun onFailure(e: Exception?) {}
                        })
                } catch (e: Exception) {
                }
            }
        }
    }

    fun setViewID() {
        findEmail_name = findViewById(R.id.findEmail_name)
        findEmail_number = findViewById(R.id.findEmail_number)
        findEmail_birth_btn = findViewById(R.id.findEmail_birth_btn)
        findEmailBtn = findViewById(R.id.findEmailBtn)
        find_birth_T = findViewById(R.id.find_birth_T)
        findEmail_age = findViewById(R.id.findEmail_age)
    }

    private fun calculateAge(birthDate: Date): Int {
        val birthCalendar = Calendar.getInstance()
        birthCalendar.time = birthDate
        val currentCalendar = Calendar.getInstance()
        var age = currentCalendar[Calendar.YEAR] - birthCalendar[Calendar.YEAR]
        if (currentCalendar[Calendar.DAY_OF_YEAR] < birthCalendar[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.noti))
            .setMessage(resources.getString(R.string.enterInfo))
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun showEmail() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.findIDResult))
            .setMessage(finedID) // 받은 id 값을 사용하여 메시지 설정
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which -> dialog.dismiss() }
            .show()
    }
}