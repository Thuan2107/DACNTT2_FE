package com.example.chatapplication.model.entity

import com.google.gson.annotations.SerializedName


class ChatSocket {

    @SerializedName("message_id")
    var messageId: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("user")
    var user: Sender = Sender()

    @SerializedName("user_target")
    var userTarget: ArrayList<Sender> = ArrayList()

    @SerializedName("tag")
    var tag: ArrayList<UserTag> = ArrayList()

    @SerializedName("message")
    var message: String = ""

    @SerializedName("thumb")
    var thumb: Thumbnail = Thumbnail()

    @SerializedName("media")
    var media: ArrayList<MediaList> = ArrayList()

    @SerializedName("conversation")
    var conversation: Conversation = Conversation()


    @SerializedName("is_timeline")
    var isTimeline: Int = 0

    @SerializedName("no_of_reaction")
    var noOfReaction: Int = 0

    @SerializedName("no_of_like")
    var noOfLike: Int = 0

    @SerializedName("no_of_love")
    var noOfLove: Int = 0

    @SerializedName("no_of_haha")
    var noOfHaha: Int = 0

    @SerializedName("no_of_wow")
    var noOfWow: Int = 0

    @SerializedName("no_of_sad")
    var noOfSad: Int = 0

    @SerializedName("no_of_angry")
    var noOfAngry: Int = 0

    @SerializedName("my_reaction")
    var myReaction: Int = 0

    @SerializedName("position")
    var position: String = ""

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("updated_at")
    var updatedAt = ""

    @SerializedName("key_error")
    var keyError = ""

    @SerializedName("is_last_vote")
    var isLastVote: Int = 0
}