package com.example.chatapplication.cache

import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.helper.PrefUtils.Companion.getString
import com.example.chatapplication.helper.PrefUtils.Companion.putString
import com.example.chatapplication.model.entity.User
import com.google.gson.Gson

class CurrentUser {
    private var mUserInfo: User = User()

    fun getUserInfo(): User {
        try {
            mUserInfo = Gson().fromJson(
                getString(AppConstants.KEY_CACHE_USER_INFO),
                User::class.java
            )
        } catch (e: Exception) {
            e.stackTrace
        }
        return mUserInfo
    }

    fun checkLogin(): Boolean {
        return getUserInfo().id.toLong() != 0L
    }

    fun saveUserInfo(user: User?) {
        var userInfo = user
        try {
            if (userInfo == null) {
                userInfo = User()
            }
            putString(AppConstants.KEY_CACHE_USER_INFO, Gson().toJson(userInfo))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getToken(): String {
        var token = ""
        if (checkLogin()) {
            token = "Bearer ${getUserInfo().accessToken}"
        }
        return token
    }
}