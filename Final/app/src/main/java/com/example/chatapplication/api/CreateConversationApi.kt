package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpRename


class CreateConversationApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterConversation.API_CREATE_CONVERSATION()
    }

    @HttpRename("member_id")
    var memberId: String = ""

    companion object {
        fun params(memberId: String): BaseApi {
            val data = CreateConversationApi()
            data.authorization = UserCache.getAccessToken()
            data.memberId = memberId
            return data
        }
    }
}