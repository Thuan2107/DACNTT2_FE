package com.example.chatapplication.constant


class InfoCustomerConstants {
    companion object {
        const val DISABLE = 0
        const val DATA_CUSTOMER = "DATA_CUSTOMER"
        const val ENABLE = 1

        //(1-người lạ, 2-họ gửi lời mời, 3-mình gửi lời mời, 4-bạn bè)
        const val ITS_ME = 1 // chính mình
        const val NOT_FRIEND = 0 // chưa là bạn
        const val WAITING_CONFIRM = 2 // họ gửi lời mời, đợi mình xác nhận
        const val WAITING_RESPONSE = 3 // mình gửi lời mời, đợi họ phản hồi
        const val FRIEND = 4
    }
}