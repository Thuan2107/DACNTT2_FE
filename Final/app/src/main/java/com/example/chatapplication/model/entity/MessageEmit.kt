package com.example.chatapplication.model.entity

import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.TimeUtils
import com.google.gson.annotations.SerializedName


class MessageEmit {
    @SerializedName("message")
    var message = ""

    @SerializedName("conversation_id")
    var conversationId = ""

    @SerializedName("type")
    var type = 1

    @SerializedName("create_at")
    var createAt: String = createCurrentTime()

    @SerializedName("key_error")
    var keyError = ""

    @SerializedName("media")
    var media = ArrayList<String>()

    constructor()

    fun createCurrentTime(): String {
        return TimeUtils.getCurrentTimeFormat("yyyy-MM-dd HH:mm:ss", isTimeZoneUTC = true)
    }
}