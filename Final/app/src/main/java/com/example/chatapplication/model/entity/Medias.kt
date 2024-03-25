package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName
import com.hjq.http.annotation.HttpHeader
import com.hjq.http.annotation.HttpRename

/**
 * @Author:Hồ Quang Tùng
 * @Date: 29/11/2022
 */
class Medias {
    @HttpRename("name")
    @SerializedName("name")
    var name: String = ""

    @HttpRename("type")
    @SerializedName("type")
    var type: Int = 0

    @HttpRename("size")
    @SerializedName("size")
    var size: Long = 0

    @HttpRename("width")
    @SerializedName("width")
    var width: Int? = 0

    @HttpRename("height")
    @SerializedName("height")
    var height: Int? = 0

    @HttpRename("is_keep")
    var isKeep: Int? = 0

    @HttpRename("realPath")
    @SerializedName("realPath")
    var realPath: String? = ""


}