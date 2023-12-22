package com.mcuhq.simplebluetooth.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.auth.Find_Pw
import com.mcuhq.simplebluetooth.profile.Profile_1
import com.mcuhq.simplebluetooth.profile.Profile_2
import com.mcuhq.simplebluetooth.profile.Profile_3

class ProfileFragment : Fragment() {
    private var profile_name: TextView? = null
    private var profile_email: TextView? = null
    private var profile_day: TextView? = null
    private var sv: ScrollView? = null
    private var profile_logout: Button? = null
    private var profile_changepw: Button? = null
    private var email: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val emailSharedPreferences = activity?.getSharedPreferences("User", Context.MODE_PRIVATE)
        email = emailSharedPreferences?.getString("email", "null")
        profile_name = view.findViewById(R.id.profile_name)
        profile_email = view.findViewById(R.id.profile_email)
        profile_day = view.findViewById(R.id.profile_day)
        sv = view.findViewById(R.id.profile_ScrollView)
        profile_logout = view.findViewById(R.id.profile_logout_btn)
        profile_changepw = view.findViewById(R.id.profile_changepw)

        //sharedpreferneces에서 불러오기 (이름,이메일)
        val sharedPreferences = activity?.getSharedPreferences(email, Context.MODE_PRIVATE)
        val savedText1 = sharedPreferences?.getString("name", "")
        val savedText2 = sharedPreferences?.getString("email", "")
        val savedText3 = sharedPreferences?.getString("current_date", "")
        profile_name?.setText(savedText1)
        profile_email?.setText(savedText2)
        profile_day?.setText(savedText3)


        // 프래그먼트 전환 버튼
        val btn1 = view.findViewById<Button>(R.id.profile_information)
        val btn2 = view.findViewById<Button>(R.id.profile_objective)
        val btn3 = view.findViewById<Button>(R.id.profile_setting)
        //        Button btn4 = view.findViewById(R.id.profile_ecg);


        //기본정보 프래그먼트(profile_1) 기본으로
        val childFragment = Profile_1()
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.inner_fragment_container, childFragment)
        fragmentTransaction.commit()
        btn1.setTextColor(Color.BLACK)
        btn2.setTextColor(Color.LTGRAY)
        btn3.setTextColor(Color.LTGRAY)
        //        btn4.setTextColor(Color.LTGRAY);
//        btn1.setTextSize(16);
//        btn2.setTextSize(16);
//        btn3.setTextSize(16);
//        btn4.setTextSize(20);
        btn1.setOnClickListener {
            val childFragment = Profile_1()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.inner_fragment_container, childFragment)
            fragmentTransaction.commit()
            btn1.setTextColor(Color.BLACK)
            btn2.setTextColor(Color.LTGRAY)
            btn3.setTextColor(Color.LTGRAY)
            //                btn4.setTextColor(Color.LTGRAY);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(20);
        }
        btn2.setOnClickListener {
            val childFragment = Profile_2()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.inner_fragment_container, childFragment)
            fragmentTransaction.commit()
            btn1.setTextColor(Color.LTGRAY)
            btn2.setTextColor(Color.BLACK)
            btn3.setTextColor(Color.LTGRAY)
            //                btn4.setTextColor(Color.LTGRAY);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(20);
        }
        btn3.setOnClickListener {
            val childFragment = Profile_3()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.inner_fragment_container, childFragment)
            fragmentTransaction.commit()
            btn1.setTextColor(Color.LTGRAY)
            btn2.setTextColor(Color.LTGRAY)
            btn3.setTextColor(Color.BLACK)
            //                btn4.setTextColor(Color.LTGRAY);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(20);
        }

//        btn4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                profile_4 childFragment = new profile_4();
//                FragmentManager fragmentManager = getParentFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.inner_fragment_container, childFragment);
//                fragmentTransaction.commit();
//
//                btn1.setTextColor(Color.LTGRAY);
//                btn2.setTextColor(Color.LTGRAY);
//                btn3.setTextColor(Color.LTGRAY);
//                btn4.setTextColor(Color.BLACK);
//                btn1.setTextSize(20);
//                btn2.setTextSize(20);
//                btn3.setTextSize(20);
//                btn4.setTextSize(25);
//
//            }
//        });
        profile_changepw?.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, Find_Pw::class.java)

            // 다른 데이터를 넘겨야 할 경우에는 Intent에 데이터를 추가할 수 있습니다.
            // intent.putExtra("key", "value");

            // 액티비티로 이동
            startActivity(intent)
        })
        profile_logout?.setOnClickListener(View.OnClickListener { showConfirmationDialog() })
        return view
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(resources.getString(R.string.logout))
            .setMessage(resources.getString(R.string.logoutHelp))
            .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                val autoLoginSP = activity?.getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
                var editor = autoLoginSP?.edit()
                val autoLoginCheck = autoLoginSP?.getBoolean("autologin", false)!!
                if (autoLoginCheck) {
                    editor = autoLoginSP?.edit()
                    editor?.putBoolean("autologin", false)
                    editor?.apply()
                }
                activity?.finish()
            }
            .setNegativeButton(resources.getString(R.string.rejectLogout)) { dialog, which -> // 취소 버튼을 눌렀을 때 아무 작업 없이 다이얼로그 창을 닫습니다.
                dialog.dismiss()
            }
        val dialog: Dialog = builder.create()
        dialog.show()
    }

    fun controlScroll(size: Int) {
        sv!!.postDelayed({ sv!!.smoothScrollTo(0, size) }, 200)
    }
}