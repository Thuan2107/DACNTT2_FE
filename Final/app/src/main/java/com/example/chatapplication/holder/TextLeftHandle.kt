package com.example.chatapplication.holder

import android.os.Build
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.adapter.MessageAdapter
import com.example.chatapplication.databinding.ItemMessageTextLeftBinding
import com.example.chatapplication.model.entity.Message

class TextLeftHandle(
    private val binding: ItemMessageTextLeftBinding,
    private val data: Message,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter
) {
    fun setData() {

        adapter.checkTimeMessagesTheir(
            data.isTimeline,
            data.createdAt,
            binding.time.llTimeHeader,
            binding.time.tvTimeHeader,
            binding.message.tvTime,
            binding.tvNameMedia,
            binding.ivAvatar,
            position
        )

        adapter.setMarginStart(binding.root, binding.time.llTimeHeader, position)

        val lp = binding.ctlText.layoutParams as ViewGroup.MarginLayoutParams

        lp.setMargins(
            0,
            0,
            0,
            adapter.getContext().resources.getDimension(R.dimen.dp_4).toInt()
        )

        adapter.setProfilePersonChat(binding.tvNameMedia, binding.ivAvatar, data)

        val screen = IntArray(2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.root.getLocationInSurface(screen)
        }

        binding.ctlText.setOnLongClickListener {
            chatHandle!!.onRevoke(
                data,
                binding.ctlMessage,
                binding.ctlMessage.y.toInt(),
                binding.message.tvMessage
            )
            true
        }
    }
}