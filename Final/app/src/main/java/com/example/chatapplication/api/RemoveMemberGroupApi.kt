package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpRename


class RemoveMemberGroupApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_REMOVE_MEMBER_GROUP(id)
    }

    @HttpRename("user_id")
    var memberId: String = ""

    @HttpRename("id")
    var id: String = ""

    companion object {
        fun params(id: String, memberId: String): BaseApi {
            val data = RemoveMemberGroupApi()
            data.id = id
            data.memberId = memberId
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}
