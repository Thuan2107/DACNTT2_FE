package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterFriend
import com.hjq.http.annotation.HttpIgnore


class SendRequestFriendApi : BaseApi(){
    override fun getApi(): String {
        return ApiRouterFriend.API_SEND_REQUEST_FRIEND(id)
    }

    @HttpIgnore
    var id  = ""

    companion object {
        fun params(id : String): BaseApi {
            val data = SendRequestFriendApi()
            data.id = id
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}