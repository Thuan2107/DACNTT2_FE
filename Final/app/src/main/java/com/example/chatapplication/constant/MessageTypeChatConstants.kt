package com.example.chatapplication.constant


class MessageTypeChatConstants {
    companion object {
        const val EMPTY = 0
        const val TEXT = 1
        const val IMAGE = 2
        const val VIDEO = 3
        const val AUDIO = 4
        const val FILE = 5
        const val REPLY = 6
        const val UPDATE_NAME = 7
        const val UPDATE_AVATAR = 8
        const val UPDATE_BACKGROUND = 9
        const val REMOVE_USER = 10
        const val ADD_NEW_USER = 11
        const val CHANGE_PERMISSION_USER = 12 // (tin nhắn cho mọi người trong room khi có người được đổi quyền thành trưởng, phó nhóm)
        const val USER_OUT_GROUP = 13
        const val REVOKE_MESSAGE = 14 //(tin nhắn thu hồi)
        const val STICKER = 15
        const val NEW_GROUP = 16 //(tin nhắn của nhóm mới được tạo)
        const val UPDATE_PROFILE = 18
        const val PINNED = 19
        const val CREATE_REMINDER = 20
        const val CREATE_VOTE = 21
        const val CONFIRM_NEW_MEMBER = 22
        const val OFF_IS_CONFIRM_NEW_MEMBER = 23
        const val VIEW_OLD_MESSAGE_WITH_NEW_MEMBER = 24 //view_old_message_with_new_member
        const val JOIN_WITH_LINK = 25 //join_with_link
        const val SEND_MESSAGE = 26 //send_message
        const val MESSAGE_PINNED = 27 //pinned_message
        const val MESSAGE_REMOVE_PINNED = 28 //remove_pinned_message
        const val MESSAGE_ADD_DEPUTY_CONVERSATION = 29 //tin nhắn thêm phó nhóm
        const val MESSAGE_REMOVE_DEPUTY_CONVERSATION = 30 //tin nhắn gỡ phó nhóm
        const val MESSAGE_CREATE_VOTE = 31 //tạo bài vote
        const val MESSAGE_VOTE = 32//tin nhắn vote
        const val MESSAGE_CHANGE_VOTE = 33 //thay đổi bình chọn
        const val ADD_OPTION_VOTE = 34 //add bình chọn
        const val MEMBERS_WAITING_GROUP = 35 //duyệt thành viên
        const val BLOCK_VOTE = 36 // khoá bình chọn
        const val PINNED_VOTE = 37 // ghim bình chọn
        const val REMOVE_PINNED_VOTE = 38 // gỡ ghim bình chọn


        //Chưa có type này
        const val CONTACT = 100
        const val DATA_MESSAGE = "DATA_MESSAGE"
        const val SHARE_MESSAGE = "SHARE_MESSAGE"

        //Vote
        const val KEY_FLOW = "KEY_FLOW"

    }
}