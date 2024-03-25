package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class MediaStore {
    @SerializedName("time")
    var time: String = ""

    @SerializedName("user")
    var user: User = User()

    @SerializedName("message_id")
    var messageId: String = ""

    @SerializedName("media")
    var media: MediaList = MediaList()

    @SerializedName("thumb")
    var thumb: Thumbnail = Thumbnail()

    @SerializedName("created_at")
    var createdAt: String = ""

    constructor()

    constructor(messageId: String, createdAt: String) {
        this.messageId = messageId
        this.createdAt = createdAt
    }
}
