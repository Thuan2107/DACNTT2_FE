package com.example.chatapplication.holder

import android.view.View
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.adapter.MessageAdapter
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.databinding.ItemMessageImageRightBinding
import com.example.chatapplication.model.entity.Message
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils


class ImageRightHandle(
    private val binding: ItemMessageImageRightBinding,
    private val data: Message,
    private val position: Int,
    private val chatHandle: MessageAdapter.ChatHandle,
    private var adapter: MessageAdapter
) {
    fun setData() {
        adapter.checkTimeMessages(
            data.isTimeline,
            data.createdAt,
            binding.time.llTimeHeader,
            binding.time.tvTimeHeader,
            binding.tvTime,
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

        adapter.handleStatusMessage(binding.send.tvTextUserView, data)

        adapter.setMarginStart(binding.root, binding.time.llTimeHeader, position)

        setMarginContainer()

        val screen = IntArray(2)
        binding.ctlContainer.getLocationOnScreen(screen)

        binding.ivOneOne.setOnLongClickListener {
            chatHandle.onRevoke(data, binding.root, screen[1], null)
            true
        }

        if (data.media.size == 1) {
            binding.lnImageOne.show()
            binding.llImageFourMore.hide()
          
            PhotoLoadUtils.resizeImageClip(
                data.media[0].ifEmpty { data.media[0] },
                binding.ivOneOne,
            )

            binding.ivOneOne.setOnClickListener {
                AppUtils.disableClickAction(binding.ivOneOne, 500)
                AppUtils.showMediaNewsFeed(adapter.getContext(), data.media, 0)
            }
        } else {
            binding.lnImageOne.hide()
            binding.llImageFourMore.show()
        }

        binding.root.setOnLongClickListener {
            chatHandle.onRevoke(data, binding.root, screen[1], null)
            true
        }


    }

    private fun setMarginContainer() {
        val lp = binding.ctlVideo.layoutParams as ViewGroup.MarginLayoutParams
        if (binding.tvTime.visibility == View.GONE) {
            lp.setMargins(
                0,
                0,
                0,
                adapter.getResources().getDimension(R.dimen.dp_4)
                    .toInt()
            )
        } else {
            lp.setMargins(
                0,
                0,
                0,
                adapter.getResources().getDimension(R.dimen.dp_20)
                    .toInt()
            )
        }

    }
}