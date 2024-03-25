package com.example.chatapplication.router

object APIRouter {
    fun API_SEARCH_BY_PHONE(): String{
        return "user/find-phone"
    }

    fun API_CHANGE_PASSWORD(): String {
        return "auth/change-password"
    }

    fun API_PROFILE_CUSTOMER(): String {
        return "user/profile"
    }

}