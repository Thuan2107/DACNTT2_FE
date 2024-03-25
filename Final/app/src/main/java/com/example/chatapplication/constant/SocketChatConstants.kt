package com.example.chatapplication.constant

/**
 * @AuthorUpdate: HO QUANG TUNG
 * @Date: 02/01/2023
 */
class SocketChatConstants {
    companion object {
        const val version = "-v2"
        const val JOIN_ROOM = "join-room"
        const val LEAVE_ROOM = "leave-room"

        //typing on
        const val ON_TYPING_ON = "typing-on"
        const val EMIT_TYPING_ON = "typing-on"

        //typing off
        const val ON_TYPING_OFF = "typing-off"
        const val EMIT_TYPING_OFF = "typing-off"

        //message
        const val EMIT_MESSAGE = "message"
        const val ON_MESSAGE = "message"

        //Socket Error
        const val ON_SOCKET_ERROR = "socket-error$version"

        //Nghe join room
        const val ON_JOIN_ROOM = "join-room"


    }
}