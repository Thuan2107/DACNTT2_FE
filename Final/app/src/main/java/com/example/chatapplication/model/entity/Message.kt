package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class Message {
    @SerializedName("_id")
    var messageId: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("user_target")
    var userTarget: ArrayList<Sender> = ArrayList()

    @SerializedName("user")
    var user: Sender = Sender()

    @SerializedName("message")
    var message: String = ""

    @SerializedName("media")
    var media: ArrayList<String> = ArrayList()

    @SerializedName("conversation")
    var conversation: Conversation = Conversation()

    @SerializedName("position")
    var position: String = ""

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("updated_at")
    var updatedAt = ""

    @SerializedName("key_error")
    var keyError = ""

    @SerializedName("is_timeline")
    var isTimeline: Int = 0

    @SerializedName("message_view")
    var messageView : ArrayList<MessageView> = ArrayList()


    var highlight = 0

    var isServerResponse = true

    var isErrorMessage = false
}