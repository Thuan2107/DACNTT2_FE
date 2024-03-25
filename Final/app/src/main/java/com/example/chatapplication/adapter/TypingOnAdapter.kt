package com.example.chatapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.databinding.ItemTypingOnBinding
import com.example.chatapplication.model.entity.Typing
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter

class TypingOnAdapter constructor(context: Context) : AppAdapter<Typing>(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemTypingOnBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (getData().size > 3) {
            3
        } else {
            getData().size
        }
    }

    inner class ViewHolder(private val binding: ItemTypingOnBinding) : AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            val item = getItem(position)
            PhotoLoadUtils.loadImageAvatarByGlide(binding.ivTypingUser, item.user.avatar)
        }
    }
}