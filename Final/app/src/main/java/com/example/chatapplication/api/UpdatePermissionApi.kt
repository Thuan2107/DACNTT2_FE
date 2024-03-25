package com.example.chatapplication.api

import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.router.ApiRouterConversation
import com.hjq.http.annotation.HttpRename



class UpdatePermissionApi : BaseApi() {

    override fun getApi(): String {
        return ApiRouterConversation.API_UPDATE_PERMISSION(idGroup)
    }

    @HttpRename("user_id")
    var memberId: String = ""

    @HttpRename("permission")
    var permission: Int = 0

    var idGroup = ""

    companion object {
        fun params(idGroup: String, memberId: String, permission: Int): BaseApi {
            val data = UpdatePermissionApi()
            data.authorization = UserCache.getAccessToken()
            data.memberId = memberId
            data.idGroup = idGroup
            data.permission = permission
            return data
        }
    }
}
