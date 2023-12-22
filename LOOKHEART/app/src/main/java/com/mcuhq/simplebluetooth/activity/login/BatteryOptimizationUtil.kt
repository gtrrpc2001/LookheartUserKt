package com.mcuhq.simplebluetooth.activity.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.mcuhq.simplebluetooth.R

object BatteryOptimizationUtil {
    @JvmStatic
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            ?: return false // PowerManager를 가져오지 못함
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun checkBatteryOptimizations(context: Context) {
        val pm = context
            .applicationContext
            .getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        }
    }

    @JvmStatic
    fun showBatteryOptimizationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.setBatTitle))
        builder.setMessage(context.resources.getString(R.string.setBatMessage))
        builder.setPositiveButton(context.resources.getString(R.string.ok)) { dialog, which ->
            checkBatteryOptimizations(
                context
            )
        }
        builder.setNegativeButton(context.resources.getString(R.string.rejectLogout)) { dialog, which -> // 사용자가 취소를 선택한 경우의 처리
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}