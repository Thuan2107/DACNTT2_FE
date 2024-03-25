package com.example.chatapplication.cache

import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.model.entity.User
import com.google.gson.Gson
import com.tencent.mmkv.MMKV


object UserCache {
    private var mUserInfo = User()
    private val mmkv: MMKV = MMKV.mmkvWithID("cache_user")

    fun getUser(): User {
        try {
            mUserInfo = Gson().fromJson(mmkv.getString(AppConstants.CACHE_USER, ""), User::class.java)
        } catch (e: Exception) {
            e.stackTrace
        }
        return mUserInfo
    }

    fun saveUser(userInfo: User) {
        try {
            mmkv.remove(AppConstants.CACHE_USER)
            mmkv.putString(AppConstants.CACHE_USER, Gson().toJson(userInfo)).commit()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    fun isLogin(): Boolean {
        val mUserInfo = getUser()
        return mUserInfo.id.length > 0
    }

    fun getAccessToken(): String {
        val user = getUser()
        return String.format("%s %s", "Bearer", user.accessToken)
    }

    fun accessToken(): String {
        val user = getUser()
        return user.accessToken
    }

    fun nodeToken(): String {
        val user = getUser()
        return user.jwtToken
    }

    fun getNodeToken(): String {
        val user = getUser()
        return String.format("%s %s", user.tokenType, user.jwtToken)
    }

//    fun getAuthToken(): String {
//        return String.format(
//            "%s %s:%s",
//            ConfigCache.getConfig().type,
//            ConfigCache.getConfig().session,
//            ConfigCache.getConfig().apiKey
//        )
//    }
}
