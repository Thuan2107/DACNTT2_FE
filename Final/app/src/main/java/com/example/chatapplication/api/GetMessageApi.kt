package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.router.ApiRouterMessage
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename


class GetMessageApi : BaseApi() {
    override fun getApi(): String {
        return ApiRouterMessage.API_GET_MESSAGE(id)
    }

    @HttpRename("arrow")
    var arrow: Int? = ChatConstants.SCROLL_TO_TOP

    @HttpRename("limit")
    var limit: Int? = 0

    @HttpIgnore
    var id: String = ""

    @HttpRename("position")
    var position: String = ""

    companion object {
        fun params(id: String, position: String, limit: Int, arrow: Int): BaseApi {
            val data = GetMessageApi()
            data.id = id
            data.position = position
            data.limit = limit
            data.arrow = arrow
            data.method = AppConstants.HTTP_METHOD_GET
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}