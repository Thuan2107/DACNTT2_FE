package com.example.chatapplication.api

import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.router.ApiRouterOauth
import com.hjq.http.annotation.HttpRename

class LoginApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterOauth.API_LOGIN()
    }

    @HttpRename("password")
    var password: String? = ""

    @HttpRename("phone")
    var phone: String? = ""
    companion object {
        fun params(phone: String, password: String): BaseApi {
            val data = LoginApi()
            data.phone = phone
            data.password = password
            return data
        }
    }
}