package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterFriend
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class AcceptAddFriendApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterFriend.API_ADD_FRIEND(userId)
    }

    @HttpRename("_id")
    var userId = ""

    companion object {
        fun params(contactFromUserId: String): BaseApi {
            val data = AcceptAddFriendApi()
            data.userId = contactFromUserId
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}