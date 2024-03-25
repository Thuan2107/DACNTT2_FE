package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterFriend
import com.hjq.http.annotation.HttpRename

class GetListFriend : BaseApi() {

    override fun getApi(): String {
        return ApiRouterFriend.API_GET_LIST_FRIEND()
    }

    @HttpRename("type")
    var type: Int = 0

    companion object {
        fun params(type: Int): BaseApi {
            val data = GetListFriend()
            data.authorization = UserCache.getAccessToken()
            data.type = type
            return data
        }
    }
}