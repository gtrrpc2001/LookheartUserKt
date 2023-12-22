package com.mcuhq.simplebluetooth.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.library.KTLibrary.viewmodel.SharedViewModel
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Profile_1 : Fragment() {
    var viewModel: SharedViewModel? = null
    private val saveButton: Button? = null
    var retrofitServerManager: RetrofitServerManager? = null
    private var birthbutton: ImageButton? = null
    private val editText3: TextView? = null
    private var ageTextView: TextView? = null
    private var userDetailsSharedPref: SharedPreferences? = null
    private var userDetailsEditor: SharedPreferences.Editor? = null
    private var myCalendar: Calendar? = null
    private var dateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var profile1_name: EditText? = null
    private var profile1_number: EditText? = null
    private var profile1_height: EditText? = null
    private var profile1_weight: EditText? = null
    private var profile1_sleep1: EditText? = null
    private var profile1_sleep2: EditText? = null
    private var profile1check = false
    private var profile1_birth: TextView? = null
    private var profile1_gender: TextView? = null
    private var email: String? = null
    var sv: ScrollView? = null
    var age = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_profile1, container, false)
        sv = view.findViewById<ScrollView>(R.id.scrollView1)
        retrofitServerManager = RetrofitServerManager.getInstance()
        val emailSharedPreferences: SharedPreferences =
            activity?.getSharedPreferences("User", Context.MODE_PRIVATE)!!
        email = emailSharedPreferences.getString("email", "null")

        // SharedPreferences 객체 얻기
        userDetailsSharedPref = activity?.getSharedPreferences(email, Context.MODE_PRIVATE)
        userDetailsEditor = userDetailsSharedPref?.edit()
        val saveButton = view.findViewById<Button>(R.id.profile1_save)
        birthbutton = view.findViewById<ImageButton>(R.id.profile1_calendar)
        ageTextView = view.findViewById<TextView>(R.id.profile1_age)

        // 이미 저장된 생년월일이 있을 경우, TextView에 나이 표시
        val savedBirthday: String = userDetailsSharedPref?.getString("birthday", "")!!
        ageTextView?.setText(calculateAge(savedBirthday))

        // DatePickerDialog의 리스너를 초기화합니다.
        myCalendar = Calendar.getInstance()
        dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
                val birthday = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                ageTextView?.setText(calculateAge(birthday))

                // 생년월일을 선택한 후, 선택한 날짜를 EditText에 표시합니다.
                myCalendar?.set(Calendar.YEAR, year)
                myCalendar?.set(Calendar.MONTH, month)
                myCalendar?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                userDetailsEditor?.putString("birthday", birthday)
                userDetailsEditor?.apply()
                updateEditText()
            }
        }

        //생년월일 설정
        birthbutton?.setOnClickListener(View.OnClickListener {
            val c = Calendar.getInstance()
            val myBirthday = profile1_birth?.getText() as String
            val spMyBirthday =
                myBirthday.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val year = spMyBirthday[0].toInt()
            val month = spMyBirthday[1].toInt() - 1
            val day = spMyBirthday[2].toInt()

            // Create a new instance of DatePickerDialog with spinner style
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.RoundedDatePickerDialog,  // 새로운 스타일 적용,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        val birthday = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                        ageTextView!!.setText(calculateAge(birthday))
                        userDetailsEditor?.putString("birthday", birthday)
                        userDetailsEditor?.apply()
                        profile1_birth?.setText(birthday)
                    }
                }, year, month, day
            )
            if (datePickerDialog.getWindow() != null) {
                datePickerDialog.getWindow()!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                datePickerDialog.getWindow()!!.setGravity(Gravity.CENTER)
            }
            // 데이트피커 제목을 설정합니다.
