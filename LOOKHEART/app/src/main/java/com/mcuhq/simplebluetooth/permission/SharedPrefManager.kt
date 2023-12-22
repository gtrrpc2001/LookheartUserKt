package com.mcuhq.simplebluetooth.permission

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager private constructor(context: Context, sharedPrefName: String) {
    private val sharedPreferences: SharedPreferences

    // Context를 전달받아 SharedPreferences 인스턴스를 얻는 private 생성자
    init {
        sharedPreferences =
            context.applicationContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
    }

    fun setPermissionCnt(count: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(IS_FIRST_TIME, count)
        editor.apply()
    }

    fun permissionCheck(): Int {
        return sharedPreferences.getInt(IS_FIRST_TIME, 0)
    }

    companion object {
        private const val IS_FIRST_TIME = "permission_check"

        // 싱글턴 인스턴스
        private var instance: SharedPrefManager? = null

        // 클래스의 싱글턴 인스턴스를 반환하는 public 메서드
        @Synchronized
        fun getInstance(context: Context, sharedPrefName: String): SharedPrefManager? {
            if (instance == null) {
                instance = SharedPrefManager(context, sharedPrefName)
            }
            return instance
        }
    }
}