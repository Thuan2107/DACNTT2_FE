package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.APIRouter
import com.hjq.http.annotation.HttpRename
class SearchUserApi : BaseApi() {
    override fun getApi(): String {
        return APIRouter.API_SEARCH_BY_PHONE()
    }

    @HttpRename("phone")
    var phone: String = ""

    companion object {
        fun params(
            phone: String,
        ): BaseApi {
            val data = SearchUserApi()
            data.phone = phone
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}