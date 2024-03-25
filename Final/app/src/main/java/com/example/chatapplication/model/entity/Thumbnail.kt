package com.example.chatapplication.model.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.hjq.http.annotation.HttpRename

class Thumbnail {
    @HttpRename("object_id")
    @SerializedName("object_id")
    var objectId: String = ""

    @HttpRename("domain")
    @SerializedName("domain")
    var domain: String = ""

    @HttpRename("title")
    @SerializedName("title")
    var title: String = ""

    @HttpRename("description")
    @SerializedName("description")
    var description: String = ""

    @HttpRename("logo")
    @SerializedName("logo")
    var logo: String = ""

    @HttpRename("url")
    @SerializedName("url")
    var url: String = ""

    @HttpRename("is_thumb")
    @SerializedName("is_thumb")
    var isThumb: Int = 0

    @HttpRename("is_system")
    @SerializedName("is_system")
    var isSystem: Int = 0

    @HttpRename("type_system")
    @SerializedName("type_system")
    var typeSystem: Int = 0

}