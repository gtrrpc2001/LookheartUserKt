package com.mcuhq.simplebluetooth.fragment.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mcuhq.simplebluetooth.R
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ForegroundService : Service() {
    private val executor: Executor = Executors.newSingleThreadExecutor() // 백그라운드에서 코드를 실행할 Executor

    @Volatile
    private var isRunning = true // 서비스가 실행 중인지 확인하는 플래그
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startBackgroundTask()
        initializeNotification() // 포그라운드 생성
        return START_NOT_STICKY
    }

    private fun startBackgroundTask() {
        executor.execute {
            while (isRunning) {
                try {
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                    // Thread interrupted
                }
            }
        }
    }

    fun initializeNotification() {
        val builder = NotificationCompat.Builder(this, FOREGROUND_SERVICE)
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
        val style = NotificationCompat.BigTextStyle()
        style.bigText(resources.getString(R.string.serviceRunning))
        style.setBigContentTitle(null)
        style.setSummaryText("LOOKHEART")
        builder.setContentText(null)
        builder.setContentTitle(null)
        builder.setOngoing(true)
        builder.setStyle(style)
        builder.setWhen(0)
        builder.setShowWhen(false)
        val notificationIntent = Intent(this, HomeFragment::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                FOREGROUND_SERVICE,
                FOREGROUND_NAME,
                NotificationManager.IMPORTANCE_NONE
            )
        )
        val notification = builder.build()
        startForeground(2017, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false // 백그라운드 작업 중지
        stopSelf()
    }

    companion object {
        private const val FOREGROUND_SERVICE = "LOOKHEART_FOREGROUND"
        private const val FOREGROUND_NAME = "LOOKHEART_USER"
    }
}