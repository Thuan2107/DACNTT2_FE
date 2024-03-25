package com.example.chatapplication.holder

import android.view.View
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.adapter.MessageAdapter
import com.example.chatapplication.databinding.ItemMessageImageLeftBinding
import com.example.chatapplication.model.entity.Message
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils

class ImageLeftHandle(
    private val binding: ItemMessageImageLeftBinding,
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
            binding.tvTime,
            binding.tvNameMedia,
            binding.ivAvatar,
            position
        )

        adapter.setMarginStart(binding.root, binding.time.llTimeHeader, position)

        adapter.setProfilePersonChat(binding.tvNameMedia, binding.ivAvatar, data)

        val screen = IntArray(2)
        binding.ctlContainer.getLocationOnScreen(screen)

        binding.ivOneOne.setOnLongClickListener {
            chatHandle.onRevoke(data, binding.root, screen[1], null)
            true
        }

        setMarginContainer()

        if (data.media.size == 1) {
            binding.ctlImageOne.show()
            binding.llImageFourMore.hide()

            PhotoLoadUtils.resizeImageClip(
                data.media[0],
                binding.ivOneOne,
            )
            binding.ivOneOne.setOnClickListener {
                AppUtils.disableClickAction(binding.ivOneOne, 500)
                AppUtils.showMediaNewsFeed(adapter.getContext(), data.media, position)
            }
        } else {
            binding.ctlImageOne.hide()
            binding.llImageFourMore.show()
        }
    }

    private fun setMarginContainer() {


    }
}