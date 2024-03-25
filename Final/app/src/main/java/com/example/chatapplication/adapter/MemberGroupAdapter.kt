package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.chatapplication.R
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.databinding.ItemListUserGroupBinding
import com.example.chatapplication.model.entity.MemberList
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter


class MemberGroupAdapter(context: Context) :
    AppAdapter<MemberList>(context) {
    private var addFriendChat: ContactListener? = null
    private var removeMemberListener: GroupMemberAdapter.RemoveMemberListener? = null

    fun setRemoveMember(removeMemberListener: GroupMemberAdapter.RemoveMemberListener) {
        this.removeMemberListener = removeMemberListener
    }

    fun setContactGroup(addFriendChat: ContactListener) {
        this.addFriendChat = addFriendChat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemListUserGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemListUserGroupBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            binding.tvName.text = item.fullName
            PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatar, item.avatar)
            binding.ivOnline.isSelected = item.isOnline == 1 || item.userId == UserCache.getUser().id

            if (item.contactType == 1) {
                binding.ivAddFiend.show()
                binding.ivWaitingFiend.hide()
            } else {
                binding.ivAddFiend.hide()
                binding.ivWaitingFiend.hide()
            }

            when (item.permission) {
                ConversationTypeConstants.OWNER -> {
                    binding.ivOnline.show()
                    binding.ivKeyMain.show()
                    binding.icKeyDeputy.hide()
                    binding.tvPosition.text = getString(R.string.owner)
                }

                ConversationTypeConstants.DEPUTY -> {
                    binding.ivOnline.show()
                    binding.ivKeyMain.hide()
                    binding.icKeyDeputy.show()
                    binding.tvPosition.text = getString(R.string.deputy)
                }

                else -> {
                    binding.ivOnline.isVisible =
                        item.contactType == AppConstants.FRIEND || item.userId == UserCache.getUser().id
                    binding.ivKeyMain.hide()
                    binding.icKeyDeputy.hide()
                    binding.tvPosition.text = getString(R.string.member)
                }
            }

            binding.llItemChat.setOnClickListener {
                AppUtils.disableClickAction(binding.llItemChat, 1000)
                removeMemberListener!!.onRemoveMember(
                    position,
                    item.userId,
                    item.permission,
                    item
                )
            }

            binding.ivAddFiend.setOnClickListener {
                binding.ivAddFiend.hide()
                binding.ivWaitingFiend.hide()
                AppUtils.disableClickAction(binding.ivAddFiend, 1000)
                addFriendChat!!.onAddFriendChat(
                    item.userId,
                    item.contactType,
                    position,
                )
            }

            binding.ivWaitingFiend.setOnClickListener {
                binding.ivAddFiend.hide()
                binding.ivWaitingFiend.show()
                AppUtils.disableClickAction(binding.ivWaitingFiend, 1000)
                addFriendChat!!.onAddFriendChat(
                    item.userId,
                    item.contactType,
                    position,
                )
            }
        }
    }

    interface ContactListener {
        fun onAddFriendChat(idUser: String, contactType: Int, position: Int)
    }
}
