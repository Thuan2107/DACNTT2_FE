package com.example.chatapplication.holder

import android.view.View
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.adapter.MessageAdapter
import com.example.chatapplication.constant.MessageTypeChatConstants
import com.example.chatapplication.databinding.ItemMessageTextRightBinding
import com.example.chatapplication.model.entity.Message


class TextRightHandle(
    private val binding: ItemMessageTextRightBinding,
    private val data: Message,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter
) {
    fun setData() {
        if (data.type == MessageTypeChatConstants.TEXT) {
            //Default data

            adapter.checkTimeMessages(
                data.isTimeline,
                data.createdAt,
                binding.time.llTimeHeader,
                binding.time.tvTimeHeader,
                binding.message.tvTime,
                position
            )

            adapter.checkUserViewMessage(
                binding.rcvUserView,
                binding.send.llSendMessage,
                data,
                position,
                binding.llView,
                binding.llUserView,
                binding.tvMoreView
            )

            adapter.handleStatusMessage(binding.send.tvTextUserView,data)

            adapter.setMarginStart(binding.root, binding.time.llTimeHeader, position)


            val lp = binding.ctlText.layoutParams as ViewGroup.MarginLayoutParams

            lp.setMargins(
                0,
                0,
                0,
                adapter.getContext().resources.getDimension(R.dimen.dp_4)
                    .toInt()
            )


            binding.ctlText.setOnLongClickListener {
                chatHandle!!.onRevoke(
                    data,
                    binding.ctlMessage,
                    binding.ctlMessage.y.toInt(),
                    binding.message.tvMessage
                )
                true
            }


        } else {
            binding.root.visibility = View.GONE
        }
    }

}