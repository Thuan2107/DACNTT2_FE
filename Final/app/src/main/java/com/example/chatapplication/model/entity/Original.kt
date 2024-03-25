package com.example.chatapplication.model.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Original {
    @SerializedName("url")
    var url: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("size")
    var size: Int = 0

    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0

    constructor()

    constructor(url: String) {
        this.url = url
    }


}