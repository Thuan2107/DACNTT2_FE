package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName


class LastMessage {
    @SerializedName("message_id")
    var messageId: String = ""//group chat

    @SerializedName("promotion_id")
    var promotionId: String = ""//group OA

    @SerializedName("type")
    var type: Int = 0//group chat và group OA

    @SerializedName("message")
    var message: String = ""//group chat

    @SerializedName("title")
    var title: String = ""//group OA

    @SerializedName("description")
    var description: String = ""//group OA

    @SerializedName("user")
    var user: Sender = Sender()//group chat

    @SerializedName("user_target")
    var userTarget: ArrayList<Sender> = ArrayList()//group chat

    @SerializedName("tag")
    var tag: ArrayList<UserTag> = ArrayList()//group chat

    @SerializedName("thumb")
    var thumb: Thumbnail = Thumbnail()//group chat
}
   
