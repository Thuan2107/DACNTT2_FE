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
import com.example.chatapplication.activity.AddMemberGroupActivity
import com.example.chatapplication.activity.CreateGroupChatActivity
import com.example.chatapplication.activity.InfoCustomerActivity
import com.example.chatapplication.activity.SearchManagerActivity
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.ActionGroupChatDialogBinding
import com.example.chatapplication.databinding.ItemMoreDialogBinding
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.google.gson.Gson
import timber.log.Timber

class ActionGroupChatDialog {
    @SuppressLint("IntentWithNullActionLaunch")
    class Builder(
        context: Context,
    ) : BaseDialog.Builder<Builder>(context) {

        private val binding: ActionGroupChatDialogBinding =
            ActionGroupChatDialogBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            setContentView(binding.root)


            binding.llCreateGroup.setOnClickListener {
                try {
                    val intent = Intent(context, CreateGroupChatActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Timber.e(e.message)
                }
            }

            binding.llJoinWithLink.setOnClickListener {
                try {
                    val intent = Intent(context, SearchManagerActivity::class.java)
                    val bundle = Bundle()
                    bundle.putBoolean(AppConstants.JOIN_GROUP, true)
                    intent.putExtras(bundle)
                    startActivity(intent)
                } catch (e: Exception) {
                    Timber.e(e.message)
                }
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