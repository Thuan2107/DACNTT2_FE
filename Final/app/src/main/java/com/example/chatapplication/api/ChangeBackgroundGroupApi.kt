package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class ChangeBackgroundGroupApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterConversation.API_CHANGE_BACKGROUND_GROUP(id)
    }

    @HttpIgnore
    var id = ""

    @HttpRename("back_ground")
    var avatar: String = ""

    companion object {
        fun params(id: String, avatar: String): BaseApi {
            val data = ChangeBackgroundGroupApi()
            data.authorization = UserCache.getAccessToken()
            data.avatar = avatar
            data.id = id
            return data
        }
    }
}
