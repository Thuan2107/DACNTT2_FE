package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class ChangeAvatarGroupApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterConversation.API_CHANGE_AVATAR_GROUP(id)
    }

    @HttpIgnore
    var id = ""

    @HttpRename("avatar")
    var avatar: String = ""

    companion object {
        fun params(id: String, avatar: String): BaseApi {
            val data = ChangeAvatarGroupApi()
            data.authorization = UserCache.getAccessToken()
            data.avatar = avatar
            data.id = id
            return data
        }
    }
}
