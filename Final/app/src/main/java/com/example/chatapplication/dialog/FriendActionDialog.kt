package com.example.chatapplication.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.databinding.DialogFriendActionBinding
import com.example.chatapplication.model.entity.Friend


class FriendActionDialog {
    class Builder(context: Context, val data: Friend) : BaseDialog.Builder<Builder>(context) {
        private val binding: DialogFriendActionBinding =
            DialogFriendActionBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)

            binding.clUnFriend.setOnClickListener {
                dismiss()
                DialogConfirmUnfriend.Builder(context, data)
                    .setListener(object : DialogConfirmUnfriend.OnListener {
                        override fun onConfirm() {
                            listener?.onUnFriendUser(data.userId)
                        }
                    }).show()
            }

            setContentView(binding.root)
        }

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }
    }

    interface OnListener {
        fun onUnFriendUser(idUser: String)
    }
}