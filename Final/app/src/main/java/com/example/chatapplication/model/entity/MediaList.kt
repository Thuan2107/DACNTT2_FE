package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName


class MediaList {
    @SerializedName("content")
    var content: String = ""

    @SerializedName("media_id")
    var mediaId: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("original")
    var original: String = ""

    @SerializedName("medium")
    var medium: Medium = Medium()

    @SerializedName("thumb")
    var thumb: Thumb = Thumb()

    @SerializedName("position")
    var position: String = ""

    @SerializedName("path_local")
    var pathLocal = ""

    @SerializedName("stop")
    var stop: Boolean = false

    @SerializedName("user")
    var user: User = User()

    var seekTo = 0
    var seekToVideo = 0L
    var isPlayVideo = true
    var play = false

    constructor()

    constructor(original: String, type: Int) {
        this.original = original
        this.type = type
    }

    constructor(mediaId: String, original: String) {
        this.mediaId = mediaId
        this.original = original
    }

    constructor(url: String, pathLocal: String, name: String, type: Int) {
        this.original = url
        this.pathLocal = pathLocal
        this.type = type
    }
}