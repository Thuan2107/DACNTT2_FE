package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.databinding.ItemListImageClipBinding
import com.example.chatapplication.utils.AppUtils
import vn.techres.line.app.AppAdapter

class ImageClipAdapter (context: Context) : AppAdapter<String>(context) {

    private var clickImageClip: ClickImageClip? = null

    fun setClickImageClip(clickImageClip: ClickImageClip) {
        this.clickImageClip = clickImageClip
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListImageClipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemListImageClipBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            AppUtils.resizeImageClip(item, binding.imageCLip)
            binding.ibDelete.setOnClickListener {
                clickImageClip!!.clickDelete(
                    position
                )
            }
            binding.imageCLip.setOnClickListener {
                clickImageClip!!.clickImageClip(
                    position
                )
            }
        }
    }
    interface ClickImageClip {
        fun clickDelete(position: Int)
        fun clickImageClip(position: Int)
    }
}