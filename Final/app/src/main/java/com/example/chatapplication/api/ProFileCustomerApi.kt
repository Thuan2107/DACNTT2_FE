package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.APIRouter
import com.hjq.http.annotation.HttpIgnore
import com.hjq.http.annotation.HttpRename

class ProFileCustomerApi : BaseApi() {
    override fun getApi(): String {
        return APIRouter.API_PROFILE_CUSTOMER()
    }

     @HttpRename("_id")
     var userId = ""

    companion object {
        fun params(
                id: String
        ): BaseApi {
            val data = ProFileCustomerApi()
            data.userId = id
            data.authorization = UserCache.getAccessToken()
            return data
        }
    }
}
