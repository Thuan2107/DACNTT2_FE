package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterOauth
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class UpdateProfileApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterOauth.API_UPDATE_PROFILE()
    }

    @HttpRename("full_name")
    var fullName: String? = ""

    @HttpRename("avatar")
    var avatar: String = ""

    @HttpRename("email")
    var email: String? = ""

    @HttpRename("birthday")
    var birthday: String? = ""

    @HttpRename("gender")
    var gender: Int? = 0

    @HttpRename("address")
    var address: String? = ""

    @HttpRename("nick_name")
    var nickName: String? = ""

    companion object {
        fun params(
            fullName: String,
            avatar: String,
            email: String,
            birthday: String,
            gender: Int,
            address: String,
            nickname: String,
            isDisplayNickName: Int
        ): BaseApi {
            val data = UpdateProfileApi()
            data.fullName = fullName
            data.avatar = avatar
            data.email = email
            data.birthday = birthday
            data.gender = gender
            data.address = address
            data.nickName = nickname
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}