package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpRename


class AddMemberApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_ADD_MEMBER(id)
    }

    @HttpRename("id")
    var id: String = ""

    @HttpRename("members")
    var members: ArrayList<String> = ArrayList()

    companion object {
        fun params(id: String, members: ArrayList<String>): BaseApi {
            val data = AddMemberApi()
            data.authorization = UserCache.getAccessToken()
            data.members = members
            data.id = id
            return data
        }
    }
}
