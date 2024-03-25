package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename


class ChangeNameGroupApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterConversation.API_CHANGE_NAME_GROUP(id)
    }

    @HttpIgnore
    var id: String = ""

    @HttpRename("name")
    var name: String = ""

    companion object {
        fun params(id: String, name: String): BaseApi {
            val data = ChangeNameGroupApi()
            data.authorization = UserCache.getAccessToken()
            data.name = name
            data.id = id
            return data
        }
    }
}
