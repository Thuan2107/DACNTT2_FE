package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class DeleteGroupApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_DELETE_GROUP(conversationId)
    }

    @HttpIgnore
    var conversationId = ""

    companion object {
        fun params(conversationId: String): BaseApi {
            val data = DeleteGroupApi()
            data.authorization = UserCache.getAccessToken()
            data.conversationId = conversationId
            return data
        }
    }
}