package com.mcuhq.simplebluetooth.auth.sendEmail

import android.content.Context
import android.util.Log

class SendMail(var getEmail: String) {
    var gMailSender = GMailSender()
    var code = ""

    fun sendSecurityCode(context: Context?, tile: String?, content: String): Boolean {
        var result = false
        try {
            this.code = gMailSender.emailCode
            gMailSender.sendMail(
                tile, """
     $content
     인증번호 : ${this.code}
     """.trimIndent(), getEmail
            )
            result = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("sendSecurityCode", result.toString())
        return result
    }
}