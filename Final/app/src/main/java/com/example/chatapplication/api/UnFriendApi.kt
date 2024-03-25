package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterFriend
import com.google.gson.annotations.SerializedName
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class UnFriendApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterFriend.API_UN_FRIEND(userId)
    }
    @HttpRename("_id")
    var userId = ""
    companion object {
        fun params(userId: String): BaseApi {
            val data = UnFriendApi()
            data.authorization = UserCache.getAccessToken()
            data.userId = userId
            return data
        }
    }
}