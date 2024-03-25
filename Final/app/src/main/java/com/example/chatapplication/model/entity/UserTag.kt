package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class UserTag {
    @SerializedName("user")
    var user = Sender()

    @SerializedName("key")
    var key: String = ""
}