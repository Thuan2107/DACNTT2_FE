package com.example.chatapplication.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.databinding.DialogSelectionChatBinding
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show

/**
 * @Author:HO QUANG TUNG
 * @Date: 05/04/2023
 */
class DialogSelectionChat {
    class Builder(
        context: Context, group: GroupChat
    ) : BaseDialog.Builder<Builder>(context) {
        private var listener: OnListener? = null
        private var group: GroupChat = GroupChat()
        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        private var binding = DialogSelectionChatBinding.inflate(LayoutInflater.from(context))

        init {
            setCancelable(true)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            setContentView(binding.root)
            this.group = group

            binding.llPinnedChat.isVisible =
                group.isPinned == 0 && checkUserActive()//Ghim cuộc trò chuyện
            binding.llUnPinnedChat.isVisible =
                group.isPinned != 0 && checkUserActive()//Bỏ ghim cuộc trò chuyện
            binding.llOutGroupChat.isVisible = group.type == AppConstants.TYPE_GROUP//Rời nhóm


            binding.llDeleteChat.show()//Xóa lịch sử cuộc trò chuyện

            setOnClickListener(
                binding.llPinnedChat,
                binding.llUnPinnedChat,
                binding.llDeleteChat,
                binding.llOutGroupChat
            )
        }

        private fun checkUserActive(): Boolean {
            return if (group.type == AppConstants.TYPE_PRIVATE) {
                group.userStatus == AppConstants.STATUS_ACTIVE
            } else {
                true
            }
        }

        override fun onClick(view: View) {
            when (view) {
                binding.llPinnedChat -> {
                    binding.llPinnedChat.hide()
                    binding.llUnPinnedChat.show()
                    listener!!.onSelectionChat(
                        group.id,
                        ChatConstants.TYPE_PINNED,
                        getDialog()!!
                    )
                }

                binding.llUnPinnedChat -> {
                    binding.llPinnedChat.show()
                    binding.llUnPinnedChat.hide()
                    listener!!.onSelectionChat(
                        group.id,
                        ChatConstants.TYPE_UN_PINNED,
                        getDialog()!!
                    )
                }


                binding.llDeleteChat -> {
                    listener!!.onSelectionChat(
                        group.id,
                        ChatConstants.TYPE_DELETE,
                        getDialog()!!
                    )
                }

                binding.llOutGroupChat -> {
                    listener!!.onSelectionChat(
                        group.id,
                        ChatConstants.TYPE_OUT_GROUP,
                        getDialog()!!
                    )
                }

                else -> {
                    dismiss()
                }
            }
        }

    }

    interface OnListener {
        fun onSelectionChat(
            idGroup: String, type: Int, dialog: Dialog
        )
    }
}
