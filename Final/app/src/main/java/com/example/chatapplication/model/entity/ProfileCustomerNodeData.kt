package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class ProfileCustomerNodeData {
    @SerializedName("_id")
    var userId: String = ""

    @SerializedName("nick_name")
    var nickName: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("address")
    var address: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("birthday")
    var birthday: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("gender")
    var gender: Int = 0

    @SerializedName("contact_type")
    var contactType: Int = 0

    @SerializedName("is_enable_birthday")
    var isEnableBirthday: Int = 0

    @SerializedName("is_enable_phone")
    var isEnablePhone: Int = 0

    @SerializedName("is_enable_email")
    var isEnableEmail: Int = 0

    @SerializedName("is_enable_address")
    var isEnableAddress: Int = 0

    @SerializedName("is_enable_city")
    var isEnableCity: Int = 0

    @SerializedName("is_enable_district")
    var isEnableDistrict: Int = 0

    @SerializedName("is_enable_ward")
    var isEnableWard: Int = 0

    @SerializedName("is_display_name")
    var isDisplayNickName: Int = 0

    @SerializedName("is_enable_gender")
    var isEnableGender: Int = 0

    @SerializedName("mutual_friend")
    var mutualFriend: Int = 0

    @SerializedName("no_of_alo_point")
    var noOfAloPoint: Int = 0

    @SerializedName("is_online")
    var online = 0

    @SerializedName("identification")
    var identification = 0

    @SerializedName("follow_type")
    var followType = 0
}
