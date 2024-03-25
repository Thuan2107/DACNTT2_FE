package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpRename

class CreateGroupApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_CREATE_GROUP()
    }

    @HttpRename("name")
    var name: String = ""

    @HttpRename("avatar")
    var avatar: String = ""

    @HttpRename("member_ids")
    var members: ArrayList<String> = ArrayList()

    companion object {
        fun params(name: String, avt: String, member: ArrayList<String>): BaseApi {
            val data = CreateGroupApi()
            data.authorization = UserCache.getAccessToken()
            data.name = name
            data.avatar = avt
            data.members = member
            return data
        }
    }
}