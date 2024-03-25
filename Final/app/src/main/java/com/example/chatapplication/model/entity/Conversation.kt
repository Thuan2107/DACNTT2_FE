package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class Conversation {
    @SerializedName("_id")
    var conversationId: String = ""

    @SerializedName("name")
    var name: String = ""


    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("background")
    var background: String = ""

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

    @SerializedName("no_of_not_seen")
    var noOfNotSeen: Int = 0

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("position")
    var position: String = ""

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("updated_at")
    var updatedAt = ""

    @SerializedName("status")
    var userStatus: Int = 0
}