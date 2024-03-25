package com.example.chatapplication.api

import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore
import com.example.chatapplication.cache.UserCache
import com.hjq.http.annotation.HttpRename


class DetailGroupApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_DETAIL_GROUP()
    }

    @HttpRename("conversation_id")
    var conversationId = ""

    companion object {
        fun params(idGroup: String): BaseApi {
            val data = DetailGroupApi()
            data.conversationId = idGroup
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}