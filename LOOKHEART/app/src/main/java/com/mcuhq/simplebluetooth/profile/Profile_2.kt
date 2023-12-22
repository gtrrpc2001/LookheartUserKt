package com.mcuhq.simplebluetooth.profile

import android.content.Context
import android.content.SharedPreferences
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
import androidx.lifecycle.ViewModelProvider
import com.library.KTLibrary.viewmodel.SharedViewModel
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.server.RetrofitServerManager

class Profile_2 : Fragment() {
    var viewModel: SharedViewModel? = null
    var retrofitServerManager: RetrofitServerManager? = null
    private var userDetailsSharedPref: SharedPreferences? = null
    private var userDetailsEditor: SharedPreferences.Editor? = null
    private var profile2_bpm_m: Button? = null
    private var profile2_bpm_p: Button? = null
    private var profile2_step_m: Button? = null
    private var profile2_step_p: Button? = null
    private var profile2_distance_m: Button? = null
    private var profile2_distance_p: Button? = null
    private var profile2_ecal_m: Button? = null
    private var profile2_ecal_p: Button? = null
    private var profile2_cal_m: Button? = null
    private var profile2_cal_p: Button? = null
    private var profile2_save: Button? = null
    private var profile2_bpm: EditText? = null
    private var profile2_step: EditText? = null
    private var profile2_distance: EditText? = null
    private var profile2_ecal: EditText? = null
    private var profile2_cal: EditText? = null
    private val bpm = 90
    private val step = 2000
    private val distance = 5
    private val ecal = 500
    private val cal = 3000
    private var profile2check = false
    private var email: String? = null
    var sv: ScrollView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_profile2, container, false)
        sv = view.findViewById<ScrollView>(R.id.scrollView2)
        retrofitServerManager = RetrofitServerManager.getInstance()
        val emailSharedPreferences: SharedPreferences =
            activity?.getSharedPreferences("User", Context.MODE_PRIVATE)!!
        email = emailSharedPreferences.getString("email", "null")
        userDetailsSharedPref = activity?.getSharedPreferences(email, Context.MODE_PRIVATE)!!
        userDetailsEditor = userDetailsSharedPref?.edit()
        profile2_bpm_m = view.findViewById(R.id.profile2_bpm_m)
        profile2_bpm_p = view.findViewById(R.id.profile2_bpm_p)
        profile2_step_m = view.findViewById(R.id.profile2_step_m)
        profile2_step_p = view.findViewById(R.id.profile2_step_p)
        profile2_distance_m = view.findViewById(R.id.profile2_distance_m)
        profile2_distance_p = view.findViewById(R.id.profile2_distance_p)
        profile2_ecal_m = view.findViewById(R.id.profile2_ecal_m)
        profile2_ecal_p = view.findViewById(R.id.profile2_ecal_p)
        profile2_cal_m = view.findViewById(R.id.profile2_cal_m)
        profile2_cal_p = view.findViewById(R.id.profile2_cal_p)
        profile2_save = view.findViewById(R.id.profile2_save)
        profile2_bpm = view.findViewById(R.id.profile2_bpm)
        profile2_step = view.findViewById(R.id.profile2_step)
        profile2_distance = view.findViewById(R.id.profile2_distance)
        profile2_ecal = view.findViewById(R.id.profile2_ecal)
        profile2_cal = view.findViewById(R.id.profile2_cal)
        val args: Bundle? = arguments

        //SharedPreferences에서 개인정보 불러오기
        val savedText1: String = userDetailsSharedPref?.getString("o_bpm", bpm.toString())!!
        val savedText2: String = userDetailsSharedPref?.getString("o_step", step.toString())!!
        val savedText3: String = userDetailsSharedPref?.getString("o_distance", distance.toString())!!
        val savedText4: String = userDetailsSharedPref?.getString("o_ecal", ecal.toString())!!
        val savedText5: String = userDetailsSharedPref?.getString("o_cal", cal.toString())!!
        profile2check = userDetailsSharedPref?.getBoolean("profile2check", false)!!
        profile2_bpm?.setText(savedText1)
        profile2_step?.setText(savedText2)
        profile2_distance?.setText(savedText3)
        profile2_ecal?.setText(savedText4)
        profile2_cal?.setText(savedText5)


        // 값을 int로 변환합니다.
        val o_bpm = intArrayOf(savedText1.toInt())
        val o_step = intArrayOf(savedText2.toInt())
        val o_distance = intArrayOf(savedText3.toInt())
        val o_ecal = intArrayOf(savedText4.toInt())
        val o_cal = intArrayOf(savedText5.toInt())

        // TextView에 int 값으로 설정합니다.
        profile2_bpm?.setText(o_bpm[0].toString())
        profile2_step?.setText(o_step[0].toString())
        profile2_distance?.setText(o_distance[0].toString())
        profile2_ecal?.setText(o_ecal[0].toString())
        profile2_cal?.setText(o_cal[0].toString())
        profile2_bpm_p?.setOnClickListener(View.OnClickListener {
            o_bpm[0]++
            profile2_bpm?.setText(o_bpm[0].toString())
        })
        profile2_bpm_m?.setOnClickListener(View.OnClickListener {
            o_bpm[0]--
            profile2_bpm?.setText(o_bpm[0].toString())
        })
        profile2_step_p?.setOnClickListener(View.OnClickListener {
            o_step[0]++
            profile2_step?.setText(o_step[0].toString())
        })
        profile2_step_m?.setOnClickListener(View.OnClickListener {
            o_step[0]--
            profile2_step?.setText(o_step[0].toString())
        })
        profile2_distance_p?.setOnClickListener(View.OnClickListener {
            o_distance[0]++
            profile2_distance?.setText(o_distance[0].toString())
        })
        profile2_distance_m?.setOnClickListener(View.OnClickListener {
            o_distance[0]--
            profile2_distance?.setText(o_distance[0].toString())
        })
        profile2_ecal_p?.setOnClickListener(View.OnClickListener {
            o_ecal[0]++
            profile2_ecal?.setText(o_ecal[0].toString())
        })
        profile2_ecal_m?.setOnClickListener(View.OnClickListener {
            o_ecal[0]--
            profile2_ecal?.setText(o_ecal[0].toString())
        })
        profile2_cal_p?.setOnClickListener(View.OnClickListener {
            o_cal[0]++
            profile2_cal?.setText(o_cal[0].toString())
        })
        profile2_cal_m?.setOnClickListener(View.OnClickListener {
            o_cal[0]--
            profile2_cal?.setText(o_cal[0].toString())
        })
        profile2_save?.setOnClickListener(View.OnClickListener {
            val textToSave1: String = profile2_bpm?.getText().toString()
            val textToSave2: String = profile2_step?.getText().toString()
            val textToSave3: String = profile2_distance?.getText().toString()
            val textToSave4: String = profile2_ecal?.getText().toString()
            val textToSave5: String = profile2_cal?.getText().toString()
            userDetailsEditor?.putString("o_bpm", textToSave1)
            userDetailsEditor?.putString("o_step", textToSave2)
            userDetailsEditor?.putString("o_distance", textToSave3)
            userDetailsEditor?.putString("o_ecal", textToSave4)
            userDetailsEditor?.putString("o_cal", textToSave5)
            userDetailsEditor?.putBoolean("profile2check", true)
            userDetailsEditor?.apply()
            saveProfileData()
            viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
            viewModel?.setBpm(textToSave1)
            viewModel?.setTCalText(textToSave5)
            viewModel?.setECalText(textToSave4)
            viewModel?.setDistance(textToSave3)
            viewModel?.setStep(textToSave2)

            // 저장한 후, 필요한 작업 수행 (예: 토스트 메시지 표시 등)
//                Toast.makeText(getActivity(), getResources().getString(R.string.saveData), Toast.LENGTH_SHORT).show();
        })


        //입력할때 키보드에 대한 높이조절
        profile2_bpm?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(100)
            }
        })
        profile2_step?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(300)
            }
        })
        profile2_distance?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(300)
            }
        })
        profile2_ecal?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(500)
            }
        })
        profile2_cal?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View, hasFocus: Boolean) {
                KeyboardUp(500)
            }
        })
        return view
    }

    fun KeyboardUp(size: Int) {
        sv?.postDelayed(Runnable { sv?.smoothScrollTo(0, size) }, 200)
    }

    fun saveProfileData() {
        val name: String = userDetailsSharedPref?.getString("name", "null")!!
        val number: String = userDetailsSharedPref?.getString("number", "null")!!
        val birthday: String = userDetailsSharedPref?.getString("birthday", "null")!!
        val age: String = userDetailsSharedPref?.getString("age", "null")!!
        val gender: String = userDetailsSharedPref?.getString("gender", "null")!!
        val height: String = userDetailsSharedPref?.getString("height", "null")!!
        val weight: String = userDetailsSharedPref?.getString("weight", "null")!!
        val sleep1: String = userDetailsSharedPref?.getString("sleep1", "null")!!
        val sleep2: String = userDetailsSharedPref?.getString("sleep2", "null")!!
        val bpm: String = profile2_bpm?.getText().toString()
        val step: String = profile2_step?.getText().toString()
        val distance: String = profile2_distance?.getText().toString()
        val eCal: String = profile2_ecal?.getText().toString()
        val cal: String = profile2_cal?.getText().toString()
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
                        Log.e("setProfile", "setProfile")
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