package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpRename



class GetGroupInfoFromLinkGroupApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterConversation.API_GET_GROUP_INFO(linkJoin)
    }

    @HttpRename("link_join")
    var linkJoin: String = ""

    companion object {
        fun params(linkJoin: String): BaseApi {
            val data = GetGroupInfoFromLinkGroupApi()
            data.linkJoin = linkJoin
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}
