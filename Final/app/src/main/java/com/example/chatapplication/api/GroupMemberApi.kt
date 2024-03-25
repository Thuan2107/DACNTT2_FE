package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class GroupMemberApi : BaseApi() {


    override fun getApi(): String {
        return ApiRouterConversation.API_GROUP_MEMBER(conversation_id)
    }

    @HttpIgnore
    var conversation_id = ""

    companion object {
        fun params(id: String): BaseApi {
            val data = GroupMemberApi()
            data.authorization = UserCache.getAccessToken()
            data.conversation_id = id
            return data
        }
    }
}