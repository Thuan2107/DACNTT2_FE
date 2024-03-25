package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName


class GroupInfo {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("is_confirm_new_member")
    var isConfirmNewMember: Int = 0

    @SerializedName("is_join")
    var isJoin: Int = 0

    @SerializedName("no_of_member")
    var noOfMember: Int = 0

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("background")
    var background: MediaList = MediaList()

    @SerializedName("link_join")
    var linkJoin: String = ""

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("updated_at")
    var updatedAt: String = ""

    @SerializedName("members")
    var members: ArrayList<User> = ArrayList()
}