package com.example.chatapplication.adapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.ItemContactBinding
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import vn.techres.line.app.AppAdapter


class ContactAdapter constructor(context: Context) : AppAdapter<Friend>(context) {
    private var myFriendInterFace: MyFriendInterface? = null
    private var onClickMore: MyFriendInterface? = null
    private var onClickWall: MyFriendInterface? = null
    private var onClickMessage: MyFriendInterface? = null
    private var clickAddFriend: AddFriendInterface? = null
    private var clickRecallFriend: AddFriendInterface? = null
    private var clickFeedBack: AddFriendInterface? = null

    fun setMyFriendInterFace(myFriendInterFace: MyFriendInterface) {
        this.myFriendInterFace = myFriendInterFace
    }

    fun setOnClickMore(onClickMore: MyFriendInterface) {
        this.onClickMore = onClickMore
        this.onClickWall = onClickMore
        this.onClickMessage = onClickMore
    }

    fun setOnClickAddFriend(clickAddFriend: AddFriendInterface) {
        this.clickAddFriend = clickAddFriend
        this.clickRecallFriend = clickAddFriend
        this.clickFeedBack = clickAddFriend

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemContactBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            binding.txtFullName.text = item.fullName.ifEmpty { item.phone }

            PhotoLoadUtils.loadImageAvatarByGlide(binding.imgAvatar, item.avatar)

            when (item.contactType) {
                AppConstants.ITS_ME -> {
                    binding.ivAddFriend.hide()
                    binding.ivMore.hide()
                    binding.ivMessage.hide()
                    binding.ivRecall.hide()
                    binding.ivFeedBack.hide()
                }

                AppConstants.FRIEND -> {
                    binding.ivAddFriend.hide()
                    binding.ivRecall.hide()
//                    binding.ivMessage.show()
                    binding.ivMore.show()
                    binding.ivFeedBack.hide()
                }

                AppConstants.WAITING_RESPONSE -> {
                    binding.ivAddFriend.hide()
                    binding.ivRecall.show()
//                    binding.ivMessage.show()
                    binding.ivMore.hide()
                    binding.ivFeedBack.hide()
                }

                AppConstants.NOT_FRIEND -> {
                    binding.ivAddFriend.show()
                    binding.ivRecall.hide()
//                    binding.ivMessage.show()
                    binding.ivMore.hide()
                    binding.ivFeedBack.hide()
                }

                else -> {
                    binding.ivAddFriend.hide()
                    binding.ivRecall.hide()
//                    binding.ivMessage.show()
                    binding.ivMore.hide()
                    binding.ivFeedBack.show()
                }
            }

            //Action
            binding.ivMore.setOnClickListener {
                onClickMore!!.clickMore(item, position)
            }

            binding.ivAddFriend.setOnClickListener {
                clickAddFriend!!.clickAddFriend(item.userId, position)
                binding.ivAddFriend.hide()
                binding.ivRecall.show()
            }

//            binding.ivRecall.setOnClickListener {
//                clickRecallFriend!!.clickRecall(item.userId.toInt(), position)
//                binding.ivAddFriend.show()
//                binding.ivRecall.hide()
//            }

            binding.ivFeedBack.setOnClickListener {
                clickFeedBack!!.clickFeedBack(item.userId, position)
            }

            binding.ivMessage.setOnClickListener {
                onClickMessage!!.clickMessage(position)
            }

            itemView.setOnClickListener {
                onClickWall!!.clickWall(position)
            }

            binding.rltAvatar.setOnClickListener {
                onClickWall!!.clickAvatar(position)
            }
        }
    }

    interface MyFriendInterface {
        fun clickMore(item: Friend, position: Int)
        fun clickWall(position: Int)
        fun clickMessage(position: Int)
        fun clickAvatar(position: Int)
    }

    interface AddFriendInterface {
        fun clickAddFriend(id: String, position: Int)
//        fun clickRecall(id: Int, position: Int)
        fun clickFeedBack(id: String, position: Int)
    }

}

