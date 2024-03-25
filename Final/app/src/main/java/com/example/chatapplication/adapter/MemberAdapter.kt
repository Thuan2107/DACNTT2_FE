package com.example.chatapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.databinding.ItemChooseMemberGroupBinding
import com.example.chatapplication.model.entity.MemberList
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter


class MemberAdapter(context: Context) : AppAdapter<MemberList>(context) {
    private var listener: OnChooseMemberListener? = null

    fun setOnChooseMemberListener(listener: OnChooseMemberListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemChooseMemberGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemChooseMemberGroupBinding) : AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            val item = getItem(position)

            PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatar, item.avatar)
            binding.tvName.text = item.fullName
            binding.tvPermission.text = when (item.permission) {
                ConversationTypeConstants.OWNER -> {
                    getString(R.string.owner)
                }

                ConversationTypeConstants.DEPUTY -> {
                    getString(R.string.deputy)
                }

                else -> {
                    getString(R.string.member)
                }
            }
            binding.rbChoose.setOnCheckedChangeListener(null)
            binding.rbChoose.isChecked = item.isSelected
            binding.rbChoose.setOnCheckedChangeListener { _, isChecked ->
                listener?.onChooseMember(item, isChecked)
            }
            binding.llMember.setOnClickListener {
                binding.rbChoose.isChecked = !binding.rbChoose.isChecked
            }
        }
    }

    interface OnChooseMemberListener {
        fun onChooseMember(member: MemberList, isCheck: Boolean)
    }
}
