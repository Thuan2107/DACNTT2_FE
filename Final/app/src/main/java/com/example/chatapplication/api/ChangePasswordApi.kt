package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.APIRouter
import com.hjq.http.annotation.HttpRename

/**
 * @Author: HỒ QUANG TÙNG
 * @Date: 26/02/2023
 */
class ChangePasswordApi : BaseApi() {

    override fun getApi(): String {
        return APIRouter.API_CHANGE_PASSWORD()
    }

    @HttpRename("old_password")
    var oldPassword: String? = ""

    @HttpRename("new_password")
    var newPassword: String? = ""

    companion object {
        fun params(newPassword: String): BaseApi {
            val data = ChangePasswordApi()
            data.authorization = UserCache.getAccessToken()
            data.newPassword = newPassword
            data.oldPassword = UserCache.getUser().password
            return data
        }
    }
}