package com.mcuhq.simplebluetooth.server

import com.google.gson.annotations.SerializedName

class UserProfile {
    // getters
    // Gson 응답 파싱
    @SerializedName("eq")
    @get:JvmName("Id")
    val id: String? = null

    @JvmField
    @SerializedName("eqname")
    @get:JvmName("Name")
    val name: String? = null

    @SerializedName("email")
    @get:JvmName("Email")
    val email: String? = null

    @JvmField
    @SerializedName("userphone")
    @get:JvmName("Phone")
    val phone: String? = null

    @JvmField
    @SerializedName("sex")
    @get:JvmName("Gender")
    val gender: String? = null

    @JvmField
    @SerializedName("height")
    @get:JvmName("Height")
    val height: String? = null

    @JvmField
    @SerializedName("weight")
    @get:JvmName("Weight")
    val weight: String? = null

    @JvmField
    @SerializedName("age")
    @get:JvmName("Age")
    val age: String? = null

    @JvmField
    @SerializedName("birth")
    @get:JvmName("Birthday")
    val birthday: String? = null

    @JvmField
    @SerializedName("signupdate")
    @get:JvmName("JoinDate")
    val joinDate: String? = null

    @JvmField
    @SerializedName("sleeptime")
    @get:JvmName("SleepStart")
    val sleepStart: String? = null

    @JvmField
    @SerializedName("uptime")
    @get:JvmName("SleepEnd")
    val sleepEnd: String? = null

    @JvmField
    @SerializedName("bpm")
    @get:JvmName("ActivityBPM")
    val activityBPM: String? = null

    @JvmField
    @SerializedName("step")
    @get:JvmName("DailyStep")
    val dailyStep: String? = null

    @JvmField
    @SerializedName("distanceKM")
    @get:JvmName("DailyDistance")
    val dailyDistance: String? = null

    @SerializedName("calexe")
    @get:JvmName("DailyActivityCalorie")
    private val dailyActivityCalorie: String? = null

    @SerializedName("cal")
    @get:JvmName("DailyCalorie")
    private val dailyCalorie: String? = null

    @SerializedName("alarm_sms")
    private val smsNotification: String? = null

    @SerializedName("differtime")
    private val timeDifference: String? = null

    @SerializedName("phone")
    @get:JvmName("Guardian")
    val guardian: String? = null

    fun getDailyCalorie(): String? {
        return dailyActivityCalorie
    }

    fun getDailyActivityCalorie(): String? {
        return dailyCalorie
    }

    fun getId(): String? {
        return id
    }

    fun getName(): String? {
        return name
    }

    fun getEmail(): String? {
        return email
    }

    fun getPhone(): String? {
        return phone
    }

    fun getGender(): String? {
        return gender
    }

    fun getHeight(): String? {
        return height
    }

    fun getWeight(): String? {
        return weight
    }

    fun getAge(): String? {
        return age
    }

    fun getBirthday(): String? {
        return birthday
    }

    fun getJoinDate(): String? {
        return joinDate
    }

    fun getSleepStart(): String? {
        return sleepStart
    }

    fun getSleepEnd(): String? {
        return sleepEnd
    }

    fun getActivityBPM(): String? {
        return activityBPM
    }

    fun getDailyStep(): String? {
        return dailyStep
    }

    fun getDailyDistance(): String? {
        return dailyDistance
    }

    fun getGuardian(): String? {
        return guardian
    }
}