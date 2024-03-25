package com.example.chatapplication.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.chatapplication.R
import com.example.chatapplication.R.*
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.activity.InfoCustomerActivity
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.ItemMoreDialogBinding
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import timber.log.Timber

class MoreDialog {
    @SuppressLint("IntentWithNullActionLaunch")
    class Builder(
        context: Context,
        data: Friend,
        key: Int
    ) : BaseDialog.Builder<Builder>(context) {

        private val binding: ItemMoreDialogBinding =
            ItemMoreDialogBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null
        private var item = data

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            setContentView(binding.root)

            if (key == 1) {
                binding.clUnFriend.isVisible = item.contactType == 4
                binding.llWall.show()
            } else {
                binding.clUnFriend.hide()
                binding.llWall.hide()
            }

            binding.tvWall.text =
                String.format("%s %s", getString(string.personal_page), item.fullName)
            binding.tvUnFriend.text =
                String.format("%s %s", getString(string.un_friend), item.fullName)

            binding.llWall.setOnClickListener {
                try {
                    val intent = Intent(
                        getContext(),
                        InfoCustomerActivity::class.java
                    )
                    val bundle = Bundle()
                    bundle.putString(
                        AppConstants.ID_USER, item.userId)
                    intent.putExtras(bundle)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    dismiss()
                } catch (e: Exception) {
                    Timber.e(e.message)
                }
            }

            binding.clUnFriend.setOnClickListener {
                dismiss()
                DialogConfirmUnfriend.Builder(context, item)
                    .setListener(object : DialogConfirmUnfriend.OnListener {
                        override fun onConfirm() {
                            listener!!.onUnFriendUser(item.userId)
                        }
                    }).show()
            }


        }
        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        interface OnListener {

            fun onUnFriendUser(idUser: String)
        }
    }
}