package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 03/04/2023
 */
class MessageVoteOption {

    @SerializedName("message_vote_option_id")
    var messageVoteOptionId = 0

    @SerializedName("no_of_vote")
    var noOfVote = 0

    @SerializedName("content")
    var content = ""

    @SerializedName("my_vote")
    var myVote = 0

    @SerializedName("message_vote_user")
    var messageVoteUser: ArrayList<Sender> = ArrayList()

    var isClick = 0

    constructor()

    constructor(content: String, myVote: Int) {
        this.content = content
        this.myVote = myVote
    }
}