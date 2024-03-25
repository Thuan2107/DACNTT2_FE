package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class ChangeName {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("name")
    var name: String = ""

}