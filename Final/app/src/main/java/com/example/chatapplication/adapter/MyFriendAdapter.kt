package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.content.res.AppCompatResources
import com.example.chatapplication.R
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.ItemUserCreateGroupChatBinding
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.example.chatapplication.utils.TimeUtils
import vn.techres.line.app.AppAdapter

class MyFriendAdapter(context: Context, type: Int) : AppAdapter<Friend>(context) {

    private var clickChooseItem: ChooseItemListener? = null
    private var checkType = type

    fun setClickChooseItem(clickChooseItem: ChooseItemListener) {
        this.clickChooseItem = clickChooseItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemUserCreateGroupChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)

    }

    inner class ViewHolder(private val binding: ItemUserCreateGroupChatBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            setIsRecyclable(false)

            val checkChangeListener: CompoundButton.OnCheckedChangeListener =
                CompoundButton.OnCheckedChangeListener { _, isChecked ->
                    clickChooseItem!!.clickChooseItem(
                        position,
                        isChecked,
                        item
                    )
                }

            binding.ivOnline.hide()

            if (checkType == 1) {
                binding.tvName.text = item.fullName
                PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatar, item.avatar)

                binding.tvTime.text = TimeUtils.formatDatetimeAgo(getContext(), item.lastActivity)

                binding.cbChooseUser.setOnCheckedChangeListener(null)
                if (item.isMember == 1) {
                    binding.cbChooseUser.isChecked = true
                    binding.cbChooseUser.isEnabled = false
                    binding.cbChooseUser.buttonDrawable = AppCompatResources.getDrawable(
                        getContext(),
                        R.drawable.button_disable_orange
                    )
                } else {
                    binding.cbChooseUser.isEnabled = true
                    binding.cbChooseUser.buttonDrawable = AppCompatResources.getDrawable(
                        getContext(),
                        R.drawable.radio_button_background_orange
                    )
                    itemView.setOnClickListener {
                        binding.cbChooseUser.setOnCheckedChangeListener(null)
                        binding.cbChooseUser.isChecked = !binding.cbChooseUser.isChecked
                        clickChooseItem!!.clickChooseItem(
                            position,
                            binding.cbChooseUser.isChecked,
                            item
                        )
                        binding.cbChooseUser.setOnCheckedChangeListener(checkChangeListener)
                    }
                    binding.cbChooseUser.setOnCheckedChangeListener(checkChangeListener)
                }
            } else {
                if (item.permission == 0) {
                    binding.tvTime.text = getString(R.string.member)
                }
                binding.tvName.text = item.fullName
                PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatar, item.avatar)

                binding.tvTime.text = TimeUtils.formatDatetimeAgo(getContext(), item.lastActivity)

                binding.cbChooseUser.isChecked = item.isMember != 0

                itemView.setOnClickListener {
                    binding.cbChooseUser.setOnCheckedChangeListener(null)
                    binding.cbChooseUser.isChecked = !binding.cbChooseUser.isChecked
                    clickChooseItem!!.clickChooseItem(
                        position,
                        binding.cbChooseUser.isChecked,
                        item
                    )
                    binding.cbChooseUser.setOnCheckedChangeListener(checkChangeListener)
                }
                binding.cbChooseUser.setOnCheckedChangeListener(checkChangeListener)
            }

            if (item.contactType == AppConstants.FRIEND)
                binding.ivOnline.show()
            else
                binding.ivOnline.hide()
        }
    }

    interface ChooseItemListener {
        fun clickChooseItem(position: Int, isChecked: Boolean, item: Friend)
    }
}