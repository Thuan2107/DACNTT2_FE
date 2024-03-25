package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.databinding.ItemUserViewMessageBinding
import com.example.chatapplication.model.entity.MessageView
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter

class UserViewMessageAdapter constructor(context: Context) : AppAdapter<MessageView>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemUserViewMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemUserViewMessageBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            PhotoLoadUtils.loadImageAvatarByGlide(binding.civAvatar, item.avatar)
        }
    }
}