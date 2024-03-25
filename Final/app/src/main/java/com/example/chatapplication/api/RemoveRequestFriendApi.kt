package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterFriend
import com.hjq.http.annotation.HttpRename

class RemoveRequestFriendApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterFriend.API_REMOVE_REQUEST_FRIEND(userId)
    }

    @HttpRename("_id")
    var userId = ""

    companion object {
        fun params(userId: String): BaseApi {
            val data = RemoveRequestFriendApi()
            data.userId = userId
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}