package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.MessageTypeChatConstants
import com.example.chatapplication.databinding.ItemGroupBinding
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.MediaList
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.example.chatapplication.utils.TimeUtils
import vn.techres.line.app.AppAdapter

class GroupAdapter(context: Context) : AppAdapter<GroupChat>(context) {
    private var onClickGroupChat: GroupChatListener? = null


    override fun getItemId(position: Int): Long {
        return getItem(position).conversationId.hashCode().toLong()
    }

    fun setGroupChatListener(listener: GroupChatListener) {
        this.onClickGroupChat = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {

        val binding = ItemGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemGroupBinding) :
            AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindView(position: Int) {
            val item = getItem(position)

            when (item.type) {
                AppConstants.TYPE_GROUP -> {
                    binding.tvName.text = item.name.trim()
                    PhotoLoadUtils.loadImageAvatarGroupByGlide(
                            binding.imgAvatar,
                            item.avatar
                    )
                    binding.imvOnline.hide()
                    if (item.lastMessage.tag.size > 0) {
                        item.lastMessage.tag.forEach {
                            val tagName = if (it.user.userId == AppConstants.USER_TAG_ALL_ID) {
                                "@All"
                            } else {
                                "@${it.user.fullName}"
                            }
                            item.lastMessage.message = item.lastMessage.message.replace(
                                    it.key, tagName
                            )
                        }
                    }

                    if (item.lastMessage.user.userId == UserCache.getUser().id) {
                        when (item.lastMessage.type) {
                            MessageTypeChatConstants.NEW_GROUP, MessageTypeChatConstants.EMPTY -> {
                                binding.tvContent.text =
                                        getString(R.string.content_empty)
                            }

                            MessageTypeChatConstants.TEXT, MessageTypeChatConstants.REPLY -> {
                                val content = item.lastMessage.message

                                binding.tvContent.text = String.format(
                                        "%s: %s",
                                        getString(R.string.me_message),
                                        content
                                )
                            }

                            MessageTypeChatConstants.IMAGE -> {
                                binding.tvContent.text = String.format(
                                        "%s: %s",
                                        getString(R.string.me_message),
                                        getString(R.string.type_image)
                                )
                            }

                            MessageTypeChatConstants.VIDEO -> {
                                binding.tvContent.text = String.format(
                                        "%s: %s",
                                        getString(R.string.me_message),
                                        getString(R.string.type_video)
                                )
                            }

                            else -> binding.tvContent.text = ""
                        }
                    } else {
                        when (item.lastMessage.type) {
                            MessageTypeChatConstants.NEW_GROUP, MessageTypeChatConstants.EMPTY -> {
                                binding.tvContent.text = getString(R.string.content_empty)
                            }

                            MessageTypeChatConstants.TEXT, MessageTypeChatConstants.REPLY -> {
                                val content = item.lastMessage.message
                                binding.tvContent.text = String.format(
                                        "%s: %s",
                                        item.lastMessage.user.fullName,
                                        content
                                )
                            }

                            MessageTypeChatConstants.IMAGE -> {
                                binding.tvContent.text = String.format(
                                        "%s: %s",
                                        item.lastMessage.user.fullName,
                                        getString(R.string.type_image)
                                )
                            }

                            MessageTypeChatConstants.VIDEO -> {
                                binding.tvContent.text = String.format(
                                        "%s: %s",
                                        item.lastMessage.user.fullName,
                                        getString(R.string.type_video)
                                )
                            }
                            else -> binding.tvContent.text = ""
                        }
                    }
                }

                AppConstants.TYPE_PRIVATE -> {
                    if (item.contactType == AppConstants.FRIEND)
                        binding.imvOnline.show()
                    else
                        binding.imvOnline.hide()

                    if (item.isOnline == 1) {
                        binding.imvOnline.setBackgroundResource(R.drawable.ic_online)
                    } else {
                        binding.imvOnline.setBackgroundResource(R.drawable.ic_off)
                    }
                    if (item.userStatus == AppConstants.STATUS_ACTIVE) {
                        binding.tvName.text = item.name.trim()
                        PhotoLoadUtils.loadImageAvatarByGlide(
                                binding.imgAvatar,
                                item.avatar
                        )
                    } else {
                        binding.tvName.text =
                                getString(R.string.undefine_account)
                        binding.imgAvatar.setImageResource(R.drawable.ic_user_default)
                    }

                    binding.tvContent.text = getString(R.string.content_empty)
                    

                    when (item.lastMessage.type) {
                        MessageTypeChatConstants.EMPTY, MessageTypeChatConstants.NEW_GROUP -> {
                            binding.tvContent.text = getString(R.string.content_empty)
                        }

                        MessageTypeChatConstants.TEXT, MessageTypeChatConstants.REPLY -> {
                            binding.tvContent.text = item.lastMessage.message
                                item.lastMessage.message
                        }

                        MessageTypeChatConstants.IMAGE -> {
                            binding.tvContent.text = getString(R.string.type_image)
                        }

                        MessageTypeChatConstants.VIDEO -> {
                            binding.tvContent.text = getString(R.string.type_video)
                        }

                        else -> binding.tvContent.text = ""
                    }
                }
            }

            if (item.noOfNotSeen == 0) {
                binding.tvCountChat.hide()
                binding.tvContent.setTypeface(null, Typeface.NORMAL)
                binding.tvName.setTypeface(null, Typeface.NORMAL)
                binding.tvContent.isSelected = false
            } else {
                if (item.lastMessage.user.userId == UserCache.getUser().id) {
                    binding.tvCountChat.hide()
                    binding.tvContent.setTypeface(null, Typeface.NORMAL)
                    binding.tvName.setTypeface(null, Typeface.NORMAL)
                    binding.tvContent.isSelected = false
                } else {
                    if (item.noOfNotSeen > 99)
                        binding.tvCountChat.text = "99${getString(R.string.max)}"
                    else
                        binding.tvCountChat.text = item.noOfNotSeen.toString()

                    binding.tvCountChat.hide()
                    binding.tvName.setTypeface(null, Typeface.BOLD)
                    binding.tvContent.setTypeface(null, Typeface.BOLD)
                    binding.tvContent.isSelected = true
                }
            }

            if (item.lastActivity.isNotEmpty() && item.lastActivity != "Invalid date") {
                binding.tvTime.text = TimeUtils.formatTimeUserOnlineStatus2(getContext(), item.lastActivity)
            } else {
                binding.tvTime.text = ""
            }

            if (item.isPinned == 1) {
                binding.imgPin.show()
                binding.lnItemChat.setBackgroundResource(R.drawable.ripple_animation_pin_conversation)
            } else {
                binding.imgPin.hide()
                binding.lnItemChat.setBackgroundResource(R.drawable.ripple_animation_conversation)
            }

            binding.imgAvatar.setOnClickListener {
                onClickGroupChat?.clickAvatar(position, item.avatar)
            }

            binding.lnItemChat.setOnClickListener {
                onClickGroupChat?.clickGroup(item.id)
            }
            binding.lnItemChat.setOnLongClickListener {
                onClickGroupChat?.onLongClickGroup(position, item)
                true
            }
        }
    }

    interface GroupChatListener {
        fun clickGroup(id: String)
        fun onLongClickGroup(position: Int, group: GroupChat)
        fun clickAvatar(position: Int, avatar: String)

    }
}