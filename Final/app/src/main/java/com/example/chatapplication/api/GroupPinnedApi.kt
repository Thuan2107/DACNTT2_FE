package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpRename

class GroupPinnedApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterConversation.API_GET_CONVERSATION_PINNED()
    }

    @HttpRename("position")
    var position: String = ""

    @HttpRename("limit")
    var limit: Int = 0

    @HttpRename("key_search")
    var keySearch: String = ""

    companion object {
        fun params(position: String, keySearch: String, limit: Int): BaseApi {
            val data = GroupPinnedApi()
            data.authorization = UserCache.getAccessToken()
            data.position = position
            data.limit = limit
            data.keySearch = keySearch
            return data
        }
    }
}