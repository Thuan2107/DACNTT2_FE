package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class Typing {
    @SerializedName("user_id")
    var userId: String = ""

    @SerializedName("user")
    var user: Sender = Sender()

    @SerializedName("conversation_id")
    var conversationId: String = ""
}