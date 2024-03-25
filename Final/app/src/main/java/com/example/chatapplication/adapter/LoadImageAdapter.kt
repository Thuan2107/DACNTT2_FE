package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.databinding.ItemLoadImageBinding
import com.example.chatapplication.model.entity.MediaList
import com.example.chatapplication.model.entity.Message
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter


class LoadImageAdapter(
    context: Context,
    var message: Message,
    var y: Int,
    var isAllowLongClick: Boolean
) :
    AppAdapter<String>(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding =
            ItemLoadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemLoadImageBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val data = getItem(position)

            PhotoLoadUtils.loadPhoto(binding.imgChat,data)

            binding.imgChat.setOnClickListener {
                AppUtils.disableClickAction(binding.imgChat, 500)
                AppUtils.showMediaNewsFeed(
                    getContext(),
                    getData() as ArrayList<String>,
                    position
                )
            }

        }
    }
}