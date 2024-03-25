package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore

class JoinLinkGroupApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterConversation.API_JOIN_LINK_GROUP(linkJoin)
    }

    @HttpIgnore
    var linkJoin: String = ""

    companion object {
        fun params(linkJoin: String): BaseApi {
            val data = JoinLinkGroupApi()
            data.linkJoin = linkJoin
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}