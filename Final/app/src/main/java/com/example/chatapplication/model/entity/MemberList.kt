package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName


class MemberList {

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("user_id")
    var userId: String = ""

    @SerializedName("contact_type")
    var contactType: Int = 0

    @SerializedName("permission")
    var permission: Int = 0

    @SerializedName("is_online")
    var isOnline: Int = 0

    var isSelected = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberList) return false
        return userId == other.userId
    }

    // Override the hashCode() method to use the id field of the model object
    override fun hashCode(): Int {
        return userId.hashCode()
    }

    constructor()
    constructor(fullName: String, userId: String) {
        this.fullName = fullName
        this.userId = userId
    }

    constructor(userId: String, fullName: String, avatar: String, permission: Int) {
        this.userId = userId
        this.fullName = fullName
        this.avatar = avatar
        this.permission = permission
    }

    constructor(userId: String, fullName: String, avatar: String, permission: Int, contactType: Int) {
        this.userId = userId
        this.fullName = fullName
        this.avatar = avatar
        this.permission = permission
        this.contactType = contactType
    }
}