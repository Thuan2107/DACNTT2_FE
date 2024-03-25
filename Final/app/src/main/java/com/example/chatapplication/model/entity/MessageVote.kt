package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 03/04/2023
 */
class MessageVote {
    @SerializedName("message_vote_id")
    var messageVoteId = ""

    @SerializedName("conversation_id")
    var conversationId = ""

    @SerializedName("created_at")
    var createdAt = ""

    @SerializedName("updated_at")
    var updatedAt = ""

    @SerializedName("end_time")
    var endTime = ""

    @SerializedName("is_pinned")
    var isPinned: Int = 0

    @SerializedName("content")
    var content = ""

    @SerializedName("status")
    var status = 0

    @SerializedName("no_of_option")
    var noOfOption = 0

    @SerializedName("no_of_user_vote")
    var noOfUserVote = 0

    @SerializedName("message_vote_option")
    var messageVoteOption: ArrayList<MessageVoteOption> = ArrayList()

    @SerializedName("user")
    var user = Sender()

    @SerializedName("is_add_option")
    var isAddOption = 0

    @SerializedName("is_choose_many_option")
    var isChooseManyOption = 0
}