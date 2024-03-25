package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName


class GroupSocket {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("avatar")
    var avatar: MediaList = MediaList()

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("is_pinned")
    var isPinned: Int = 0

    @SerializedName("is_notify")
    var isNotify: Int = 0

    @SerializedName("is_confirm_new_member")
    var isConfirmNewMember: Int = 0

    @SerializedName("my_permission")
    var myPermission: Int = 0

    @SerializedName("no_of_member")
    var noOfMember: Int = 0

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("last_message")
    var lastMessage  = LastMessage()
}