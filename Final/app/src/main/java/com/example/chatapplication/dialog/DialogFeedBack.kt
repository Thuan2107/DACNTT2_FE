package com.example.chatapplication.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.databinding.DialogFeedbackFriendBinding

class DialogFeedBack {
    class Builder(
        context: Context
    ) : CommonDialog.Builder<Builder>(context), BaseAdapter.OnItemClickListener {
        private var listener: ClickAddFriend? = null

        private val binding: DialogFeedbackFriendBinding = DialogFeedbackFriendBinding.inflate(
            LayoutInflater.from(context)
        )

        fun setListener(listener: ClickAddFriend): Builder = apply {
            this@Builder.listener = listener
        }

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            setContentView(binding.root)
            binding.llConfirmAddFriend.setOnClickListener {
                listener!!.onClickAddFriend(1)
                autoDismiss()
            }
            binding.llCancelAddFriend.setOnClickListener {
                listener!!.onClickAddFriend(2)
                autoDismiss()
            }
        }

        override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
            //Empty
        }
    }

    interface ClickAddFriend {
        fun onClickAddFriend(type: Int)
    }
}