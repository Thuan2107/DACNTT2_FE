package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class Sender {
    @SerializedName("_id")
    var userId = ""

    @SerializedName("full_name")
    var fullName = ""

    @SerializedName("avatar")
    var avatar = ""

    @SerializedName("identification")
    var identification = 0

    constructor()
    constructor(userId: String, fullName: String, avatar: String) {
        this.userId = userId
        this.fullName = fullName
        this.avatar = avatar
    }
}