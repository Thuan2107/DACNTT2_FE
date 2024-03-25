package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("_id")
    var id: String = ""

    @SerializedName("user_id")
    var userId: String = ""

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("nick_name")
    var nickName: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("birthday")
    var birthday: String = ""

    @SerializedName("address_full_text")
    var addressFullText: String = ""

    @SerializedName("gender")
    var gender: Int = 2


    @SerializedName("access_token")
    var accessToken: String = ""

    @SerializedName("jwt_token")
    var jwtToken: String = ""


    @SerializedName("token_type")
    var tokenType: String = ""


    @SerializedName("type")
    var type: String = ""


    @SerializedName("restaurantId")
    var restaurantId: Int = 0
}


