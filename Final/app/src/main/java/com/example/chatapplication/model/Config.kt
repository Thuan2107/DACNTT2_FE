package com.example.chatapplication.model

import com.google.gson.annotations.SerializedName

class Config {

    @SerializedName("api_connection")
    var apiConnection: String = ""

    @SerializedName("realtime_domain")
    var realtimeDomain = ""

    @SerializedName("api_key")
    var apiKey = ""

    @SerializedName("restaurant_name")
    var restaurantName = ""

    @SerializedName("api_upload")
    var apiUpload = ""

    @SerializedName("api_upload_short")
    var apiUploadV2 = ""


    @SerializedName("session")
    var session = ""

    @SerializedName("socket_conect_login")
    var socketConectLogin = ""
}