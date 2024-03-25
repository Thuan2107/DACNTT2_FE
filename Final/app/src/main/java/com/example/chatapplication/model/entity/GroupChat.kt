package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class GroupChat {
    @SerializedName("_id")
    var id: String = ""

    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("conversation_system_id")
    var conversationSystemId: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("contact_type")
    var contactType: Int = 0

    @SerializedName("last_message")
    var lastMessage: LastMessage = LastMessage()

    @SerializedName("is_pinned")
    var isPinned: Int = 0

    @SerializedName("is_notify")
    var isNotify: Int = 0

    @SerializedName("is_confirm_new_member")
    var isConfirmNewMember: Int = 0

    @SerializedName("is_online")
    var isOnline: Int = 0

    @SerializedName("is_hidden")
    var isHidden: Int = 0

    @SerializedName("my_permission")
    var myPermission: Int = 0

    @SerializedName("members")
    var members: ArrayList<Sender> = ArrayList()

    @SerializedName("no_of_member")
    var noOfMember: Int = 0

    @SerializedName("no_of_not_seen")
    var noOfNotSeen: Int = 0

    @SerializedName("background")
    var background: String = ""

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("position")
    var position: String = ""

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("updated_at")
    var updatedAt: String = ""

    @SerializedName("medias")
    var medias: ArrayList<String> = ArrayList()

    @SerializedName("link_join")
    var linkJoin: String = ""

    @SerializedName("event")
    var event: Int = 0

    @SerializedName("user_block_type")
    var userBlockType: Int = 0

    @SerializedName("status")
    var userStatus: Int = 0

    @SerializedName("user_target_id")
    var userTargetId: String = ""

    var checked: Int = 0

    constructor()
    constructor(conversationId: String) {
        this.conversationId = conversationId  //id cuộc trò chuyện
    }
}