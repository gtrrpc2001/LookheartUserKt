package com.mcuhq.simplebluetooth.profile

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.library.KTLibrary.viewmodel.SharedViewModel
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.fragment.ProfileFragment
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class Profile_3 : Fragment() {
    var viewModel: SharedViewModel? = null
    var retrofitServerManager: RetrofitServerManager? = null
    private var userDetailsSharedPref: SharedPreferences? = null
    private var userDetailsEditor: SharedPreferences.Editor? = null
    private var profile3_save: Button? = null
    private var profile3_arrcnt: EditText? = null
    private var profile3_guard_num1: EditText? = null
    private var profile3_guard_num2: EditText? = null

    interface KeyboardUpListener {
        fun onKeyboardUp(size: Int)
    }

    private val mListener: KeyboardUpListener? = null
    private var s_emergency = false
    private var s_arr = false
    private var s_muscle = false
    private var s_drop = false
    private var s_fastarr = false
    private var s_slowarr = false
    private var s_irregular = false
    private var s_guard = false
    private var profile3check = false
    private val s_arrcnt = 1
    private var email: String? = null
    var sv: ScrollView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_profile3, container, false)
        retrofitServerManager = RetrofitServerManager.getInstance()
        val emailSharedPreferences: SharedPreferences =
            activity?.getSharedPreferences("User", Context.MODE_PRIVATE)!!
        email = emailSharedPreferences.getString("email", "null")
        userDetailsSharedPref = activity?.getSharedPreferences(email, Context.MODE_PRIVATE)!!
        userDetailsEditor = userDetailsSharedPref?.edit()
        val btn1 = view.findViewById<Button>(R.id.profile3_emergency_off)
        val btn2 = view.findViewById<Button>(R.id.profile3_emergency_on)
        val btn3 = view.findViewById<Button>(R.id.profile3_arr_off)
        val btn4 = view.findViewById<Button>(R.id.profile3_arr_on)
        val btn5 = view.findViewById<Button>(R.id.profile3_muscle_off)
        val btn6 = view.findViewById<Button>(R.id.profile3_muscle_on)
        val btn7 = view.findViewById<Button>(R.id.profile3_drop_off)
        val btn8 = view.findViewById<Button>(R.id.profile3_drop_on)
        val btn9 = view.findViewById<Button>(R.id.profile3_fastarr_off)
        val btn10 = view.findViewById<Button>(R.id.profile3_fastarr_on)
        val btn11 = view.findViewById<Button>(R.id.profile3_slowarr_off)
        val btn12 = view.findViewById<Button>(R.id.profile3_slowarr_on)
        val btn13 = view.findViewById<Button>(R.id.profile3_irregular_off)
        val btn14 = view.findViewById<Button>(R.id.profile3_irregular_on)
        val btn15 = view.findViewById<Button>(R.id.profile3_guard_off)
        val btn16 = view.findViewById<Button>(R.id.profile3_guard_on)
        profile3_arrcnt = view.findViewById(R.id.profile3_arrcnt)
        val btn17 = view.findViewById<Button>(R.id.profile3_arrcnt_m)
        val btn18 = view.findViewById<Button>(R.id.profile3_arrcnt_p)
        profile3_save = view.findViewById(R.id.profile3_save)
        profile3_guard_num1 = view.findViewById(R.id.profile3_guard_num1)
        profile3_guard_num2 = view.findViewById(R.id.profile3_guard_num2)


        // sharedpreferences에서 가져오기
        s_emergency = userDetailsSharedPref?.getBoolean("s_emergency", true)!!
        s_arr = userDetailsSharedPref?.getBoolean("s_arr", true)!!
        s_muscle = userDetailsSharedPref?.getBoolean("s_muscle", false)!!
        s_drop = userDetailsSharedPref?.getBoolean("s_drop", true)!!
        s_fastarr = userDetailsSharedPref?.getBoolean("s_fastarr", false)!!
        s_slowarr = userDetailsSharedPref?.getBoolean("s_slowarr", false)!!
        s_irregular = userDetailsSharedPref?.getBoolean("s_irregular", false)!!
        s_guard = userDetailsSharedPref?.getBoolean("s_guard", false)!!
        profile3check = userDetailsSharedPref?.getBoolean("profile3check", false)!!
        val savedText1: String = userDetailsSharedPref?.getString("s_arrcnt", s_arrcnt.toString())!!
        val savedText2: String = userDetailsSharedPref?.getString("s_guard_num1", "")!!
        val savedText3: String = userDetailsSharedPref?.getString("s_guard_num2", "")!!
        profile3_arrcnt?.setText(savedText1)
        profile3_guard_num1?.setText(savedText2)
        profile3_guard_num2?.setText(savedText3)
        val s_arrcnt = intArrayOf(savedText1.toInt())
        profile3_arrcnt?.setText(s_arrcnt[0].toString())

        //버튼 초기 세팅
        btn1.setTextColor(Color.WHITE)
        btn1.setBackgroundColor(Color.DKGRAY)
        btn2.setTextColor(Color.BLACK)
        btn2.setBackgroundColor(Color.LTGRAY)
        btn3.setTextColor(Color.WHITE)
        btn3.setBackgroundColor(Color.DKGRAY)
        btn4.setTextColor(Color.BLACK)
        btn4.setBackgroundColor(Color.LTGRAY)
        btn5.setTextColor(Color.WHITE)
        btn5.setBackgroundColor(Color.DKGRAY)
        btn6.setTextColor(Color.BLACK)
        btn6.setBackgroundColor(Color.LTGRAY)
        btn7.setTextColor(Color.WHITE)
        btn7.setBackgroundColor(Color.DKGRAY)
        btn8.setTextColor(Color.BLACK)
        btn8.setBackgroundColor(Color.LTGRAY)
        btn9.setTextColor(Color.WHITE)
        btn9.setBackgroundColor(Color.DKGRAY)
        btn10.setTextColor(Color.BLACK)
        btn10.setBackgroundColor(Color.LTGRAY)
        btn11.setTextColor(Color.WHITE)
        btn11.setBackgroundColor(Color.DKGRAY)
        btn12.setTextColor(Color.BLACK)
        btn12.setBackgroundColor(Color.LTGRAY)
        btn13.setTextColor(Color.WHITE)
        btn13.setBackgroundColor(Color.DKGRAY)
        btn14.setTextColor(Color.BLACK)
        btn14.setBackgroundColor(Color.LTGRAY)
        btn15.setTextColor(Color.WHITE)
        btn15.setBackgroundColor(Color.DKGRAY)
        btn16.setTextColor(Color.BLACK)
        btn16.setBackgroundColor(Color.LTGRAY)

        // 해제/사용
        if (s_emergency == false) {
            btn1.setTextColor(Color.WHITE)
            btn1.setBackgroundColor(Color.DKGRAY)
            btn2.setTextColor(Color.BLACK)
            btn2.setBackgroundColor(Color.LTGRAY)
        } else if (s_emergency) {
            btn1.setTextColor(Color.BLACK)
            btn1.setBackgroundColor(Color.LTGRAY)
            btn2.setTextColor(Color.WHITE)
            btn2.setBackgroundColor(Color.DKGRAY)
        }
        if (s_arr == false) {
            btn3.setTextColor(Color.WHITE)
            btn3.setBackgroundColor(Color.DKGRAY)
            btn4.setTextColor(Color.BLACK)
            btn4.setBackgroundColor(Color.LTGRAY)
        } else if (s_arr) {
            btn3.setTextColor(Color.BLACK)
            btn3.setBackgroundColor(Color.LTGRAY)
            btn4.setTextColor(Color.WHITE)
            btn4.setBackgroundColor(Color.DKGRAY)
        }
        if (s_muscle == false) {
            btn5.setTextColor(Color.WHITE)
            btn5.setBackgroundColor(Color.DKGRAY)
            btn6.setTextColor(Color.BLACK)
            btn6.setBackgroundColor(Color.LTGRAY)
        } else if (s_muscle) {
            btn5.setTextColor(Color.BLACK)
            btn5.setBackgroundColor(Color.LTGRAY)
            btn6.setTextColor(Color.WHITE)
            btn6.setBackgroundColor(Color.DKGRAY)
        }
        if (s_drop == false) {
            btn7.setTextColor(Color.WHITE)
            btn7.setBackgroundColor(Color.DKGRAY)
            btn8.setTextColor(Color.BLACK)
            btn8.setBackgroundColor(Color.LTGRAY)
        } else if (s_drop) {
            btn7.setTextColor(Color.BLACK)
            btn7.setBackgroundColor(Color.LTGRAY)
            btn8.setTextColor(Color.WHITE)
            btn8.setBackgroundColor(Color.DKGRAY)
        }
        if (s_fastarr == false) {
            btn9.setTextColor(Color.WHITE)
            btn9.setBackgroundColor(Color.DKGRAY)
            btn10.setTextColor(Color.BLACK)
            btn10.setBackgroundColor(Color.LTGRAY)
        } else if (s_fastarr) {
            btn9.setTextColor(Color.BLACK)
            btn9.setBackgroundColor(Color.LTGRAY)
            btn10.setTextColor(Color.WHITE)
            btn10.setBackgroundColor(Color.DKGRAY)
        }
        if (s_slowarr == false) {
            btn11.setTextColor(Color.WHITE)
            btn11.setBackgroundColor(Color.DKGRAY)
            btn12.setTextColor(Color.BLACK)
            btn12.setBackgroundColor(Color.LTGRAY)
        } else if (s_slowarr) {
            btn11.setTextColor(Color.BLACK)
            btn11.setBackgroundColor(Color.LTGRAY)
            btn12.setTextColor(Color.WHITE)
            btn12.setBackgroundColor(Color.DKGRAY)
        }
        if (s_irregular == false) {
            btn13.setTextColor(Color.WHITE)
            btn13.setBackgroundColor(Color.DKGRAY)
            btn14.setTextColor(Color.BLACK)
            btn14.setBackgroundColor(Color.LTGRAY)
        } else if (s_irregular) {
            btn13.setTextColor(Color.BLACK)
            btn13.setBackgroundColor(Color.LTGRAY)
            btn14.setTextColor(Color.WHITE)
            btn14.setBackgroundColor(Color.DKGRAY)
        }
        if (s_guard == false) {
            btn15.setTextColor(Color.WHITE)
            btn15.setBackgroundColor(Color.DKGRAY)
            btn16.setTextColor(Color.BLACK)
            btn16.setBackgroundColor(Color.LTGRAY)
        } else if (s_guard) {
            btn15.setTextColor(Color.BLACK)
            btn15.setBackgroundColor(Color.LTGRAY)
            btn16.setTextColor(Color.WHITE)
            btn16.setBackgroundColor(Color.DKGRAY)
        }
        btn1.setOnClickListener {
            s_emergency = false
            //버튼효과
            btn1.setTextColor(Color.WHITE)
            btn1.setBackgroundColor(Color.DKGRAY)
            btn2.setTextColor(Color.BLACK)
            btn2.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn2.setOnClickListener {
            s_emergency = true
            btn1.setTextColor(Color.BLACK)
            btn1.setBackgroundColor(Color.LTGRAY)
            btn2.setTextColor(Color.WHITE)
            btn2.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn3.setOnClickListener {
            s_arr = false
            btn3.setTextColor(Color.WHITE)
            btn3.setBackgroundColor(Color.DKGRAY)
            btn4.setTextColor(Color.BLACK)
            btn4.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn4.setOnClickListener {
            s_arr = true
            btn3.setTextColor(Color.BLACK)
            btn3.setBackgroundColor(Color.LTGRAY)
            btn4.setTextColor(Color.WHITE)
            btn4.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn5.setOnClickListener {
            s_muscle = false
            btn5.setTextColor(Color.WHITE)
            btn5.setBackgroundColor(Color.DKGRAY)
            btn6.setTextColor(Color.BLACK)
            btn6.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn6.setOnClickListener {
            s_muscle = true
            btn5.setTextColor(Color.BLACK)
            btn5.setBackgroundColor(Color.LTGRAY)
            btn6.setTextColor(Color.WHITE)
            btn6.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn7.setOnClickListener {
            s_drop = false
            btn7.setTextColor(Color.WHITE)
            btn7.setBackgroundColor(Color.DKGRAY)
            btn8.setTextColor(Color.BLACK)
            btn8.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn8.setOnClickListener {
            s_drop = true
            btn7.setTextColor(Color.BLACK)
            btn7.setBackgroundColor(Color.LTGRAY)
            btn8.setTextColor(Color.WHITE)
            btn8.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn9.setOnClickListener {
            s_fastarr = false
            btn9.setTextColor(Color.WHITE)
            btn9.setBackgroundColor(Color.DKGRAY)
            btn10.setTextColor(Color.BLACK)
            btn10.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn10.setOnClickListener {
            s_fastarr = true
            btn9.setTextColor(Color.BLACK)
            btn9.setBackgroundColor(Color.LTGRAY)
            btn10.setTextColor(Color.WHITE)
            btn10.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn11.setOnClickListener {
            s_slowarr = false
            btn11.setTextColor(Color.WHITE)
            btn11.setBackgroundColor(Color.DKGRAY)
            btn12.setTextColor(Color.BLACK)
            btn12.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn12.setOnClickListener {
            s_slowarr = true
            btn11.setTextColor(Color.BLACK)
            btn11.setBackgroundColor(Color.LTGRAY)
            btn12.setTextColor(Color.WHITE)
            btn12.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn13.setOnClickListener {
            s_irregular = false
            btn13.setTextColor(Color.WHITE)
            btn13.setBackgroundColor(Color.DKGRAY)
            btn14.setTextColor(Color.BLACK)
            btn14.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn14.setOnClickListener {
            s_irregular = true
            btn13.setTextColor(Color.BLACK)
            btn13.setBackgroundColor(Color.LTGRAY)
            btn14.setTextColor(Color.WHITE)
            btn14.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn15.setOnClickListener {
            s_guard = false
            btn15.setTextColor(Color.WHITE)
            btn15.setBackgroundColor(Color.DKGRAY)
            btn16.setTextColor(Color.BLACK)
            btn16.setBackgroundColor(Color.LTGRAY)
            savesetting()
        }
        btn16.setOnClickListener {
            s_guard = true
            btn15.setTextColor(Color.BLACK)
            btn15.setBackgroundColor(Color.LTGRAY)
            btn16.setTextColor(Color.WHITE)
            btn16.setBackgroundColor(Color.DKGRAY)
            savesetting()
        }
        btn17.setOnClickListener {
            s_arrcnt[0]--
            profile3_arrcnt?.setText(s_arrcnt[0].toString())
        }
        btn18.setOnClickListener {
            s_arrcnt[0]++
            profile3_arrcnt?.setText(s_arrcnt[0].toString())
        }

        //저장버튼으로 알림발생 기준 횟수, 보호자연락처1,2 저장
        profile3_save?.setOnClickListener(View.OnClickListener {
            val textToSave1: String = profile3_arrcnt?.getText().toString()
            val textToSave2: String = profile3_guard_num1?.getText().toString()
            val textToSave3: String = profile3_guard_num2?.getText().toString()
            val guardianList = ArrayList<String>()
            userDetailsEditor?.putString("s_arrcnt", textToSave1)
            userDetailsEditor?.putString("s_guard_num1", textToSave2)
            userDetailsEditor?.putString("s_guard_num2", textToSave3)
            userDetailsEditor?.putBoolean("profile3check", true)
            userDetailsEditor?.apply()
            if (!textToSave2.isEmpty()) guardianList.add(textToSave2)
            if (!textToSave3.isEmpty()) guardianList.add(textToSave3)
            if (!textToSave2.isEmpty() || !textToSave3.isEmpty()) saveGuardianToServer(guardianList)

            // 저장한 후, 필요한 작업 수행 (예: 토스트 메시지 표시 등)
            Toast.makeText(activity, resources.getString(R.string.saveData), Toast.LENGTH_SHORT)
                .show()
        })

        //입력할때 키보드에 대한 높이조절
        profile3_arrcnt?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {

//                KeyboardUp(1200);
                someMethod(500)
            }
        })
        profile3_guard_num1?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {

//                KeyboardUp(1400);
//                someMethod(700);
                adjustForEditText(profile3_guard_num1)
            }
        })
        profile3_guard_num2?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {

//                KeyboardUp(1500);
//                someMethod(800);
                adjustForEditText(profile3_guard_num2)
            }
        })
        return view
    }

    private fun savesetting() {
        // SharedPreferences에 알림설정 저장
        userDetailsEditor?.putBoolean("s_emergency", s_emergency)
        userDetailsEditor?.putBoolean("s_arr", s_arr)
        userDetailsEditor?.putBoolean("s_muscle", s_muscle)
        userDetailsEditor?.putBoolean("s_drop", s_drop)
        userDetailsEditor?.putBoolean("s_fastarr", s_fastarr)
        userDetailsEditor?.putBoolean("s_slowarr", s_slowarr)
        userDetailsEditor?.putBoolean("s_irregular", s_irregular)
        userDetailsEditor?.putBoolean("s_guard", s_guard)
        userDetailsEditor?.apply()
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel?.setEmergency(s_emergency)
        viewModel?.setArr(s_arr)
        viewModel?.setMyo(s_muscle)
        viewModel?.setNonContact(s_drop)
        viewModel?.setFastarr(s_fastarr)
        viewModel?.setSlowarr(s_slowarr)
        viewModel?.setIrregular(s_irregular)
    }

    fun KeyboardUp(size: Int) {
        sv?.postDelayed(Runnable { sv?.smoothScrollTo(0, size) }, 200)
    }

    fun adjustForEditText(editText: EditText?) {
        val location = IntArray(2)
        editText?.getLocationOnScreen(location)
        val yPosition = location[1]
        someMethod(yPosition)
    }

    fun someMethod(size: Int) {
        val parentFragment: ProfileFragment? = parentFragment as ProfileFragment?
        if (parentFragment != null) {
            parentFragment.controlScroll(size)
        }
    }

    private fun saveGuardianToServer(guardian: ArrayList<String>) {
        val utcOffsetAndCountry = timeZone()
        val writeTime = currentUtcTime()
        retrofitServerManager?.setGuardian(
            email!!,
            utcOffsetAndCountry,
            writeTime,
            guardian,
            object : RetrofitServerManager.ServerTaskCallback {
                override fun onSuccess(result: String?) {
                    Log.i("saveGuardianToServer", result!!)
                    if (result.lowercase(Locale.getDefault()).contains("true")) {
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.setGuardianComp),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.setGuardianFail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(e: Exception?) {
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.setGuardianFail),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("saveGuardianToServer", "send Err")
                    e!!.printStackTrace()
                }
            })
    }

    fun timeZone(): String {
        // 국가 코드
        val current = requireActivity().resources.configuration.locales[0]
        val currentCountry = current.country
        val utcOffset: String
        val utcOffsetAndCountry: String

        // 현재 시스템의 기본 타임 존
        val currentTimeZone = TimeZone.getDefault()

        // 타임 존의 아이디
        val timeZoneId = currentTimeZone.id
        val zoneId = ZoneId.of(timeZoneId)
        val offset = LocalDateTime.now().atZone(zoneId).offset
        val utcTime = offset.toString()
        val firstChar = utcTime[0].toString()
        utcOffset = if (firstChar == "+" || firstChar == "-") {
            utcTime
        } else {
            "+$utcTime"
        }
        utcOffsetAndCountry = "$utcOffset/$currentCountry"
        return utcOffsetAndCountry
    }

    fun currentUtcTime(): String {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val currentTimeZone = TimeZone.getDefault()
        val timeZoneId = currentTimeZone.id
        val currentTimezone =
            now.withZoneSameInstant(ZoneId.of(timeZoneId))
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return formatter.format(currentTimezone)
    }
}