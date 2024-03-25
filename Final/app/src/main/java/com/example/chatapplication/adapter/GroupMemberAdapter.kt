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
import com.example.chatapplication.databinding.ItemMemberBinding
import com.example.chatapplication.model.entity.MemberList
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter


class GroupMemberAdapter(context: Context) : AppAdapter<MemberList>(context) {
    private var managerMemberListener: ManagerMemberListener? = null
    private var addFriendChat: MemberGroupAdapter.ContactListener? = null
    private var removeMemberListener: RemoveMemberListener? = null

    fun setUserListener(managerMemberListener: ManagerMemberListener) {
        this.managerMemberListener = managerMemberListener
    }

    fun setRemoveMember(removeMemberListener: RemoveMemberListener) {
        this.removeMemberListener = removeMemberListener
    }

    fun setContactGroup(addFriendChat: MemberGroupAdapter.ContactListener) {
        this.addFriendChat = addFriendChat
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemMemberBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            if (item.contactType == 1) {
                binding.ivAddFiend.show()
            } else {
                binding.ivAddFiend.hide()
            }

            PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatar, item.avatar)
            binding.tvName.text = item.fullName
            binding.ivOnline.isSelected = item.isOnline == 1 || item.userId == UserCache.getUser().id

            when (item.permission) {
                ConversationTypeConstants.OWNER -> {
                    binding.ivOnline.show()
                    binding.ivKeyMain.show()
                    binding.icKeyDeputy.hide()
                    binding.tvPermission.text = getString(R.string.owner)
                }

                ConversationTypeConstants.DEPUTY -> {
                    binding.ivOnline.show()
                    binding.ivKeyMain.hide()
                    binding.icKeyDeputy.show()
                    binding.tvPermission.text = getString(R.string.deputy)
                }

                else -> {
                    binding.ivOnline.isVisible =
                        item.contactType == AppConstants.FRIEND || item.userId == UserCache.getUser().id
                    binding.ivKeyMain.hide()
                    binding.icKeyDeputy.hide()
                    binding.tvPermission.text = getString(R.string.member)
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
                AppUtils.disableClickAction(binding.ivAddFiend, 1000)
                addFriendChat!!.onAddFriendChat(
                    item.userId,
                    item.contactType,
                    position,
                )
            }
        }
    }

    interface RemoveMemberListener {
        fun onRemoveMember(position: Int, idUser: String, permission: Int, data: MemberList)
    }

    interface ManagerMemberListener {
        fun clickMember(permission: Int, String: Int, position: Int, data: MemberList)
//        fun clickMemberGroup(position: Int, idUser: Int, permission: Int, data: MemberList)

    }
}