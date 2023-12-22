package com.mcuhq.simplebluetooth.noti

import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentActivity
import com.mcuhq.simplebluetooth.R

class NotificationManager private constructor(private val fragmentActivity: FragmentActivity) {
    var basicSound = R.raw.basicsound
    var arrSound = R.raw.arrsound
    private var notificationManager: android.app.NotificationManager? = null
    var notiPermissionCheck = false

    init {
        notiPermissionCheck = true
        initBasicNotificationChannel()
        initArrNotificationChannel()
    }

    private fun createNotificationChannel(channelId: String, soundUri: Uri) {
        // notification manager 생성
        if (notificationManager == null) {
            notificationManager =
                fragmentActivity.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        }
        val notificationChannel = NotificationChannel(
            channelId,
            CHANNEL_NAME,
            android.app.NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Notification"
        notificationChannel.setSound(
            soundUri,
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        )
        notificationManager!!.createNotificationChannel(notificationChannel)
    }

    private fun initBasicNotificationChannel() {
        createNotificationChannel(
            PRIMARY_CHANNEL_ID,
            Uri.parse("android.resource://" + fragmentActivity.packageName + "/" + basicSound)
        )
    }

    private fun initArrNotificationChannel() {
        createNotificationChannel(
            PRIMARY_ARR_CHANNEL_ID,
            Uri.parse("android.resource://" + fragmentActivity.packageName + "/" + arrSound)
        )
    }

    fun sendNotification(noti: String, notificationId: Int, getCurrentTime: String) {
        val notifyBuilder = getNotificationBuilder(noti, getCurrentTime)
        notificationManager!!.notify(notificationId, notifyBuilder.build())
    }

    fun sendArrNotification(noti: String, notificationId: Int, getCurrentTime: String) {
        val notifyBuilder = getArrNotificationBuilder(noti, getCurrentTime)
        notificationManager!!.notify(notificationId, notifyBuilder.build())
    }

    private fun getNotificationBuilder(
        noti: String,
        currentTime: String
    ): NotificationCompat.Builder {
        val notifyBuilder = NotificationCompat.Builder(fragmentActivity, PRIMARY_CHANNEL_ID)
        when (noti) {
            MYO -> notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.notiTypeMyo))
            NON_CONTACT -> notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.notiTypeNonContact))
            else -> return notifyBuilder
        }
        notifyBuilder.setContentText(fragmentActivity.resources.getString(R.string.notiTime) + currentTime)
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        return notifyBuilder
    }

    private fun getArrNotificationBuilder(
        noti: String,
        currentTime: String
    ): NotificationCompat.Builder {
        val notifyBuilder = NotificationCompat.Builder(fragmentActivity, PRIMARY_ARR_CHANNEL_ID)
        when (noti) {
            ARR -> notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.notiTypeArr))
            BRADYCARDIA -> notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.notiTypeSlowArr))
            TACHYCARDIA -> notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.notiTypeFastArr))
            ATRIAL_FIBRILLATION -> notifyBuilder.setContentTitle(
                fragmentActivity.resources.getString(
                    R.string.notiTypeHeavyArr
                )
            )

            ARR_50 -> {
                notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.arrCnt50))
                notifyBuilder.setContentText(fragmentActivity.resources.getString(R.string.arrCnt50Text))
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                return notifyBuilder
            }

            ARR_100 -> {
                notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.arrCnt100))
                notifyBuilder.setContentText(fragmentActivity.resources.getString(R.string.arrCnt100Text))
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                return notifyBuilder
            }

            ARR_200 -> {
                notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.arrCnt200))
                notifyBuilder.setContentText(fragmentActivity.resources.getString(R.string.arrCnt200Text))
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                return notifyBuilder
            }

            ARR_300 -> {
                notifyBuilder.setContentTitle(fragmentActivity.resources.getString(R.string.arrCnt300))
                notifyBuilder.setContentText(fragmentActivity.resources.getString(R.string.arrCnt300Text))
                notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                return notifyBuilder
            }

            else -> return notifyBuilder
        }
        notifyBuilder.setContentText(fragmentActivity.resources.getString(R.string.notiTime) + currentTime)
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        return notifyBuilder
    }

    companion object {
        private const val PRIMARY_CHANNEL_ID = "basicNotification"
        private const val CHANNEL_NAME = "LOOK_HEART_CHANNEL"
        private const val PRIMARY_ARR_CHANNEL_ID = "arrNotification"
        private const val ARR = "arr"
        private const val BRADYCARDIA = "bradycardia"
        private const val TACHYCARDIA = "tachycardia"
        private const val ATRIAL_FIBRILLATION = "atrialFibrillation"
        private const val MYO = "myo"
        private const val NON_CONTACT = "nonContact"
        private const val ARR_50 = "50"
        private const val ARR_100 = "100"
        private const val ARR_200 = "200"
        private const val ARR_300 = "300"
        private var instance: NotificationManager? = null
        @JvmStatic
        @Synchronized
        fun getInstance(getFragmentActivity: FragmentActivity): NotificationManager? {
            if (instance == null) {
                instance = NotificationManager(getFragmentActivity)
            }
            return instance
        }
    }
}