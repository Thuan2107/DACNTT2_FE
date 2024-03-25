package com.example.chatapplication.router

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/04/2023
 */
@Suppress("FunctionName")
object ApiRouterFriend {
    private const val FRIEND = "friend"
    fun API_FRIEND_REQUEST_LIST(): String {
        return "REQUEST_FRIEND/list"
    }

    fun API_FRIEND_REQUEST_SEND(): String {
        return "REQUEST_FRIEND/send"
    }

    fun API_ADD_FRIEND(userId: String): String {
        return "friend/accept/$userId"
    }

    fun API_SEND_REQUEST_FRIEND(id: String): String {
        return "friend/send/$id"
    }

    fun API_REQUEST_FRIEND_QR(id : Long): String {
        return "REQUEST_FRIEND/$id/qr-code"
    }

    fun API_COUNT(): String {
        return "REQUEST_FRIEND/count-tab"
    }

    fun API_NOT_ACCEPT(idUser: String): String {
        return "$FRIEND/denied/$idUser"
    }

    fun API_REMOVE_REQUEST_FRIEND(id: String): String {
        return "$FRIEND/remove-request/$id"
    }

    fun API_GET_LIST_FRIEND(): String {
        return "$FRIEND"
    }

    fun API_MY_FRIEND(idUser: Int): String {
        return "FRIEND/$idUser"
    }

    fun API_UN_FRIEND(idUser: String): String {
        return "$FRIEND/remove-friend/$idUser"
    }

    fun API_FRIEND_SUGGEST(): String {
        return "SUGGEST_FRIEND"
    }

    fun API_SKIP_REQUEST_FRIEND(userId: Long): String{
        return "SUGGEST_FRIEND/${userId}/remove"
    }
}