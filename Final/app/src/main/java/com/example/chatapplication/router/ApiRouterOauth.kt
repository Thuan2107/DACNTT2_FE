package com.example.chatapplication.router


@Suppress("FunctionName")
object ApiRouterOauth {

    fun API_LOGIN(): String {
        return "auth/sign-in"
    }
    fun API_REGISTER(): String {
        return "auth/sign-up"
    }

    fun API_UPDATE_PROFILE(): String {
        return "user/update"
    }

}