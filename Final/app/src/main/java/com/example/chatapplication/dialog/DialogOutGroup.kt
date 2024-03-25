package com.example.chatapplication.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.databinding.DialogOutGroupBinding

class DialogOutGroup {
    @SuppressLint("SuspiciousIndentation")
    class Builder (
        context: Context,
        title : String,
        content: String,
        confirm: String,
    ) : CommonDialog.Builder<Builder>(context), BaseAdapter.OnItemClickListener {
        private var listener: OnListener? = null
        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        private var binding: DialogOutGroupBinding =
            DialogOutGroupBinding.inflate(
                LayoutInflater.from(context)
            )

        init {
            setCustomView(binding.root)
            setActionView(false)
            setTitleView(false)
            setCancelable(true)

            setAnimStyle(AnimAction.ANIM_TOAST)
            binding.tvTitle.text = title
            binding.tvContent.text = content
            binding.tvOutGroup.text = confirm

            binding.tvOutGroupCancel.setOnClickListener {
                autoDismiss()
            }
            binding.tvOutGroup.setOnClickListener {
                listener!!.onPolicyConfirm(
                    0
                )
                autoDismiss()
            }

        }

        override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
            //
        }


    }

    interface OnListener {
        fun onPolicyConfirm(
            next: Int,
        )
    }
}
