package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.databinding.ItemUserBottomCreateGroupBinding
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter

class ChooseFriendAdapter constructor(context: Context) : AppAdapter<Friend>(context) {

    private var clickRemoveItem: RemoveItemListener? = null
    fun setClickRemove(clickRemoveItem: RemoveItemListener) {
        this.clickRemoveItem = clickRemoveItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemUserBottomCreateGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)

    }

    inner class ViewHolder(private val binding: ItemUserBottomCreateGroupBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatar, item.avatar)
            itemView.setOnClickListener {
                clickRemoveItem!!.removeItem(position)
            }

            binding.ibClose.setOnClickListener {
                clickRemoveItem!!.removeItem(position)
            }
        }
    }
    interface RemoveItemListener {
        fun removeItem(position: Int)
    }
}