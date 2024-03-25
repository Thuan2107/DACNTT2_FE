package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.chatapplication.R
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.constant.MessageTypeChatConstants
import com.example.chatapplication.databinding.ItemMessageImageLeftBinding
import com.example.chatapplication.databinding.ItemMessageImageRightBinding
import com.example.chatapplication.databinding.ItemMessageNotificationBinding
import com.example.chatapplication.databinding.ItemMessageTextLeftBinding
import com.example.chatapplication.databinding.ItemMessageTextRightBinding
import com.example.chatapplication.databinding.ItemMessageVideoLeftBinding
import com.example.chatapplication.databinding.ItemMessageVideoRightBinding
import com.example.chatapplication.holder.TextLeftHandle
import com.example.chatapplication.holder.TextRightHandle
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.Message
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.invisible
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.TimeUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import vn.techres.line.app.AppAdapter
import com.example.chatapplication.model.entity.MessageView
import com.example.chatapplication.other.CustomLayoutManager
import com.example.chatapplication.utils.PhotoLoadUtils
import com.example.chatapplication.holder.ImageLeftHandle
import com.example.chatapplication.holder.ImageRightHandle
import com.example.chatapplication.model.entity.Conversation
import com.example.chatapplication.utils.AppUtils
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Objects


class MessageAdapter(
    context: Context,
    var groupChat: GroupChat
) : AppAdapter<Message>(context) {

    private var onYoutubePlayer: OnYoutubePlayer? = null
    private var chatHandle: ChatHandle? = null

    fun setChatHandle(chatHandle: ChatHandle) {
        this.chatHandle = chatHandle
    }
    fun setOnYoutubePlayer(onYoutubePlayer: OnYoutubePlayer) {
        this.onYoutubePlayer = onYoutubePlayer
    }

    fun setDataGroup(groupChat: GroupChat) {
        this.groupChat = groupChat
    }

    companion object {

        const val TEXT_RIGHT = 1
        const val TEXT_LEFT = 2

        const val IMAGE_RIGHT = 3
        const val IMAGE_LEFT = 4

        const val VIDEO_RIGHT = 5
        const val VIDEO_LEFT = 6

        const val REPLY_RIGHT = 7
        const val REPLY_LEFT = 8

        const val REVOKE_RIGHT = 15
        const val REVOKE_LEFT = 16

        const val OUT_GROUP = 26


    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)

        return if (checkSenderMessage(message)) {
            when (message.type) {
                MessageTypeChatConstants.TEXT -> TEXT_RIGHT //tin nhắn bên phải

                MessageTypeChatConstants.IMAGE -> IMAGE_RIGHT // tin nhắn hình bên phải

                MessageTypeChatConstants.VIDEO -> VIDEO_RIGHT // tin nhắn video bên phải

                MessageTypeChatConstants.REPLY -> REPLY_RIGHT // tin nhắn reply bên phải

                MessageTypeChatConstants.USER_OUT_GROUP -> OUT_GROUP


                else -> 100
            }
        } else {
            when (message.type) {
                MessageTypeChatConstants.TEXT -> TEXT_LEFT // tin nhắn text bên trái

                MessageTypeChatConstants.IMAGE -> IMAGE_LEFT // tin nhắn hình bên trái

                MessageTypeChatConstants.VIDEO -> VIDEO_LEFT // tin nhắn video bên trái

                MessageTypeChatConstants.REPLY -> REPLY_LEFT // tin nhắn reply bên trái

                MessageTypeChatConstants.USER_OUT_GROUP -> OUT_GROUP


                else -> 100
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return when (viewType) {
            TEXT_RIGHT -> TextRightHolder(
                ItemMessageTextRightBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            TEXT_LEFT -> TextLeftHolder(
                ItemMessageTextLeftBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            IMAGE_RIGHT -> ImageRightHolder(
                ItemMessageImageRightBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            IMAGE_LEFT -> ImageLeftHolder(
                ItemMessageImageLeftBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
//
//            VIDEO_RIGHT -> VideoRightHolder(
//                ItemMessageVideoRightBinding.inflate(
//                    LayoutInflater.from(
//                        parent.context
//                    ), parent, false
//                )
//            )
//
//            VIDEO_LEFT -> VideoLeftHolder(
//                ItemMessageVideoLeftBinding.inflate(
//                    LayoutInflater.from(
//                        parent.context
//                    ), parent, false
//                )
//            )

            else -> TextRightHolder(
                ItemMessageTextRightBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    inner class TextRightHolder(private val binding: ItemMessageTextRightBinding) :
        AppViewHolder(binding.root) {

        override fun onBindView(position: Int) {
            val data = getItem(position)
            binding.message.tvMessage.text = data.message
            checkTimeMessages(
                data.isTimeline,
                data.createdAt,
                binding.time.llTimeHeader,
                binding.time.tvTimeHeader,
                binding.message.tvTime,
                position
            )

            checkUserViewMessage(
                binding.rcvUserView,
                binding.send.llSendMessage,
                data,
                position,
                binding.llView,
                binding.llUserView,
                binding.tvMoreView
            )

            handleStatusMessage(binding.send.tvTextUserView,data)

            setMarginStart(binding.root, binding.time.llTimeHeader, position)


            val lp = binding.ctlText.layoutParams as ViewGroup.MarginLayoutParams

            lp.setMargins(
                0,
                0,
                0,
                getContext().resources.getDimension(R.dimen.dp_4)
                    .toInt()
            )


            binding.ctlText.setOnLongClickListener {
                chatHandle!!.onRevoke(
                    data,
                    binding.ctlMessage,
                    binding.ctlMessage.y.toInt(),
                    binding.message.tvMessage
                )
                true
            }
        }
    }

    inner class TextLeftHolder(private val binding: ItemMessageTextLeftBinding) :
        AppViewHolder(binding.root) {

        override fun onBindView(position: Int) {
            val data = getItem(position)
            binding.message.tvMessage.text = data.message

            checkTimeMessagesTheir(
            data.isTimeline,
            data.createdAt,
            binding.time.llTimeHeader,
            binding.time.tvTimeHeader,
            binding.message.tvTime,
            binding.tvNameMedia,
            binding.ivAvatar,
            position
        )

        setMarginStart(binding.root, binding.time.llTimeHeader, position)

        val lp = binding.ctlText.layoutParams as ViewGroup.MarginLayoutParams

        lp.setMargins(
            0,
            0,
            0,
            getContext().resources.getDimension(R.dimen.dp_4).toInt()
        )

        setProfilePersonChat(binding.tvNameMedia, binding.ivAvatar, data)

        val screen = IntArray(2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.root.getLocationInSurface(screen)
        }

        binding.ctlText.setOnLongClickListener {
            chatHandle!!.onRevoke(
                data,
                binding.ctlMessage,
                binding.ctlMessage.y.toInt(),
                binding.message.tvMessage
            )
            true
        }
        }
    }

    inner class ImageLeftHolder(private val binding: ItemMessageImageLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            val data = getItem(position)
            checkTimeMessagesTheir(
                data.isTimeline,
                data.createdAt,
                binding.time.llTimeHeader,
                binding.time.tvTimeHeader,
                binding.tvTime,
                binding.tvNameMedia,
                binding.ivAvatar,
                position
            )

            setMarginStart(binding.root, binding.time.llTimeHeader, position)

            setProfilePersonChat(binding.tvNameMedia, binding.ivAvatar, data)

            val screen = IntArray(2)
            binding.ctlContainer.getLocationOnScreen(screen)

            binding.ivOneOne.setOnLongClickListener {
                chatHandle!!.onRevoke(data, binding.root, screen[1], null)
                true
            }
            val lp = binding.rltVideo.layoutParams as ViewGroup.MarginLayoutParams
            if (binding.tvTime.visibility == View.GONE) {

                lp.setMargins(
                    0,
                    0,
                    0,
                    getContext().resources.getDimension(R.dimen.dp_4)
                        .toInt()
                )
            } else {
                lp.setMargins(
                    0,
                    0,
                    0,
                    getContext().resources.getDimension(R.dimen.dp_20).toInt()
                )
            }

            if (data.media.size == 1) {
                binding.ctlImageOne.show()
                binding.llImageFourMore.hide()

                PhotoLoadUtils.resizeImageClip(
                    data.media[0],
                    binding.ivOneOne,
                )
                binding.ivOneOne.setOnClickListener {
                    AppUtils.disableClickAction(binding.ivOneOne, 500)
                    AppUtils.showMediaNewsFeed(getContext(), data.media, position)
                }
            } else {
                binding.ctlImageOne.hide()
                binding.llImageFourMore.show()
                setAdapterImageVideo(
                    binding.rcvImgMore,
                    data,
                    screen[1],
                    isAllowLongClick = true
                )
            }
        }
    }

    inner class ImageRightHolder(private val binding: ItemMessageImageRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {
            val data = getItem(position)
            checkTimeMessages(
                data.isTimeline,
                data.createdAt,
                binding.time.llTimeHeader,
                binding.time.tvTimeHeader,
                binding.tvTime,
                position
            )

            checkUserViewMessage(
                binding.rcvUserView,
                binding.send.llSendMessage,
                data,
                position,
                binding.llView,
                binding.llUserView,
                binding.tvMoreView
            )

            handleStatusMessage(binding.send.tvTextUserView, data)

            setMarginStart(binding.root, binding.time.llTimeHeader, position)

            val lp = binding.ctlVideo.layoutParams as ViewGroup.MarginLayoutParams
            if (binding.tvTime.visibility == View.GONE) {
                lp.setMargins(
                    0,
                    0,
                    0,
                    getResources().getDimension(R.dimen.dp_4)
                        .toInt()
                )
            } else {
                lp.setMargins(
                    0,
                    0,
                    0,
                    getResources().getDimension(R.dimen.dp_20)
                        .toInt()
                )
            }

            val screen = IntArray(2)
            binding.ctlContainer.getLocationOnScreen(screen)

            binding.ivOneOne.setOnLongClickListener {
                chatHandle!!.onRevoke(data, binding.root, screen[1], null)
                true
            }

            if (data.media.size == 1) {
                binding.lnImageOne.show()
                binding.llImageFourMore.hide()

                PhotoLoadUtils.resizeImageClip(
                    data.media[0].ifEmpty { data.media[0] },
                    binding.ivOneOne,
                )

//                binding.ivOneOne.setOnClickListener {
//                    AppUtils.disableClickAction(binding.ivOneOne, 500)
//                    AppUtils.showMediaNewsFeed(getContext(), data.media, 0)
//                }
            } else {
                binding.lnImageOne.hide()
                binding.llImageFourMore.show()
                setAdapterImageVideo(
                    binding.rcvImgMore,
                    data,
                    screen[1],
                    isAllowLongClick = true
                )
            }

            binding.root.setOnLongClickListener {
                chatHandle!!.onRevoke(data, binding.root, screen[1], null)
                true
            }
        }
    }

    inner class VideoRightHolder(private val binding: ItemMessageVideoRightBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

        }
    }

    inner class VideoLeftHolder(private val binding: ItemMessageVideoLeftBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

        }
    }

    inner class OutGroupHolder(private val binding: ItemMessageNotificationBinding) :
        AppViewHolder(binding.root) {
        override fun onBindView(position: Int) {

        }
    }

    fun setAdapterImageVideo(
        recyclerView: RecyclerView, item: Message, y: Int, isAllowLongClick: Boolean
    ) {
        val adapter = LoadImageAdapter(recyclerView.context, item, y, isAllowLongClick)
        adapter.setData(item.media)
        val layoutManager = FlexboxLayoutManager(recyclerView.context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.CENTER
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    fun checkSenderMessage(message: Message): Boolean {
        return message.user.userId == UserCache.getUser().id
    }

    fun checkTimeMessages(
        toDay: Int,
        strDate: String,
        lnTimeHeader: LinearLayout,
        timeHeader: TextView,
        timeMessage: TextView,
        position: Int
    ) {
        if (toDay == 1) {
            lnTimeHeader.show()
            timeHeader.text = TimeUtils.getToDayMessage(strDate)
            timeMessage.hide()
        } else {
            lnTimeHeader.hide()
            timeMessage.show()
        }
        timeMessage.text = TimeUtils.getTimeMessage(strDate)
        if (position != 0 && position != getData().size - 1) {
            if (checkTimeOld(strDate, position)) {
                if (getData()[position].user.userId == getData()[position - 1].user.userId && getData()[position - 1].type != (MessageTypeChatConstants.UPDATE_NAME) && getData()[position - 1].type != (MessageTypeChatConstants.UPDATE_AVATAR) && getData()[position - 1].type != (MessageTypeChatConstants.UPDATE_BACKGROUND) && getData()[position - 1].type != (MessageTypeChatConstants.REMOVE_USER) && getData()[position - 1].type != (MessageTypeChatConstants.ADD_NEW_USER) && getData()[position - 1].type != (MessageTypeChatConstants.PINNED) && getData()[position - 1].type != (MessageTypeChatConstants.USER_OUT_GROUP)) {
                    timeMessage.hide()
                } else timeMessage.show()
            } else timeMessage.show()
        } else {
            timeMessage.show()
        }
    }

    fun checkTimeMessagesTheir(
        toDay: Int,
        strDate: String,
        lnTimeHeader: LinearLayout,
        timeHeader: TextView,
        timeMessage: TextView,
        name: View,
        avatar: View,
        position: Int
    ) {
        if (toDay == 1) {
            lnTimeHeader.show()
            timeHeader.text = TimeUtils.getToDayMessage(strDate)
        } else {
            lnTimeHeader.hide()
        }
        timeMessage.text = TimeUtils.getTimeMessage(strDate)
        if (position != 0 && position < getData().size - 1 && getData().size > 1) {
            handleMessageInMiddle(strDate, timeMessage, name, avatar, position)
        } else {
            if (position == 0) {//Đầu mảng => là tin nhắn cuối cùng
                handleLastMessage(strDate, timeMessage, name, avatar)
            } else {//Cuối mảng => là tin nhắn đầu tiên
                handleFirstMessage(strDate, timeMessage, name, avatar, position)
            }
        }
    }



    private fun handleLastMessage(
        strDate: String,
        timeMessage: TextView,
        name: View,
        avatar: View
    ) {
        if (//Tin nhắn cuối cùng thời gian với tin nhắn trước và đều là của 1 người nhắn
            getData().size > 1 &&
            checkTopMessageSender(0) &&
            checkTimeNew(strDate, 0) &&
            checkTopMessageIsValid(0)
        ) {
            name.hide()
        } else {
            if (groupChat.type == AppConstants.TYPE_PRIVATE) {
                name.hide()
            } else {
                name.show()
            }
        }
        avatar.show()
        timeMessage.show()
    }

    private fun handleFirstMessage(
        strDate: String,
        timeMessage: TextView,
        name: View,
        avatar: View,
        position: Int
    ) {
        if (groupChat.type == AppConstants.TYPE_PRIVATE) {
            name.hide()
        } else {
            name.show()
        }
        if (//Tin nhắn đầu tiên và tin nhắn này là của cùng 1 người và cùng thời gian
            checkBottomMessageSender(position) &&
            checkBottomMessageIsValid(position) &&
            checkTimeOld(strDate, position)
        ) {
            avatar.invisible()
            timeMessage.hide()
        } else {
            avatar.show()
            timeMessage.show()
        }
    }

    private fun handleMessageInMiddle(
        strDate: String,
        timeMessage: TextView,
        name: View,
        avatar: View,
        position: Int
    ) {
        if (
            checkTopAndBottomMessageIsTheSameSender(position) &&
            checkTopMessageIsValid(position) &&
            checkBottomMessageIsValid(position)
        ) {//Tin nhắn trước và sau của tin nhắn này đều là của 1 người
            handleAboutTimeMiddleMessage(strDate, timeMessage, name, avatar, position)
        } else if (
            checkBottomMessageIsTheSameSender(position) &&
            checkBottomMessageIsValid(position) &&
            checkTimeOld(strDate, position)
        ) {//Tin nhắn trước là của người khác, tin nhắn sau và tin nhắn này là của 1 người và cùng thời gian
            if (groupChat.type == AppConstants.TYPE_PRIVATE) {
                name.hide()
            } else {
                name.show()
            }
            avatar.invisible()
            timeMessage.hide()
        } else if (
            checkTopMessageIsTheSameSender(position) &&
            checkTopMessageIsValid(position) &&
            checkTimeNew(strDate, position)
        ) {//Tin nhắn sau là của người khác, tin nhắn trước và tin nhắn này là của 1 người và cùng thời gian
            name.hide()
            avatar.show()
            timeMessage.show()
        } else {
            if (groupChat.type == AppConstants.TYPE_PRIVATE) {
                name.hide()
            } else {
                name.show()
            }
            avatar.show()
            timeMessage.show()
        }
    }

    private fun handleAboutTimeMiddleMessage(
        strDate: String,
        timeMessage: TextView,
        name: View,
        avatar: View,
        position: Int
    ) {
        if (checkTimeOld(strDate, position) && checkTimeNew(strDate, position)) {
            //Tin nhắn trước và tin nhắn sau cùng thời gian với tin nhắn này
            name.hide()
            avatar.invisible()
            timeMessage.hide()
        } else if (checkTimeOld(strDate, position) && !checkTimeNew(strDate, position)) {
            //Chỉ có tin nhắn sau là cùng thời gian với tin nhắn này
            if (groupChat.type == AppConstants.TYPE_PRIVATE) {
                name.hide()
            } else {
                name.show()
            }
            avatar.invisible()
            timeMessage.hide()
        } else if (!checkTimeOld(strDate, position) && checkTimeNew(strDate, position)) {
            //Chỉ có tin nhắn trước là cùng thời gian với tin nhắn này
            name.hide()
            avatar.show()
            timeMessage.show()
        } else {
            //Tin nhắn trước và tin nhắn sau đều KHÔNG cùng thời gian với tin nhắn này
            if (groupChat.type == AppConstants.TYPE_PRIVATE) {
                name.hide()
            } else {
                name.show()
            }
            avatar.show()
            timeMessage.show()
        }
    }

    private fun checkBottomMessageSender(position: Int): Boolean {
        return getData()[position].user.userId == getData()[position - 1].user.userId
    }

    private fun checkTopMessageSender(position: Int): Boolean {
        return getData()[position].user.userId == getData()[position + 1].user.userId
    }

    private fun checkTopAndBottomMessageIsTheSameSender(position: Int): Boolean {//Tin nhắn trên và dưới là của cùng 1 người nhắn
        return checkBottomMessageSender(position) && checkTopMessageSender(position)
    }

    private fun checkTopMessageIsTheSameSender(position: Int): Boolean {//Chỉ có tin nhắn phía trên là cùng 1 người nhắn
        return !checkBottomMessageSender(position) && checkTopMessageSender(position)
    }

    private fun checkBottomMessageIsTheSameSender(position: Int): Boolean {//Chỉ có tin nhắn phía dưới là cùng 1 người nhắn
        return checkBottomMessageSender(position) && !checkTopMessageSender(position)
    }

    private fun checkTopMessageIsValid(position: Int): Boolean {
        return getData()[position + 1].type != (MessageTypeChatConstants.UPDATE_NAME) &&
                getData()[position + 1].type != (MessageTypeChatConstants.UPDATE_AVATAR) &&
                getData()[position + 1].type != (MessageTypeChatConstants.UPDATE_BACKGROUND) &&
                getData()[position + 1].type != (MessageTypeChatConstants.REMOVE_USER) &&
                getData()[position + 1].type != (MessageTypeChatConstants.ADD_NEW_USER) &&
                getData()[position + 1].type != (MessageTypeChatConstants.PINNED) &&
                getData()[position + 1].type != (MessageTypeChatConstants.USER_OUT_GROUP)
    }

    private fun checkBottomMessageIsValid(position: Int): Boolean {
        return getData()[position - 1].type != (MessageTypeChatConstants.UPDATE_NAME) &&
                getData()[position - 1].type != (MessageTypeChatConstants.UPDATE_AVATAR) &&
                getData()[position - 1].type != (MessageTypeChatConstants.UPDATE_BACKGROUND) &&
                getData()[position - 1].type != (MessageTypeChatConstants.REMOVE_USER) &&
                getData()[position - 1].type != (MessageTypeChatConstants.ADD_NEW_USER) &&
                getData()[position - 1].type != (MessageTypeChatConstants.PINNED) &&
                getData()[position - 1].type != (MessageTypeChatConstants.USER_OUT_GROUP)
    }

    private fun checkTimeOld(strDate: String, position: Int): Boolean {
        if (position + 1 == getData().size) {
            return false
        }
        @SuppressLint("SimpleDateFormat") val format =
            SimpleDateFormat(ChatConstants.DATE_TIME_MINUS_FORMAT)
        return try {
            format.parse(TimeUtils.changeFormatTimeMessageChat(strDate)) ==
                    format.parse(TimeUtils.changeFormatTimeMessageChat(getData()[position - 1].createdAt))
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
    }

    private fun checkTimeNew(strDate: String, position: Int): Boolean {
        if (position + 1 == getData().size) {
            return false
        }
        @SuppressLint("SimpleDateFormat") val format =
            SimpleDateFormat(ChatConstants.DATE_TIME_MINUS_FORMAT)
        return try {
            format.parse(TimeUtils.changeFormatTimeMessageChat(strDate)) ==
                    format.parse(TimeUtils.changeFormatTimeMessageChat(getData()[position + 1].createdAt))
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
    }

    fun handleStatusMessage(view: TextView, message: Message) {
        if (message.isErrorMessage) {
            view.text = AppApplication.applicationContext().getString(R.string.error)
            view.setTextColor(getColor(R.color.red))
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_message, 0, 0, 0)
        } else {
            if (message.isServerResponse) {
                view.text = AppApplication.applicationContext().getString(R.string.received)
                view.setTextColor(getColor(R.color.white))
                view.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_bubble_chat_received,
                    0,
                    0,
                    0
                )
            } else {
                view.text = AppApplication.applicationContext().getString(R.string.sending)
                view.setTextColor(getColor(R.color.white))
                view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sending_message, 0, 0, 0)
            }
        }
    }

    fun setMarginStart(view: View, time: View, position: Int) {
        val lp = view.layoutParams as ViewGroup.MarginLayoutParams
        if (position == getData().size - 1 && time.visibility == View.GONE) {
            lp.setMargins(
                0,
                getContext().resources.getDimension(R.dimen.dp_8).toInt(),
                0,
                0
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapterUserViewMessage(
        recyclerView: RecyclerView, messageViewList: ArrayList<MessageView>
    ) {
        recyclerView.layoutManager =
            CustomLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
        Objects.requireNonNull(recyclerView.itemAnimator)!!.changeDuration = 0
        (Objects.requireNonNull(recyclerView.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations =
            false
        recyclerView.isNestedScrollingEnabled = false

        val userView = ArrayList<MessageView>()
        val userViewMessageAdapter = UserViewMessageAdapter(getContext())
        userViewMessageAdapter.setData(userView)
        recyclerView.adapter = userViewMessageAdapter

        if (messageViewList.size > 7) {
            userView.clear()
            userView.addAll(
                messageViewList.subList(
                    messageViewList.size - 7, messageViewList.size
                )
            )
            userViewMessageAdapter.notifyDataSetChanged()
        } else {
            userView.clear()
            userView.addAll(messageViewList)
            userViewMessageAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("SetTextI18n")
    fun checkUserViewMessage(
        rclUserView: RecyclerView,
        lnSendMessage: LinearLayout,
        item: Message,
        position: Int,
        lnView: LinearLayout,
        lnUserView: LinearLayout,
        tvMoreView: TextView
    ) {
        if (item.messageView.size != 0) {
            if (position == 0) {
                lnSendMessage.hide()
                lnView.show()
                rclUserView.show()
                lnUserView.hide()

                setAdapterUserViewMessage(
                    rclUserView, item.messageView
                )
                if (item.messageView.size > 7) {
                    lnUserView.show()
                    tvMoreView.text = "+" + (item.messageView.size - 7)
                } else {
                    lnUserView.hide()
                }
            }
        } else {
            if (position == 0) {
                lnSendMessage.show()
                rclUserView.hide()
                lnView.show()
                lnUserView.hide()
            } else {
                if (item.isErrorMessage || !item.isServerResponse) {
                    lnSendMessage.show()
                    lnView.show()
                } else {
                    lnSendMessage.hide()
                    lnView.hide()
                }
                rclUserView.hide()
                lnUserView.hide()
            }
        }
    }

    fun setProfilePersonChat(tvNameUserChat: TextView, ivAvatarUserChat: ImageView, data: Message) {
        if (groupChat.type == AppConstants.TYPE_PRIVATE && groupChat.userStatus != AppConstants.STATUS_ACTIVE) {
            tvNameUserChat.text = getString(R.string.undefine_account)
            ivAvatarUserChat.setImageResource(R.drawable.ic_user_default)
        } else {
            tvNameUserChat.text = data.user.fullName
            PhotoLoadUtils.loadImageAvatarByGlide(
                ivAvatarUserChat, data.user.avatar
            )
        }
    }

    interface OnYoutubePlayer {
        fun onYoutubePlayer(
            youTubePlayer: YouTubePlayer?,
            youTubePlayerView: YouTubePlayerView?
        )
    }

    interface ChatHandle {
        fun scrollToReplyMessage(message: Message)
        fun onRevoke(messagesByGroup: Message, view: View, y: Int, textView: TextView?)

    }
}