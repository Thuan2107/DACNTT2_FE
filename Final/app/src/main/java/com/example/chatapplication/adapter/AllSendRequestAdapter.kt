package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ItemAllRequestFriendBinding
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter


class AllSendRequestAdapter(context: Context) : AppAdapter<Friend>(context) {

    private var friendRequestInterface: FriendRequestInterface? = null

    fun setClickFriendRequest(friendRequestInterface: FriendRequestInterface) {
        this.friendRequestInterface = friendRequestInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAllRequestFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemAllRequestFriendBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            binding.btnAgree.hide()
            binding.btnDenied.setBackgroundResource(R.drawable.bg_refuse_272)
            binding.btnDenied.text = getString(R.string.recall)
            PhotoLoadUtils.loadImageAvatarByGlide(
                binding.imgAvatar,
                item.avatar
            )
            binding.txtFullName.text = item.fullName

            binding.btnAgree.setOnClickListener {
                friendRequestInterface!!.clickAgree(position)
            }
            binding.btnDenied.setOnClickListener {
                friendRequestInterface!!.clickClose(item)
                binding.tvToast.text = getString(R.string.recall_notify)
                binding.tvToast.setTextColor(getColor(R.color.black))
                binding.tvToast.show()
                binding.llButton.hide()
                item.contactType = 1
            }
            if (item.contactType == 1) {
                binding.tvToast.text = getString(R.string.recall_notify)
                binding.tvToast.setTextColor(getColor(R.color.black))
                binding.tvToast.show()
                binding.llButton.hide()
            }

            binding.imgAvatar.setOnClickListener {
                friendRequestInterface!!.clickAvatar(position)
            }
        }
    }

    interface FriendRequestInterface {
        fun clickAgree(position: Int)
        fun clickClose(item: Friend)
        fun clickProfile(position: Int)
        fun clickAvatar(position: Int)
    }
}