package com.mcuhq.simplebluetooth.language

import android.content.Context
import java.util.Locale

object LanguageCheck {
    fun checklanguage(context: Context): String {
        val configuration = context.resources.configuration
        val localeList = configuration.locales
        val locale: Locale
        locale = if (!localeList.isEmpty) {
            localeList[0] // 리스트의 첫 번째 로케일이 사용자의 현재 로케일입니다.
        } else {
            Locale.getDefault() // 시스템의 현재 로케일을 가져옵니다.
        }
        return locale.language
    }
}