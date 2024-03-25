package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

class ListenJoinRoomSocket {
    @SerializedName("user_id")
    var userId: Int = 0

    @SerializedName("event")
    var event: String = ""

    @SerializedName("data")
    var data: JoinRoomData = JoinRoomData()
}
