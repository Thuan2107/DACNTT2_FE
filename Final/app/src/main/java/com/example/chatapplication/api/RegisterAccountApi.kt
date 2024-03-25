package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterOauth
import com.hjq.http.annotation.HttpRename



class RegisterAccountApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterOauth.API_REGISTER()
    }

    @HttpRename("phone")
    var phone: String? = ""

    @HttpRename("avatar")
    var avatar: String? = ""

    @HttpRename("full_name")
    var name: String? = ""

    @HttpRename("birthday")
    var birthday: String? = ""

    @HttpRename("gender")
    var gender: Int? = 0

    @HttpRename("password")
    var password: String? = ""

    companion object {
        fun params(
            phone: String,
            avatar: String,
            name: String,
            birthday: String,
            gender: Int,
            password: String,
        ): BaseApi {
            val data = RegisterAccountApi()
            data.phone = phone
            data.avatar = avatar
            data.name = name
            data.birthday = birthday
            data.gender = gender
            data.password = password
            return data
        }
    }
}