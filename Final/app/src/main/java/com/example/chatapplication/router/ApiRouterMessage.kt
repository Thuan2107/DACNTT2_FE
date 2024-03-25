package com.example.chatapplication.router

@Suppress("FunctionName")
object ApiRouterMessage {
    private const val MESSAGE = "message"
    fun API_DELETE_MESSAGE(id : String): String{
        return "${MESSAGE}/${id}/delete"
    }

    fun API_DELETE_PINNED(): String{
        return "${MESSAGE}/delete-pinned"
    }

    fun API_GET_MESSAGE(id : String): String{
        return "${MESSAGE}/$id"
    }

    fun API_LIST_REACTION(id : String): String{
        return "${MESSAGE}/${id}/reaction"
    }

    fun API_PINNED_MESSAGE(): String{
        return "${MESSAGE}/list-pinned"
    }

    fun API_SHARE_MESSAGE(): String{
        return "${MESSAGE}/share-message"
    }

    fun API_SHARE_LINK_JOIN_GROUP(): String{
        return "${MESSAGE}/share-link"
    }
}