package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class Friend {

    @SerializedName("user_id")
    var userId: String = ""

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("nick_name")
    var nickName: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("gender")
    var gender: Int = 2

    @SerializedName("position")
    var position: String = ""

    @SerializedName("contact_type")
    var contactType: Int = -1


    @SerializedName("access_token")
    var accessToken: String = ""

    @SerializedName("jwt_token")
    var jwtToken: String = ""


    @SerializedName("token_type")
    var tokenType: String = ""


    @SerializedName("type")
    var type: String = ""

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("permission")
    var permission: Int = 0

    @SerializedName("is_member")
    var isMember = 0
}


