package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.databinding.ItemListUserAvatarBinding
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter

class MemberGroupInfoAdapter (context: Context) : AppAdapter<User>(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding =
            ItemListUserAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (getData().size > 6) {
            6
        } else {
            getData().size
        }
    }

    inner class ViewHolder(private val binding: ItemListUserAvatarBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            if (position == 5) {
                binding.tvMemberCount.show()
                binding.ivAvatar.hide()
                binding.tvMemberCount.text = "${(getData().size - 5)}+"
            } else {
                binding.tvMemberCount.hide()
                binding.ivAvatar.show()
                PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatar, item.avatar)
            }
        }
    }
}
