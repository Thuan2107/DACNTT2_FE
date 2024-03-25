package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName


class SearchUser {
    @SerializedName("user")
    var user = ProfileCustomerNodeData()
}