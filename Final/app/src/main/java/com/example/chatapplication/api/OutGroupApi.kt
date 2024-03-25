package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore



class OutGroupApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_OUT_GROUP(id)
    }

    @HttpIgnore
    var id = ""

    companion object {
        fun params(id: String): BaseApi {
            val data = OutGroupApi()
            data.authorization = UserCache.getAccessToken()
            data.id = id
            return data
        }
    }
}