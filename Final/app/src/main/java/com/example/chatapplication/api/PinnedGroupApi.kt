package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore


class PinnedGroupApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_PINNED_GROUP(id)
    }

    @HttpIgnore
    var id = ""

    companion object {
        fun params(idGroup: String): BaseApi {
            val data = PinnedGroupApi()
            data.authorization = UserCache.getAccessToken()
            data.id = idGroup
            return data
        }
    }
}