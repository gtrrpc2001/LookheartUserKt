package com.mcuhq.simplebluetooth.server

object UserProfileManager {

    private var instance: UserProfileManager? = null

    @set:JvmName("UserProfile")
    @get:JvmName("UserProfile")
    var userProfile: UserProfile? = null

    fun getInstance(): UserProfileManager? {
        if (instance == null) {
            instance = UserProfileManager
        }
        return instance
    }

    fun getUserProfile(): UserProfile? {
        return userProfile
    }

    fun setUserProfile(myUserProfile: UserProfile) {
        userProfile = myUserProfile
    }
}