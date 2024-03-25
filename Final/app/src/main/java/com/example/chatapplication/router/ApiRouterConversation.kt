package com.example.chatapplication.router

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/04/2023
 */
@Suppress("FunctionName")
object ApiRouterConversation {
    private const val CONVERSATION = "conversation"
    private const val CONVERSATION_GROUP = "conversation-group"

    fun API_CREATE_CONVERSATION(): String {
        return "$CONVERSATION/create"
    }

    fun API_DELETE_GROUP(conversationId : String): String {
        return "$CONVERSATION/delete/$conversationId"
    }

    fun API_DETAIL_GROUP(): String {
        return "$CONVERSATION/detail"
    }

    fun API_GET_CONVERSATION(): String {
        return "$CONVERSATION"
    }

    fun API_GET_CONVERSATION_PINNED(): String {
        return "$CONVERSATION/pinned"
    }

    fun API_GET_GROUP_INFO(linkJoin: String): String {
        return "$CONVERSATION/${linkJoin}/detail-link-join"
    }

    fun API_JOIN_LINK_GROUP(linkJoin: String): String {
        return "$CONVERSATION_GROUP/${linkJoin}/join-with-link"
    }

    fun API_PINNED_GROUP(idGroup: String): String {
        return "$CONVERSATION/$idGroup/pinned"
    }


    fun API_SETTING_GROUP_CHAT(id: String): String {
        return "$CONVERSATION/$id/settings"
    }

    fun API_OUT_GROUP(id: String): String {
        return "$CONVERSATION_GROUP/${id}/leave"
    }


    fun API_ADD_MEMBER(id: String): String {
        return "$CONVERSATION_GROUP/$id/add-member"
    }

    fun API_ADD_PERMISSION_DEPUTY(idGroup: String): String {
        return "$CONVERSATION_GROUP/${idGroup}/add-deputy"
    }

    fun API_ADD_PERMISSION_LEADER(idGroup: String): String {
        return "$CONVERSATION_GROUP/${idGroup}/update-owner-permission"
    }

    fun API_CREATE_GROUP(): String {
        return "$CONVERSATION_GROUP/create"
    }

    fun API_CHANGE_NAME_GROUP(id: String): String {
        return "$CONVERSATION/${id}/update-name"
    }

    fun API_CHANGE_AVATAR_GROUP(id: String): String {
        return "$CONVERSATION/${id}/update-avatar"
    }

    fun API_CHANGE_BACKGROUND_GROUP(id: String): String {
        return "$CONVERSATION/${id}/update-background"
    }

    fun API_DIS_BRAND(idGroup: String): String {
        return "$CONVERSATION_GROUP/$idGroup/disband"
    }

    fun API_GROUP_MEMBER(id: String): String {
        return "$CONVERSATION_GROUP/${id}/member/list"
    }


    fun API_REMOVE_MEMBER_GROUP(id: String): String {
        return "$CONVERSATION_GROUP/$id/remove-member"
    }

    fun API_UPDATE_PERMISSION(idGroup: String): String {
        return "$CONVERSATION_GROUP/${idGroup}/update-permission"
    }

}