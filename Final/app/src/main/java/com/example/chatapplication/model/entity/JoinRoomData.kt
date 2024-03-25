package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class JoinRoomData {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("no_of_not_seen")
    var noOfNotSeen: Int = 0
}
