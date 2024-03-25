package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class RemoveMember {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("member_id")
    var memberId: String = ""
}