//                datePickerDialog.setTitle("생년월일을 선택하세요");

            // 데이트피커 확인 버튼을 설정합니다.
            datePickerDialog.show()
        })


        //개인정보 edittext
        profile1_name = view.findViewById<EditText>(R.id.profile1_name)
        profile1_number = view.findViewById<EditText>(R.id.profile1_number)
        profile1_birth = view.findViewById<TextView>(R.id.profile1_birth)
        profile1_height = view.findViewById<EditText>(R.id.profile1_height)
        profile1_weight = view.findViewById<EditText>(R.id.profile1_weight)
        profile1_gender = view.findViewById<TextView>(R.id.profile1_gender)
        profile1_sleep1 = view.findViewById<EditText>(R.id.profile1_sleep1)
        profile1_sleep2 = view.findViewById<EditText>(R.id.profile1_sleep2)
        val args: Bundle? = arguments


        //SharedPreferences에서 개인정보 불러오기
        val savedText1: String = userDetailsSharedPref?.getString("name", "null")!!
        val savedText2: String = userDetailsSharedPref?.getString("number", "01012345678")!!
        val savedText3: String = userDetailsSharedPref?.getString("birthday", "")!!
        val savedText4: String = userDetailsSharedPref?.getString("height", "180")!!
        val savedText5: String = userDetailsSharedPref?.getString("weight", "72")!!
        val savedText6: String = userDetailsSharedPref?.getString("gender", "남자")!!
        val savedText7: String = userDetailsSharedPref?.getString("sleep1", "23")!!
        val savedText8: String = userDetailsSharedPref?.getString("sleep2", "7")!!
        profile1check = userDetailsSharedPref?.getBoolean("profile1check", false)!!
        profile1_name?.setText(savedText1)
        profile1_number?.setText(savedText2)
        profile1_birth?.setText(savedText3)
        profile1_height?.setText(savedText4)
        profile1_weight?.setText(savedText5)
        profile1_gender?.setText(savedText6)
        profile1_sleep1?.setText(savedText7)
        profile1_sleep2?.setText(savedText8)


        //개인정보 저장버튼
        saveButton.setOnClickListener {
            val textToSave1: String = profile1_name?.getText().toString()
            val textToSave2: String = profile1_number?.getText().toString()
            val textToSave3: String = profile1_birth?.getText().toString()
            val textToSave4: String = profile1_gender?.getText().toString()
            val textToSave5: String = profile1_height?.getText().toString()
            val textToSave6: String = profile1_weight?.getText().toString()
            val textToSave7: String = profile1_sleep1?.getText().toString()
            val textToSave8: String = profile1_sleep2?.getText().toString()

            // SharedPreferences를 이용하여 데이터 저장
            userDetailsEditor?.putString("name", textToSave1)
            userDetailsEditor?.putString("number", textToSave2)
            userDetailsEditor?.putString("birthday", textToSave3)
            userDetailsEditor?.putString("age", calculateAge(textToSave3))
            userDetailsEditor?.putString("gender", textToSave4)
            userDetailsEditor?.putString("height", textToSave5)
            userDetailsEditor?.putString("weight", textToSave6)
            userDetailsEditor?.putString("sleep1", textToSave7)
            userDetailsEditor?.putString("sleep2", textToSave8)
            userDetailsEditor?.putBoolean("profile1check", true)
            userDetailsEditor?.apply()
            viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
            viewModel?.setAge(calculateAge(textToSave3))
            viewModel?.setGender(textToSave4)
            viewModel?.setHeight(textToSave5)
            viewModel?.setWeight(textToSave6)
            viewModel?.setSleep(textToSave7)
            viewModel?.setWakeup(textToSave8)
            saveProfileData()

            // 저장한 후, 필요한 작업 수행 (예: 토스트 메시지 표시 등)
//                Toast.makeText(getActivity(), getResources().getString(R.string.saveData), Toast.LENGTH_SHORT).show();
        }
        profile1_name?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(100)
            }
        })

        //입력할때 키보드에 대한 높이조절
        profile1_number?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(300)
            }
        })
        profile1_height?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(700)
            }
        })
        profile1_weight?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(700)
            }
        })
        profile1_sleep1?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(900)
            }
        })
        profile1_sleep2?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(900)
            }
        })
        return view
    }

    //생년월일입력
    private fun updateEditText() {
        val myFormat = "yyyy-MM-dd" // 날짜 포맷을 지정합니다.
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        profile1_birth?.setText(sdf.format(myCalendar!!.time))
    }

    //나이 계산
    private fun calculateAge(birthDate: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val birthDateObj = sdf.parse(birthDate)
            val birthCalendar = Calendar.getInstance()
            birthCalendar.time = birthDateObj
            val nowCalendar = Calendar.getInstance()
            age = nowCalendar[Calendar.YEAR] - birthCalendar[Calendar.YEAR]
            if (nowCalendar[Calendar.MONTH] < birthCalendar[Calendar.MONTH] || nowCalendar[Calendar.MONTH] == birthCalendar[Calendar.MONTH] && nowCalendar[Calendar.DAY_OF_MONTH] < birthCalendar[Calendar.DAY_OF_MONTH]) {
                age--
            }

            // 계산된 나이를 SharedPreferences에 저장
            userDetailsEditor?.putString("age", age.toString())
            userDetailsEditor?.apply()
            return age.toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun KeyboardUp(size: Int) {
        sv?.postDelayed(Runnable { sv?.smoothScrollTo(0, size) }, 200)
    }

    fun saveProfileData() {
        val name: String = profile1_name?.getText().toString()
        val number: String = profile1_number?.getText().toString()
        val birthday: String = profile1_birth?.getText().toString()
        val gender: String = profile1_gender?.getText().toString()
        val height: String = profile1_height?.getText().toString()
        val weight: String = profile1_weight?.getText().toString()
        val age = calculateAge(birthday)
        val sleep1: String = profile1_sleep1?.getText().toString()
        val sleep2: String = profile1_sleep2?.getText().toString()
        val bpm: String = userDetailsSharedPref?.getString("o_bpm", "90")!!
        val step: String = userDetailsSharedPref?.getString("o_step", "3000")!!
        val distance: String = userDetailsSharedPref?.getString("o_distance", "5")!!
        val eCal: String = userDetailsSharedPref?.getString("o_ecal", "500")!!
        val cal: String = userDetailsSharedPref?.getString("o_cal", "2000")!!
        try {
            retrofitServerManager?.setProfile(email!!,
                name,
                number,
                gender,
                height,
                weight,
                age,
                birthday,
                sleep1,
                sleep2,
                bpm,
                step,
                distance,
                cal,
                eCal,
                object : RetrofitServerManager.ServerTaskCallback {
                    override fun onSuccess(result: String?) {
                        Log.i("save", result!!)
                        activity!!.runOnUiThread {
                            Toast.makeText(
                                activity,
                                resources.getString(R.string.saveData),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(e: Exception?) {
                        Log.e("err", "err")
                        activity!!.runOnUiThread {
                            Toast.makeText(
                                activity,
                                resources.getString(R.string.failSaveData),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        e!!.printStackTrace()
                    }
                })
        } catch (e: Exception) {
        }
    }
}