package com.example.chatapplication.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.chatapplication.R
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.databinding.DialogCofirmBinding
import com.example.chatapplication.model.entity.Friend


class DialogConfirmUnfriend {
    class Builder (
        context: Context,
        val data: Friend
    ) : CommonDialog.Builder<Builder>(context) {
        private var binding: DialogCofirmBinding =
            DialogCofirmBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        init {
            setButtonView(false)
            setCancelable(true)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            binding.tvName.text = data.fullName
            binding.tvUnFriendMessage.text =
                context.getString(
                    R.string.message_confirm_unfriend,
                    getString(R.string.confirm_un_friend),
                    data.fullName
                )
            binding.tvUiCancel.setOnClickListener {
                dismiss()
            }
            binding.tvUiConfirm.setOnClickListener {
                listener!!.onConfirm()
                dismiss()
            }
            setCustomView(binding.root)

        }
    }

    interface OnListener {
        /**
         * bắn interface qua fragment xử lý
         */
        fun onConfirm()
    }
}