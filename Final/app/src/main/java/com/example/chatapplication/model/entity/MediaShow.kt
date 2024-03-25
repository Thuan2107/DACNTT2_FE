package com.example.chatapplication.model.entity

import com.example.chatapplication.constant.UploadTypeConstants


class MediaShow {
    var url: String = ""
    var urlLocal: String = ""
    var name: String = ""
    var type: Int = UploadTypeConstants.UPLOAD_IMAGE
    var isPlay: Boolean = true
    var currentSeek: Long = 0L

    constructor(url: String, name: String, type: Int) {
        this.url = url
        this.name = name
        this.type = type
    }

    constructor(url: String, urlLocal: String, name: String, type: Int) {
        this.url = url
        this.urlLocal = urlLocal
        this.name = name
        this.type = type
    }

    constructor(url: String) {
        this.url = url
    }

    constructor(url: String, isPlay: Boolean, name: String, type: Int, currentSeek: Long) {
        this.url = url
        this.isPlay = isPlay
        this.name = name
        this.type = type
        this.currentSeek = currentSeek
    }
}