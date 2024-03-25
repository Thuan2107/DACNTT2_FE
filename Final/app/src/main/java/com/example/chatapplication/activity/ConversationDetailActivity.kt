package com.example.chatapplication.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.chatapplication.R
import com.example.chatapplication.adapter.ImageClipAdapter
import com.example.chatapplication.adapter.MessageAdapter
import com.example.chatapplication.adapter.TypingOnAdapter
import com.example.chatapplication.api.DetailGroupApi
import com.example.chatapplication.api.GetMessageApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.constant.MediaConstants
import com.example.chatapplication.constant.MessageTypeChatConstants
import com.example.chatapplication.constant.SocketChatConstants
import com.example.chatapplication.databinding.ActivityChatBinding
import com.example.chatapplication.databinding.ItemChatViewBinding
import com.example.chatapplication.databinding.ItemChatsBinding
import com.example.chatapplication.eventbus.EvenBusConversation
import com.example.chatapplication.eventbus.EventBusLastMessage
import com.example.chatapplication.fragment.GroupFragment
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.JoinAndLeaveRoom
import com.example.chatapplication.model.entity.LastMessage
import com.example.chatapplication.model.entity.MediaList
import com.example.chatapplication.model.entity.MemberList
import com.example.chatapplication.model.entity.Message
import com.example.chatapplication.model.entity.MessageEmit
import com.example.chatapplication.model.entity.MyItemDecoration
import com.example.chatapplication.model.entity.Sender
import com.example.chatapplication.model.entity.Typing
import com.example.chatapplication.model.entity.TypingOff
import com.example.chatapplication.model.entity.TypingOn
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.invisible
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.ChatUtils
import com.example.chatapplication.utils.FileTypeUtils
import com.example.chatapplication.utils.PhotoLoadUtils
import com.example.chatapplication.utils.PhotoPickerUtils
import com.example.chatapplication.utils.TimeUtils
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import io.socket.client.Socket
import io.socket.emitter.Emitter
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

import java.io.File
import java.io.IOException
import java.util.UUID


class ConversationDetailActivity : AppActivity(), MessageAdapter.ChatHandle, MessageAdapter.OnYoutubePlayer{

    private lateinit var headerBinding: ActivityChatBinding
    private lateinit var contentBinding: ItemChatsBinding
    private lateinit var actionBinding: ItemChatViewBinding

    private var group: GroupChat = GroupChat()
    private var messageList: ArrayList<Message> = ArrayList()
    private var messageAdapter: MessageAdapter? = null
    private var membersList: ArrayList<MemberList> = ArrayList()
    private var dataMemberListTemp: ArrayList<MemberList> = ArrayList()
    private var userTypingAdapter: TypingOnAdapter? = null
    private var listTypingUser = ArrayList<Typing>()
    private var imageClip: ArrayList<String> = ArrayList()
    private var imageClipAdapter: ImageClipAdapter? = null
    private var aBoolean = false
    private var nCurrent = -1
    private var isReplyMessage = false
    private var messageReply: MessageEmit = MessageEmit()
    private var alTagged = ArrayList<MemberList>()
    private var isTying = false
    private var isLink = false
    private var contentPaste = ""
    private var scrollOption = ChatConstants.SCROLL_TO_BOTTOM
    private var checkPosition = false
    private var loading = false
    private var currentPosition = 0
    private var limit = 20
    private var isLoadMoreTop = true
    private var isLoadMoreBottom = true
    private var positionPlayer = -1
    private var mediaPlayer: MediaPlayer? = null
    private var youTubePlayerView: YouTubePlayerView? = null
    private var initializedYouTubePlayer: YouTubePlayer? = null
    private var idbackground = ""
    private var imageBackground = ""
    private var currentKeyUpload = ChatConstants.EMIT_UPLOAD

    private var currentAudioPlay = MediaPlayer()

    private var messageSend: MessageEmit = MessageEmit()

    private var connectivityManager: ConnectivityManager? = null

    private var mediaImage = ArrayList<String>()



    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            //Join-room chat
            if (!AppApplication.socketChat!!.connected()) {
                AppApplication.socketChat!!.connect()
            } else {
                ChatUtils.emitSocket(
                        SocketChatConstants.JOIN_ROOM,
                        JoinAndLeaveRoom(group.conversationId)
                )
            }
        }

        override fun onLost(network: Network) {

        }

        override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
        ) {

        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {

        }
    }

    override fun getLayoutView(): View {
        headerBinding = ActivityChatBinding.inflate(layoutInflater)
        contentBinding = ItemChatsBinding.inflate(layoutInflater)
        actionBinding = ItemChatViewBinding.inflate(layoutInflater)
        return headerBinding.getRoot()
    }

    override fun initView() {

        //Lấy data group
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(ChatConstants.GROUP_DATA)) {
                group = Gson().fromJson(
                        bundleIntent.getString(ChatConstants.GROUP_DATA),
                        GroupChat::class.java
                )
                Log.d("data group", Gson().toJson(group))
            }
        }

        connectivityManager = getSystemService(ConnectivityManager::class.java)
        //Khoi tao layout
        headerBinding.layoutMain.addView(actionBinding.root)
        actionBinding.layoutContainer.addView(contentBinding.root)

        //Setup header
        ImmersionBar.setTitleBar(this, headerBinding.rltHeader)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun initData() {
        //Set dữ liệu cho chi tiết group trước rồi gọi detail group để cập nhật sau
        setInfoDataForGroup()

        //Đăng ký các listen của socketChat
        registrySocket()

        //Đăng ký lắng nghe network
        connectivityManager?.registerDefaultNetworkCallback(networkCallback)

        //Khởi tạo messageAdapter
        initMessageAdapter()

        //Khởi tạo userTypingAdapter
        initUserTypingAdapter()

        //Khởi tạo changeBackgroundAdapter
//        initChangeBackgroundAdapter()



        //Phân trang danh sách tin nhắn
//        paginateUpDownScroll()

        //Lấy danh sách tin nhắn chat
        getMessage("", isScrollToBottom = true, scrollOption)

        initAction()

//        //cancel notification khi join room
//        try {
//            AppApplication.notificationManager?.cancel(group.conversationId.toInt())
//            NotificationCache.removeNotify(group.conversationId)
//        } catch (e: Exception) {
//            //Empty
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(
                R.anim.right_in_activity,
                R.anim.right_out_activity
        )

    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        getDetailGroup(group.id, jumpToGroup = false)
//        if (checkPermissionsFile()) {
////            getImageClip()
//        }
//        getMemberList()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
                R.anim.left_in_activity,
               R.anim.left_out_activity
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            //Leave-room chat
            ChatUtils.emitSocket(
                    SocketChatConstants.LEAVE_ROOM,
                    JoinAndLeaveRoom(group.conversationId)
            )
            //Hủy các mediaPlayer
            if (mediaPlayer != null) {
                mediaPlayer!!.release()
            }
            //Hủy youtubePlayer
            if (youTubePlayerView != null) {
                youTubePlayerView!!.release()
            }
            //Hủy các audio
            if (positionPlayer != -1) {
                messageAdapter?.notifyItemChanged(positionPlayer)
            }
            currentAudioPlay.release()

            //Hủy đăng ký socket
            unRegistrySocket()
            //Hủy đăng ký lắng nghe network
            connectivityManager?.unregisterNetworkCallback(networkCallback)
            //Sau khi rời khỏi màn hình chat thì update group chat lại là không còn tin nhắn chưa đọc
//            EventBus.getDefault().post(UpdateNoOfNotSeenEventBus(group.conversationId))
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }




    //--------------------------------------Đăng ký và hủy Socket---------------------------------//

    @SuppressLint("NewApi")
    private fun registrySocket() {
        //Listen Socket Connect Success
        AppApplication.socketChat?.on(Socket.EVENT_CONNECT, onSocketConnectSuccess)

        //Listen Socket Error
        AppApplication.socketChat?.on(SocketChatConstants.ON_SOCKET_ERROR, onSocketError)
        //Chat Message
        AppApplication.socketChat?.on(SocketChatConstants.ON_MESSAGE, onMessage)
        //Typing
//        AppApplication.socketChat?.on(SocketChatConstants.ON_TYPING_ON, onTypingOn)
        AppApplication.socketChat?.on(SocketChatConstants.ON_TYPING_OFF, onTypingOff)
        //Vote
    }

    private fun unRegistrySocket() {
        AppApplication.socketChat?.off(Socket.EVENT_CONNECT, onSocketConnectSuccess)
        //Listen Socket Error
        AppApplication.socketChat?.off(SocketChatConstants.ON_SOCKET_ERROR, onSocketError)
        //Chat Message
        AppApplication.socketChat?.off(SocketChatConstants.ON_MESSAGE, onMessage)
        //Typing
//        AppApplication.socketChat?.off(SocketChatConstants.ON_TYPING_ON, onTypingOn)
        AppApplication.socketChat?.off(SocketChatConstants.ON_TYPING_OFF, onTypingOff)


    }

    //--------------------------------------------------------------------------------------------//

    //--------------------------------Khởi tạo Kohii và các Adapter-------------------------------//


    private fun initMessageAdapter() {
        messageAdapter?.setChatHandle(this)
        messageAdapter = MessageAdapter(this, group)
        messageAdapter?.setData(messageList)
        AppUtils.initRecyclerViewVerticalReverse(contentBinding.rcvChat, messageAdapter)
    }


    private fun initUserTypingAdapter() {
        userTypingAdapter = TypingOnAdapter(this)
        userTypingAdapter?.setData(listTypingUser)
        contentBinding.rcvTypingOn.addItemDecoration(MyItemDecoration(-4)) // here set decoration in recyclerview
        contentBinding.rcvTypingOn.layoutManager = LinearLayoutManager(
                getContext(), RecyclerView.HORIZONTAL, false
        )
        contentBinding.rcvTypingOn.adapter = userTypingAdapter
    }

    private fun paginateUpDownScroll() {
        contentBinding.rcvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (scrollOption != ChatConstants.SCROLL_TO_REPLY) {
                    if (!contentBinding.rcvChat.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {//scroll to bottom
                        if (!loading && isLoadMoreBottom) {
                            loading = true
                            scrollOption = ChatConstants.SCROLL_TO_BOTTOM
                            if (messageList.isNotEmpty()) {
                                getMessage(
                                        messageList.first().position,
                                        isScrollToBottom = false,
                                        scrollOption
                                )
                            }
                        }
                    } else if (!contentBinding.rcvChat.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {//scroll to top
                        if (!loading && isLoadMoreTop) {
                            loading = true
                            scrollOption = ChatConstants.SCROLL_TO_TOP
                            if (messageList.isNotEmpty()) {
                                getMessage(
                                        messageList.last().position,
                                        isScrollToBottom = false,
                                        scrollOption
                                )
                            }
                        }
                    }
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { // RecyclerView has finished scrolling
                    contentBinding.scrollMessage.setOnClickListener {
                        contentBinding.scrollMessage.isEnabled = false
                        messageList.clear()
                        messageAdapter?.notifyDataSetChanged()
                        scrollOption = ChatConstants.SCROLL_TO_BOTTOM
//                        getMessage("", isScrollToBottom = true, scrollOption)
                    }
//                    contentBinding.lnNewMessage.setOnClickListener {
//                        contentBinding.lnNewMessage.isEnabled = false
//                        messageList.clear()
//                        messageAdapter?.notifyDataSetChanged()
//                        scrollOption = ChatConstants.SCROLL_TO_BOTTOM
//                        getMessage("", isScrollToBottom = true, scrollOption)
//                    }
                    //Lấy vị trí hiện tại của recyclerview
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    currentPosition = layoutManager.findFirstVisibleItemPosition()
//                    scrollStateChangedRecyclerView(currentPosition)
                } else {
                    contentBinding.scrollMessage.setOnClickListener(null)
//                    contentBinding.lnNewMessage.setOnClickListener(null)
                }
            }
        })
    }


    private fun getMessage(position: String, isScrollToBottom: Boolean, arrow: Int) {
        EasyHttp.get(this).api(GetMessageApi.params(group.conversationId, position, limit, arrow))
                .request(object : HttpCallbackProxy<HttpData<ArrayList<Message>>>(this) {
                    override fun onHttpStart(call: Call?) {
                        //Empty
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onHttpSuccess(data: HttpData<ArrayList<Message>>) {
                        if (data.isRequestSucceed()) {
                            loading = false
                            data.getData()?.let {
                                val result = it
                                when (arrow) {
                                    ChatConstants.SCROLL_TO_TOP -> {
                                        //Khi load tin nhắn cũ thì biến tất cả vote có isLastVote = 1 mà đã có trong danh sách tin nhắn hiện tại về 0
                                        isLoadMoreTop = result.size >= limit
                                    }

                                    ChatConstants.SCROLL_TO_BOTTOM -> {
                                        //Khi load tin nhắn mới thì biến những vote cũ mà có isLastVote = 1 và có xuất hiện trong danh sách tin nhắn vừa load thêm thành 0

                                        isLoadMoreBottom =
                                                if (isScrollToBottom) false else result.size >= limit
                                    }

                                    else -> {
                                        isLoadMoreBottom = true
                                        isLoadMoreTop = true
                                    }
                                }
                                if (result.size > 0) {
                                    when (arrow) {
                                        ChatConstants.SCROLL_TO_REPLY -> {
                                            messageList.clear()
                                            messageList.addAll(result)
                                            messageAdapter?.notifyDataSetChanged()
                                        }

                                        ChatConstants.SCROLL_TO_BOTTOM -> {
                                            messageList.addAll(0, result)
                                            messageAdapter?.notifyItemRangeInserted(0, result.size)
                                            messageAdapter?.notifyItemChanged(result.size)
                                        }

                                        else -> {
                                            val currentListCount = messageList.size
                                            messageList.addAll(result)
                                            messageAdapter?.notifyItemRangeInserted(
                                                    currentListCount,
                                                    result.size
                                            )
                                            if (currentListCount != 0) {
                                                messageAdapter?.notifyItemChanged(currentListCount - 1)
                                            }
                                        }
                                    }
                                    if (arrow == ChatConstants.SCROLL_TO_REPLY) {
                                        contentBinding.rcvChat.post {
                                            val positionRep =
                                                    messageList.indexOfFirst { x -> x.position == position }
                                            if (positionRep != -1) {
                                                contentBinding.rcvChat.smoothScrollToPosition(
                                                        positionRep
                                                )
                                                messageList[positionRep].highlight = 1
                                                messageAdapter?.notifyItemChanged(positionRep)
                                            }
                                            scrollOption = ChatConstants.SCROLL_TO_BOTTOM
                                        }
                                    }
                                }
                                if (isScrollToBottom) {
                                    contentBinding.rcvChat.post {
                                        contentBinding.rcvChat.smoothScrollToPosition(0)
                                    }
                                }
                            }
                        } else {
                            Timber.e(
                                    "${
                                        AppApplication.applicationContext()
                                                .getString(R.string.error_message)
                                    } ${data.getMessage()}"
                            )
                        }
                    }

                    override fun onHttpFail(throwable: Throwable?) {

                        Timber.e(
                                "${
                                    AppApplication.applicationContext()
                                            .getString(R.string.error_message)
                                } ${throwable}"
                        )
                    }
                })
    }


    //textWatcher của input nhập tin nhắn
    private val textMessageWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            actionBinding.inputChat.edtChat.removeTextChangedListener(this)
//            typingOn(group)
//            postDelayed({
//                typingOff(group)
//            }, 3000)

            if (s.isEmpty()) {
                actionBinding.inputChat.ibSend.hide()
                actionBinding.inputChat.ibMore.show()
                actionBinding.inputChat.ivGallery.show()
            } else {
                actionBinding.inputChat.ibSend.show()
                actionBinding.inputChat.ibMore.hide()
                actionBinding.inputChat.ivGallery.hide()
                //Lấy dữ liệu từ clipboard
                contentPaste = ""
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard.primaryClip != null) {
                    val primaryClipData = clipboard.primaryClip
                    if (primaryClipData != null) {
                        val item = primaryClipData.getItemAt(0)
                        if (item.text != null && item.text != "") {
                            contentPaste = item.text.toString()
                        }
                    }
                }

            }
            actionBinding.inputChat.edtChat.addTextChangedListener(this)
        }

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
        ) {
            val s = charSequence.toString()
            if (s != "") {
                actionBinding.inputChat.tvCountCharacter.show()
                if (s.length >= 10000) {
                    actionBinding.inputChat.edtChat.removeTextChangedListener(this)
                    actionBinding.inputChat.tvCountCharacter.setTextColor(getColor(R.color.red))
                    actionBinding.inputChat.edtChat.setText(s.substring(0, 10000))
                    actionBinding.inputChat.edtChat.setSelection(actionBinding.inputChat.edtChat.text!!.length)
                    actionBinding.inputChat.edtChat.addTextChangedListener(this)
                } else {
                    actionBinding.inputChat.tvCountCharacter.setTextColor(getColor(R.color.common_text_color))
                }
                actionBinding.inputChat.tvCountCharacter.text =
                        "${actionBinding.inputChat.edtChat.text!!.length}/10000"
            } else {
                actionBinding.inputChat.tvCountCharacter.invisible()
            }
        }
    }

    private fun typingOn(group: GroupChat) {
        val typingOn = TypingOn()
        typingOn.conversationId = group.conversationId
        try {
            if (!isTying) {
                isTying = true
                ChatUtils.emitSocket(SocketChatConstants.EMIT_TYPING_ON, typingOn)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun typingOff(group: GroupChat) {
        val typingOff = TypingOff()
        typingOff.conversationId = group.conversationId
        try {
            isTying = false
            ChatUtils.emitSocket(SocketChatConstants.EMIT_TYPING_OFF, typingOff)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }




    //--------------------------------------------------------------------------------------------//

    //------------------------------------Handle SocketChat---------------------------------------//
    private val onSocketConnectSuccess = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("ON SOCKET CONNECT SUCCESS%s",args[0].toString())
                ChatUtils.emitSocket(
                        SocketChatConstants.JOIN_ROOM,
                        JoinAndLeaveRoom(group.conversationId)
                )
            }
        }.start()
    }

    private val onSocketError = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.e("ON SOCKET ERROR%s", args[0].toString())
            }
        }.start()
    }


    private val onMessage = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Log.d("ON MESSAGE SOCKET %s", args[0].toString())
                val jsonObject = args[0] as JSONObject
                val messageJson = jsonObject.getJSONObject("message")
                val message = Gson().fromJson(
                    messageJson.toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    eventHandleNewMessage(message)
                }
            }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val onTypingOn = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("ON MESSAGE TYPING_ON %s", args.first().toString())
                val typingOn = Gson().fromJson(
                    args.first().toString(), Typing::class.java
                )
                typingOn
                group
                if (typingOn.conversationId == group.conversationId) {
                    listTypingUser.removeIf { x -> x.user.userId == typingOn.userId }
                    if (typingOn.userId != UserCache.getUser().id) {
                        listTypingUser.add(0, typingOn)
                        userTypingAdapter?.setData(listTypingUser)
                        userTypingAdapter?.notifyDataSetChanged()
                    }
                    if (listTypingUser.isNotEmpty()) {
                        contentBinding.lnTyping.show()
                    } else {
                        contentBinding.lnTyping.hide()
                    }
                }
            }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val onTypingOff = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("ON MESSAGE TYPING_OFF %s", args.first().toString())
                val typingOff = Gson().fromJson(
                    args.first().toString(), Typing::class.java
                )
                if (typingOff.conversationId == group.conversationId) {
                    listTypingUser.removeIf { x -> x.user.userId == typingOff.user.userId }
                    userTypingAdapter?.setData(listTypingUser)
                    userTypingAdapter?.notifyDataSetChanged()
                    if (listTypingUser.isEmpty()) {
                        contentBinding.lnTyping.hide()
                    } else {
                        contentBinding.lnTyping.show()
                    }
                }
            }
        }.start()
    }

    private fun eventHandleNewMessage(message: Message) {
        message.isServerResponse = true//Server phản hồi tin nhắn chat
        message.isErrorMessage = false

        if (message.user.userId == UserCache.getUser().id) {
            val indexFindItem = messageList.indexOfFirst { it.messageId == message.keyError }
            if (indexFindItem != -1) {//Các tin nhắn từ bản thân nhắn
                val media =
                        messageList[indexFindItem].media//Backup ra vì cần link local để load lên khi upload chưa lên kịp
                messageList[indexFindItem] = message
                messageList[indexFindItem].media = media
                messageAdapter?.notifyItemChanged(indexFindItem)
            } else {//Dành cho các tin nhắn của hệ thống
                messageList.add(0, message)
                messageAdapter?.notifyItemInserted(0)
                messageAdapter?.notifyItemChanged(1)
            }
        } else {
            //Xử lý dữ liệu để cập nhật ở màn hình bên ngoài (GroupFragment)
            handleLastMessage(message)
//            contentBinding.scrollMessage.hide()
//            if (currentPosition != 0) {
//                isLoadMoreBottom = true
//            } else {
                messageList.add(0, message)
                messageAdapter?.notifyItemInserted(0)
                messageAdapter?.notifyItemChanged(1)
                contentBinding.rcvChat.post {
                    contentBinding.rcvChat.smoothScrollToPosition(0)
                }
//            }
        }
    }

    private fun handleLastMessage(message: Message) {
        val groupChat = GroupChat()
        with(groupChat) {
            this.conversationId = group.conversationId
            this.name = group.name
            this.avatar = group.avatar
            this.isOnline = 1//Tin nhắn mới thì hẳn là đang on
            this.position = group.position
            this.type = group.type
            this.noOfNotSeen = 0
            this.noOfMember = group.noOfMember
            this.isNotify = group.isNotify
            this.isPinned = group.isPinned
            this.lastActivity = message.createdAt
            this.myPermission = group.myPermission
            this.userBlockType = group.userBlockType
            this.userStatus = group.userStatus
            val lastMessages = LastMessage()
            with(lastMessages) {
                this.messageId = message.messageId
                this.message = message.message
                this.type = message.type
                this.user = message.user
                this.userTarget = message.userTarget
            }
            this.lastMessage = lastMessages
        }
        EventBus.getDefault().post(EventBusLastMessage(groupChat))
    }



//    private fun renderDataPinToView() {
//        countMessagePinned = arrayPinned.size
//        if (arrayPinned.size == 0) {
//            contentBinding.pinned.llPinned.invisible()
//        } else {
//            contentBinding.pinned.llPinned.show()
//            when (arrayPinned.first().type) {
//                MessageTypeChatConstants.IMAGE -> {
//                    contentBinding.pinned.tvTitle.text = getString(vn.techres.line.R.string.type_image)
//                    PhotoLoadUtils.loadImageByGlide(
//                            contentBinding.pinned.ivIconPinned,
//                            arrayPinned.first().media.first().original.url
//                    )
//                }
//
//                MessageTypeChatConstants.STICKER -> {
//                    contentBinding.pinned.tvTitle.text =
//                            getString(vn.techres.line.R.string.type_sticker)
//                    PhotoLoadUtils.loadImageByGlide(
//                            contentBinding.pinned.ivIconPinned,
//                            arrayPinned.first().sticker.original.url
//                    )
//                }
//
//                MessageTypeChatConstants.VIDEO -> {
//                    contentBinding.pinned.tvTitle.text =
//                            getString(vn.techres.line.R.string.type_video)
//                    PhotoLoadUtils.loadImageByGlide(
//                            contentBinding.pinned.ivIconPinned,
//                            arrayPinned.first().media.first().thumb.url
//                    )
//                }
//
//                MessageTypeChatConstants.FILE -> {
//                    contentBinding.pinned.ivIconPinned.setImageDrawable(
//                            AppCompatResources.getDrawable(
//                                    this, R.drawable.ic_pinned_file
//                            )
//                    )
//                    contentBinding.pinned.tvTitle.text =
//                            "${getString(vn.techres.line.R.string.type_file)} ${arrayPinned.first().media.first().original.name}"
//                    contentBinding.pinned.ivThumbImage.invisible()
//                }
//
//                MessageTypeChatConstants.AUDIO -> {
//                    contentBinding.pinned.ivIconPinned.setImageDrawable(
//                            AppCompatResources.getDrawable(
//                                    this, R.drawable.ic_messenger_pinned
//                            )
//                    )
//                    contentBinding.pinned.tvTitle.text = getString(vn.techres.line.R.string.type_audio)
//                    contentBinding.pinned.ivThumbImage.invisible()
//                }
//
//                MessageTypeChatConstants.PINNED_VOTE -> {
//                    contentBinding.pinned.ivIconPinned.setImageDrawable(
//                            AppCompatResources.getDrawable(
//                                    this, R.drawable.ic_pinned_vote_border
//                            )
//                    )
//                    contentBinding.pinned.tvTitle.text =
//                            arrayPinned.first().message
//                    contentBinding.pinned.ivThumbImage.invisible()
//                }
//
//                else -> {
//                    contentBinding.pinned.ivThumbImage.invisible()
//                    if (arrayPinned.first().thumb.typeSystem == AppConstants.TYPE_SYSTEM_LINK_JOIN) {
//                        contentBinding.pinned.tvTitle.text =
//                                "${getString(vn.techres.line.R.string.type_link)} ${ConfigCache.getConfig().apiJoinGroup}${arrayPinned.first().thumb.objectId}"
//                        PhotoLoadUtils.loadImageLinkJoinGroupByGlide(
//                                contentBinding.pinned.ivIconPinned,
//                                arrayPinned.first().thumb.logo
//                        )
//                    } else {
//                        if (arrayPinned.first().thumb.url.isNotEmpty() && arrayPinned.first().thumb.url == arrayPinned.first().message) {
//                            contentBinding.pinned.tvTitle.text =
//                                    "${getString(vn.techres.line.R.string.type_link)} ${arrayPinned.first().thumb.title}"
//                            PhotoLoadUtils.loadThumbnail(
//                                    contentBinding.pinned.ivIconPinned,
//                                    arrayPinned.first().thumb.logo
//                            )
//                        } else {
//                            contentBinding.pinned.tvTitle.text =
//                                    ChatUtils.formatTagNameNotHighLight(
//                                            arrayPinned.first().message,
//                                            arrayPinned.first().tag
//                                    )
//                            contentBinding.pinned.ivIconPinned.setImageDrawable(
//                                    AppCompatResources.getDrawable(
//                                            this,
//                                            R.drawable.ic_messenger_pinned
//                                    )
//                            )
//                        }
//                    }
//                }
//            }
//            contentBinding.pinned.tvSubtitle.text = "${
//                if (arrayPinned.first().type == MessageTypeChatConstants.PINNED_VOTE)
//                    getString(R.string.create_by)
//                else getString(R.string.message_of)
//            } ${arrayPinned.first().user.fullName}"
//            if (countMessagePinned == 1) {
//                contentBinding.pinned.tvPinnedMore.hide()
//                contentBinding.pinned.ivPinnedOne.show()
//            } else {
//                contentBinding.pinned.ivPinnedOne.hide()
//                contentBinding.pinned.tvPinnedMore.show()
//                contentBinding.pinned.tvPinnedMore.text = "+${countMessagePinned - 1}"
//            }
//
//            if (contentBinding.headerBackground.llBackgroundView.isVisible) {
//                contentBinding.pinned.llPinned.hide()
//            } else {
//                contentBinding.pinned.llPinned.show()
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val onAddMember = Emitter.Listener { args: Array<Any> ->
        Thread {
            Timber.d("ADD MEMBER %s", args.first().toString())
            runOnUiThread {
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    group.noOfMember += message.userTarget.size
                    val userTargetList = ArrayList<MemberList>()
                    for (item in message.userTarget) {
                        userTargetList.add(
                                MemberList(
                                        item.userId,
                                        item.fullName,
                                        item.avatar,
                                        ConversationTypeConstants.PERMISSION_MEMBER.toInt()
                                )
                        )
                    }
                    dataMemberListTemp.addAll(userTargetList)
//                    userTagAdapter?.notifyDataSetChanged()
                    headerBinding.header.tvStatus.text = TimeUtils.formatTimeUserOnlineStatus(this, group.lastActivity)
                    eventHandleNewMessage(message)
                }
            }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val onRemoveMember = Emitter.Listener { args: Array<Any> ->
        Thread {
            Timber.d("REMOVE MEMBER %s", args.first().toString())
            runOnUiThread {
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    group.noOfMember -= message.userTarget.size
                    val userTargetIdList = message.userTarget.map { it.userId }
                    membersList.removeAll { it.userId in userTargetIdList }
                    dataMemberListTemp.removeAll { it.userId in userTargetIdList }
//                    userTagAdapter?.notifyDataSetChanged()
                    headerBinding.header.tvStatus.text = String.format(
                            getString(R.string.number_group_chat_content), group.noOfMember
                    )
                    eventHandleNewMessage(message)
                }
            }
        }.start()
    }



    //socket nhận type
    private val onChangeNotification = Emitter.Listener { args: Array<Any> ->
        Thread {
            Timber.d("ON NOTIFICATION %s", args.first().toString())
            runOnUiThread {
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    eventHandleNewMessage(message)
                }
            }
        }.start()
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private val onTypingOn = Emitter.Listener { args: Array<Any> ->
//        Thread {
//            runOnUiThread {
//                Timber.d("ON MESSAGE TYPING_ON %s", args.first().toString())
//                val typingOn = Gson().fromJson(
//                        args.first().toString(), Typing::class.java
//                )
//                if (typingOn.conversationId == group.conversationId) {
//                    listTypingUser.removeIf { x -> x.user.userId == typingOn.user.userId }
//                    if (typingOn.user.userId != UserCache.getUser().id) {
//                        listTypingUser.add(0, typingOn)
//                        userTypingAdapter?.setData(listTypingUser)
//                        userTypingAdapter?.notifyDataSetChanged()
//                    }
//                    if (listTypingUser.isNotEmpty()) {
//                        contentBinding.lnTyping.show()
//                    } else {
//                        contentBinding.lnTyping.hide()
//                    }
//                }
//            }
//        }.start()
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    private val onTypingOff = Emitter.Listener { args: Array<Any> ->
//        Thread {
//            runOnUiThread {
//                Timber.d("ON MESSAGE TYPING_OFF %s", args.first().toString())
//                val typingOff = Gson().fromJson(
//                        args.first().toString(), Typing::class.java
//                )
//                if (typingOff.conversationId == group.conversationId) {
//                    listTypingUser.removeIf { x -> x.user.userId == typingOff.user.userId }
//                    userTypingAdapter?.setData(listTypingUser)
//                    userTypingAdapter?.notifyDataSetChanged()
//                    if (listTypingUser.isEmpty()) {
//                        contentBinding.lnTyping.hide()
//                    } else {
//                        contentBinding.lnTyping.show()
//                    }
//                }
//            }
//        }.start()
//    }

//    @SuppressLint("NotifyDataSetChanged")

    private val onRevoke = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("ON REVOKE SOCKET: %s", args.first().toString())
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    //Xử lý dữ liệu để cập nhật ở màn hình bên ngoài (GroupFragment)
                    handleLastMessage(message)

                    val foundIndex = messageList.indexOfFirst { it.messageId == message.messageId }
                    if (foundIndex != -1) {
                        messageList[foundIndex].type = message.type
                        messageAdapter?.notifyItemChanged(foundIndex)
                    }
                }
            }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val onChangeGroupName = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("ON_CHANGE_GROUP_NAME: %s", args.first().toString())
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    group.name = message.conversation.name
                    headerBinding.header.tvGroupName.text = group.name
                    messageList.add(0, message)
                    messageAdapter?.notifyItemChanged(0)

                    //Xử lý dữ liệu để cập nhật ở màn hình bên ngoài (GroupFragment)
                    handleLastMessage(message)
                }
            }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val onChangeGroupAvatar = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("ON_CHANGE_GROUP_AVATAR: %s", args.first().toString())
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    group.avatar = message.conversation.avatar
                    PhotoLoadUtils.loadImageAvatarGroupByGlide(
                            headerBinding.header.imgAvatar,
                            group.avatar
                    )
                    messageList.add(0, message)
                    messageAdapter?.notifyItemChanged(0)

                    //Xử lý dữ liệu để cập nhật ở màn hình bên ngoài (GroupFragment)
                    handleLastMessage(message)
                }
            }
        }.start()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val onChangeBackground = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("ON_CHANGE_BACKGROUND: %s", args.first().toString())
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    group.background = message.conversation.background
                    PhotoLoadUtils.loadImageBackgroundByGlide(
                            headerBinding.ivBackground,
                            group.background
                    )
                    messageList.add(0, message)
                    messageAdapter?.notifyItemChanged(0)

                    //Xử lý dữ liệu để cập nhật ở màn hình bên ngoài (GroupFragment)
                    handleLastMessage(message)
                }
            }
        }.start()
    }

    private val onDisbandGroup = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("Socket DisbandGroup %s", Gson().toJson(args[0]))
                val message = Gson().fromJson(
                        args.first().toString(), Message::class.java
                )
                if (message.conversation.conversationId == group.conversationId) {
                    startActivity(HomeActivity::class.java)
                }
            }
        }.start()
    }

    //--------------------------------------------------------------------------------------------//

    /**
     * api lấy chi tiết cuộc trò chuyện
     */
    private fun getDetailGroup(idGroup: String, jumpToGroup: Boolean) {
        EasyHttp.get(this).api(DetailGroupApi.params(idGroup))
                .request(object : HttpCallbackProxy<HttpData<GroupChat>>(this) {
                    override fun onHttpStart(call: Call?) {
                        //empty
                    }

                    override fun onHttpEnd(call: Call?) {
                        //empty
                    }

                    override fun onHttpFail(throwable: Throwable?) {
                        Timber.e(
                                "${
                                    AppApplication.applicationContext()
                                            .getString(R.string.error_message)
                                } ${throwable}"
                        )
                    }

                    override fun onHttpSuccess(data: HttpData<GroupChat>) {
                        if (jumpToGroup) {
                            try {
                                finish()
                                val groupData = data.getData()
                                val bundle = Bundle()
                                val intent = Intent(
                                    this@ConversationDetailActivity,
                                    ConversationDetailActivity::class.java
                                )
                                bundle.putString(
                                    AppConstants.GROUP_DATA, Gson().toJson(groupData)
                                )
                                intent.putExtras(bundle)
                                startActivity(intent)
                            } catch (e: Exception) {
                                Timber.e(e.message)
                            }
                        } else {
                            data.getData()?.let {
                                group = it
                                messageAdapter?.setDataGroup(group)
                                setInfoDataForGroup()
                            }
                        }

                    }
                })
    }


    //Set Data cho group chat
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setInfoDataForGroup() {
        if (group.type == AppConstants.TYPE_GROUP) {//Group
            headerBinding.header.tvGroupName.text = group.name
            PhotoLoadUtils.loadImageAvatarGroupByGlide(
                    headerBinding.header.imgAvatar,
                    group.avatar
            )
            headerBinding.header.tvStatus.text =
                TimeUtils.formatTimeUserOnlineStatus(this, group.lastActivity)
            headerBinding.header.tvStatus.show()
            headerBinding.header.imvOnline.hide()
            headerBinding.header.ibAddMember.show()
        } else {//Cá nhân
            headerBinding.header.tvGroupName.text = group.name
            headerBinding.header.tvStatus.text =
                TimeUtils.formatTimeUserOnlineStatus(this, group.lastActivity)
            PhotoLoadUtils.loadImageAvatarByGlide(
                    headerBinding.header.imgAvatar,
                    group.avatar
            )
        }
        PhotoLoadUtils.loadImageBackgroundByGlide(
                headerBinding.ivBackground,
                imageBackground.ifEmpty { group.background }
        )
        headerBinding.header.imgAvatar.setOnClickListener {
            AppUtils.showMediaAvatar(this, group.avatar, 0)
        }

    }

    //Lấy danh sách hình ảnh vừa mới chụp
//    @SuppressLint("NotifyDataSetChanged")
//    private fun getImageClip() {
//        val mediaProjection = arrayOf(
//                MediaStore.MediaColumns._ID,
//                MediaStore.MediaColumns.DATA,
//                MediaStore.MediaColumns.DATE_ADDED,
//                MediaStore.MediaColumns.MIME_TYPE
//        )
//
//        val currentTimeMillis = System.currentTimeMillis()
//        val oneMinuteAgo = currentTimeMillis - (60 * 1000)
//
//        val selection =
//                "${MediaStore.MediaColumns.DATE_ADDED} >= ? AND ${MediaStore.MediaColumns.DATE_ADDED} <= ?"
//        val selectionArgs =
//                arrayOf((oneMinuteAgo / 1000).toString(), (currentTimeMillis / 1000).toString())
//
//        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
//
//        val queryUri = MediaStore.Files.getContentUri("external")
//        val cursor = contentResolver.query(
//                queryUri,
//                mediaProjection,
//                selection,
//                selectionArgs,
//                sortOrder
//        )
//        imageClip.clear()
//        images.stringList.clear()
//        cursor?.use {
//            val dataColumnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
//            val mimeTypeColumnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
//
//            while (it.moveToNext()) {
//                val path = it.getString(dataColumnIndex)
//                val mimeType = it.getString(mimeTypeColumnIndex)
//
//                // Kiểm tra nếu là hình ảnh
//                if (PictureMimeType.isHasImage(mimeType)) {
//                    images.stringList.add(path)
//                    if (!ImageClipDetectCache.getImageDetect().stringList.contains(path)) {
//                        imageClip.add(path)
//                    }
//                }
//            }
//        }
//        images.time = Date().time.toInt() / 1000 / 60
//        ImageClipDetectCache.saveDataImageDetect(images)
//        if (imageClip.isNotEmpty() && !isLink) {
//            contentBinding.rlnImageClip.show()
//            setImageClip()
//            imageClipAdapter?.notifyDataSetChanged()
//        } else {
//            contentBinding.rlnImageClip.hide()
//        }
//    }


//    private fun getMemberList() {
//        EasyHttp.get(this).api(
//                GetMemberListApi.params(group.conversationId)
//        ).request(object : HttpCallbackProxy<HttpData<ArrayList<MemberList>>>(this) {
//            override fun onHttpStart(call: Call?) {
//                //
//            }
//
//            @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
//            override fun onHttpSuccess(data: HttpData<ArrayList<MemberList>>) {
//                if (data.isRequestSucceed()) {
//                    membersList.clear()
//                    dataMemberListTemp.clear()
//                    membersList.add(MemberList("All", AppConstants.USER_TAG_ALL_ID))//Thêm tag All
//                    membersList.addAll(data.getData()!!)
//                    membersList.removeIf { x -> x.userId == UserCache.getUser().id }//Loại bỏ bản thân ra khỏi list tag
//                    dataMemberListTemp.addAll(membersList)
//                    userTagAdapter?.notifyDataSetChanged()
//                } else {
//                    if (data.isRequestError()) {
//                        toast(data.getMessage())
//                    }
//                    Timber.e(
//                            "${
//                                AppApplication.applicationContext()
//                                        .getString(vn.techres.line.R.string.error_message)
//                            } ${data.getMessage()}"
//                    )
//                }
//            }
//
//            override fun onHttpFail(e: java.lang.Exception?) {
//
//                Timber.e(
//                        "${
//                            AppApplication.applicationContext()
//                                    .getString(vn.techres.line.R.string.error_message)
//                        } ${e?.message}"
//                )
//            }
//        })
//    }

    @SuppressLint(
            "IntentWithNullActionLaunch", "NotifyDataSetChanged",
            "ClickableViewAccessibility"
    )
    private fun initAction() {
        contentBinding.llClick.setOnClickListener {
            hideKeyboard(contentBinding.llClick)
        }

//        headerBinding.header.tvGroupName.setOnClickListener {
//            AppUtils.disableClickAction(headerBinding.header.tvGroupName, 500)
//            val intent = Intent(this, DetailChatActivity::class.java)
//            val bundle = Bundle()
//            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }

        headerBinding.header.ibBack.setOnClickListener {
//            onBackPressed()
            EventBus.getDefault().post(EvenBusConversation())
            finish()
        }

        headerBinding.header.ibMenu.setOnClickListener {
            AppUtils.disableClickAction(headerBinding.header.ibMenu, 500)
            val intent = Intent(this, SettingConversationDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(group))
            intent.putExtras(bundle)
            startActivity(intent)
        }

        contentBinding.rcvChat.setOnClickListener {
//            hideKeyboard(actionBinding.inputChat.edtChat)
        }

        actionBinding.inputChat.ivGallery.setOnClickListener {
            currentKeyUpload = ChatConstants.EMIT_UPLOAD
            toolbarChatActionGallery()
        }
//
//        actionBinding.inputChat.ibMore.setOnClickListener { toolbarChatActionMore() }
//
//
//
//        actionBinding.inputChat.edtChat.setOnTouchListener { _, _ ->
//            toolbarChatActionEditText()
//            false
//        }
//
        actionBinding.inputChat.edtChat.addTextChangedListener(textMessageWatcher)
//
        actionBinding.inputChat.ibSend.setOnClickListener {
            send()
        }
//
//        actionBinding.reply.btnDelete.setOnClickListener {
//            isReplyMessage = false
//            actionBinding.llReplyMessage.hide()
//        }
//
//        actionBinding.link.chatAttachmentLinkDelete.setOnClickListener {
//            isLink = false
//            linkThumbnail = Thumbnail()
//            actionBinding.llLinkMessage.hide()
//        }

//        contentBinding.sendPhotoClip.setOnClickListener {
//            getImageClipUpload(imageClip)
//            imageClip.clear()
//            images.stringList.clear()
//            contentBinding.rlnImageClip.hide()
//            imageClipAdapter?.notifyDataSetChanged()
//        }
//
//        contentBinding.ivImageClipDelete.setOnClickListener {
//            imageClip.clear()
//            images.stringList.clear()
//            contentBinding.rlnImageClip.hide()
//            imageClipAdapter?.notifyDataSetChanged()
//        }
//
//        contentBinding.sendListImageClip.setOnClickListener {
//            getImageClipUpload(imageClip)
//            imageClip.clear()
//            images.stringList.clear()
//            images.stringList.addAll(imageClip)
//            contentBinding.rlnImageClip.hide()
//            imageClipAdapter?.notifyDataSetChanged()
//        }
//
//        contentBinding.cancelListImageClip.setOnClickListener {
//            imageClip.clear()
//            images.stringList.clear()
//            contentBinding.rlnImageClip.hide()
//            imageClipAdapter?.notifyDataSetChanged()
//        }
//
//        contentBinding.openImage.setOnClickListener {
//            contentBinding.lnView.hide()
//            contentBinding.rltListImageClip.show()
//        }
//
//        contentBinding.closeImage.setOnClickListener {
//            contentBinding.lnView.show()
//            contentBinding.rltListImageClip.hide()
//            Handler(Looper.getMainLooper()).postDelayed({
//                contentBinding.rlnImageClip.isVisible = contentBinding.rltListImageClip.isVisible
//            }, 5000)
//        }
//
//        actionBinding.llSendFile.setOnClickListener {
//            actionBinding.llSendFile.isEnabled = false
//            postDelayed({ actionBinding.llSendFile.isEnabled = true }, 1000)
//            if (checkPermissionsFile()) {
//                openFileManager()
//            } else {
//                requestPermissionsReadFile()
//            }
//        }
//
//        actionBinding.llVote.setOnClickListener {
//            val intent = Intent(
//                    this@ConversationDetailActivity, CreateVoteActivity::class.java
//            )
//            val bundle = Bundle()
//            bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(group))
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        contentBinding.pinned.llPinned.setOnClickListener {
//            val message = Message()
//            message.messageObjectInteracted.messageId = arrayPinned.first().messageId
//            message.messageObjectInteracted.position = arrayPinned.first().position
//            message.messageObjectInteracted.type = arrayPinned.first().type
//            scrollToReplyMessage(message)
//        }
//
//        contentBinding.headerBackground.ibCancel.setOnClickListener {
//            contentBinding.headerBackground.llBackgroundView.hide()
//            if (arrayPinned.isEmpty()) contentBinding.pinned.llPinned.hide()
//            else contentBinding.pinned.llPinned.show()
//            headerBinding.rltHeader.show()
//            PhotoLoadUtils.loadImageBackgroundByGlide(
//                    headerBinding.ivBackground,
//                    group.background.original.url
//            )
//            idbackground = ""
//            imageBackground = ""
//        }
//
//        contentBinding.headerBackground.ibConfirm.setOnClickListener {
//            if (idbackground.isEmpty()) {
//                toast(getString(R.string.empty_background))
//            } else {
//                apiChangeBackgroundGroup(idbackground)
//                contentBinding.headerBackground.llBackgroundView.hide()
//                if (arrayPinned.isEmpty()) contentBinding.pinned.llPinned.hide()
//                else contentBinding.pinned.llPinned.show()
//                headerBinding.rltHeader.show()
//            }
//        }
//
//        headerBinding.header.ibAddMember.setOnClickListener {
//            AppUtils.disableClickAction(headerBinding.header.ibAddMember, 1000)
//            val bundle = Bundle()
//            val intent = Intent(
//                    this, Class.forName(ModuleClassConstants.ADD_MEMBER_GROUP)
//            )
//            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        contentBinding.pinned.tvPinnedMore.setOnClickListener {
//            AppUtils.disableClickAction(contentBinding.pinned.tvPinnedMore, 1000)
//            setClickListPinned()
//        }
//
//        contentBinding.pinned.ivPinnedOne.setOnClickListener {
//            AppUtils.disableClickAction(contentBinding.pinned.ivPinnedOne, 1000)
//            setClickListPinned()
//        }
//
//        headerBinding.header.ibSearch.setOnClickListener {
//            actionBinding.inputChat.edtChat.setText("")
//            headerBinding.headerSearch.svSearch.setQuery("", false)
//            headerBinding.header.llHeader.hide()
//            headerBinding.headerSearch.llMessSearch.show()
//            actionBinding.countMessSearch.show()
//            actionBinding.rcvUserTag.hide()
//            actionBinding.llReplyMessage.hide()
//            actionBinding.llLinkMessage.hide()
//            actionBinding.searchSticker.hide()
//            actionBinding.extensions.hide()
//            actionBinding.lnMic.hide()
//            actionBinding.inputChat.ibEmojiSticker.isSelected = false
//            actionBinding.inputChat.ibMore.isSelected = false
//            actionBinding.inputChat.btnMic.isSelected = false
//            actionBinding.inputChat.ivGallery.isSelected = false
//            hideKeyboard(actionBinding.inputChat.edtChat)
//            actionBinding.emojiPopupLayout.hidePopupView()
//            actionBinding.inputChat.llInput.hide()
//            actionBinding.llBlockView.hide()
//            actionBinding.llDeleteAccountView.hide()
//        }
//
        headerBinding.headerSearch.ivBack.setOnClickListener {
            actionBinding.inputChat.edtChat.setText("")
            headerBinding.headerSearch.svSearch.setQuery("", false)
            headerBinding.headerSearch.llMessSearch.hide()
            headerBinding.header.llHeader.show()
            actionBinding.llReplyMessage.hide()
            actionBinding.lnMic.hide()
            actionBinding.inputChat.ibEmojiSticker.isSelected = false
            actionBinding.inputChat.ibMore.isSelected = false
            actionBinding.inputChat.btnMic.isSelected = false
            actionBinding.inputChat.ivGallery.isSelected = false
            hideKeyboard(actionBinding.inputChat.edtChat)
        }
//
//        headerBinding.headerSearch.svSearch.doOnSubmitTextListener(headerBinding.headerSearch.svSearch,500) {
//            if (it.isNotEmpty()) {
//                searchMessage(it, false)
//            } else {
//                actionBinding.messCountSearch.text = getString(R.string.no_result_message)
//                actionBinding.countUp.hide()
//                actionBinding.countDown.hide()
//            }
//        }
//
//        actionBinding.countUp.setOnClickListener {
//            AppUtils.disableClickAction(actionBinding.countUp, 1000)
//            val previousIndex = chatSearchList.indexOfFirst { it.messageId == jumpSearchMessage }
//            if (previousIndex == chatSearchList.size - 1 && chatSearchList.size < totalResultSearch) {
//                searchMessage(headerBinding.headerSearch.svSearch.query.toString(), true)
//            } else {
//                jumpToSearchMessage(ChatConstants.DIRECTION_SEARCH_UP)
//            }
//        }
//
//        actionBinding.countDown.setOnClickListener {
//            AppUtils.disableClickAction(actionBinding.countDown, 1000)
//            val previousIndex = chatSearchList.indexOfFirst { it.messageId == jumpSearchMessage }
//            if (previousIndex == -1 || previousIndex == 0) {
//                if (chatSearchList.size >= totalResultSearch) {
//                    jumpToSearchMessage(ChatConstants.DIRECTION_SEARCH_DOWN)
//                }
//            } else {
//                jumpToSearchMessage(ChatConstants.DIRECTION_SEARCH_DOWN)
//            }
//        }
    }



    /**
     * change background group
     */
//    private fun apiChangeBackgroundGroup(background: String) {
//        val isCommon = if (contentBinding.headerBackground.cbCommon.isChecked) 1 else 0
//        EasyHttp.post(this)
//                .api(ChangeBackgroundGroupApi.params(group.conversationId, background, isCommon))
//                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
//                    override fun onHttpEnd(call: Call?) {
//                        //empty
//                    }
//
//                    override fun onHttpStart(call: Call?) {
//                        //empty
//                    }
//
//                    override fun onHttpFail(e: java.lang.Exception?) {
//
//                        Timber.e(
//                                "${
//                                    AppApplication.applicationContext()
//                                            .getString(vn.techres.line.R.string.error_message)
//                                } ${e?.message}"
//                        )
//                    }
//
//                    override fun onHttpSuccess(data: HttpData<Any>) {
//                        if (!data.isRequestSucceed()) {
//                            if (data.isRequestError()) {
//                                toast(data.getMessage())
//                                idbackground = ""
//                                imageBackground = ""
//                            }
//                            Timber.e(
//                                    "${
//                                        AppApplication.applicationContext()
//                                                .getString(vn.techres.line.R.string.error_message)
//                                    } ${data.getMessage()}"
//                            )
//                        }
//                    }
//                })
//    }

    // Gửi ảnh hoặc video
    private fun toolbarChatActionGallery() {
        actionBinding.inputChat.ibEmojiSticker.isSelected = false
        actionBinding.inputChat.ibMore.isSelected = false
        actionBinding.inputChat.btnMic.isSelected = false

        if (currentKeyUpload == ChatConstants.CHANGE_BACKGROUND) {
            actionBinding.inputChat.ivGallery.isSelected = false
            PhotoPickerUtils.showImagePickerChooseAvatarChat(this, imagePickerLauncher)
        } else {
            actionBinding.inputChat.ivGallery.isSelected = true
            PhotoPickerUtils.showImagePickerChat(this, imagePickerLauncher)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            actionBinding.inputChat.ivGallery.isSelected = false

            if (result.resultCode == Activity.RESULT_OK) {
                imageClip.clear()
                imageClipAdapter?.notifyDataSetChanged()
                val localMedias =
                    PictureSelector.obtainSelectorList(result.data) as ArrayList<LocalMedia>
                val localMedia = localMedias.first()
                if (FileTypeUtils.checkFileSizeToUpload(localMedias, this)) {
                    uploadToCloudinary(localMedias,localMedia.realPath)
                }
            }
        }

    @SuppressLint("SetTextI18n")
    private fun uploadToCloudinary(localMedias: ArrayList<LocalMedia>, realPath: String) {
        Log.d("A", "sign up uploadToCloudinary- ")
        localMedias.forEach { localMedia ->
            MediaManager.get().upload(localMedia.realPath).callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("start", "start")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    Log.d("Uploading... ", "Uploading...")
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
//                    var mediaList = MediaList()
//                    mediaList.original = resultData["url"].toString()
//                    mediaList.content = resultData["original_filename"].toString()
//                    mediaList.type = if(resultData["resource_type"].toString() == "image") 2 else  3
                    mediaImage.add(resultData["url"].toString())
                    hanleMedia(mediaImage, localMedias, currentKeyUpload, realPath)

                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.d("error " + error.description, "error")
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    Log.d("Reshedule " + error.description, "Reshedule")
                }
            }).dispatch()
        }
    }

    private fun hanleMedia(
        mediaImage: ArrayList<String>,
        mediaImageLocal : ArrayList<LocalMedia>,
        keyUpload: String,
        cutPath: String
    ){
        val chatImage = MessageEmit()

        for (i in mediaImage.indices) {
            mediaImage.add(mediaImage[i])
            chatImage.media.add(mediaImage[i])
        }

        if (keyUpload == ChatConstants.EMIT_UPLOAD) {
            //Đưa tin nhắn hình ảnh và video lên trước
            val messageLocal = Message()
            val userPost = User()
            userPost.id = UserCache.getUser().id
            messageLocal.media

//            if (chatImage.media.size > 0) {
                messageSend = chatImage
                messageSend.conversationId = group.conversationId
                messageSend.type = MessageTypeChatConstants.IMAGE
                ChatUtils.emitSocket(SocketChatConstants.EMIT_MESSAGE, messageSend)
                onDoChat(mediaImage, mediaImageLocal)
//            }
        } else {
            PhotoLoadUtils.loadImageBackgroundByGlide(
                headerBinding.ivBackground,
                cutPath
            )
        }
    }



    // Mở hộp công cụ
//    private fun toolbarChatActionMore() {
//        actionBinding.inputChat.ibEmojiSticker.isSelected = false
//        actionBinding.inputChat.btnMic.isSelected = false
//        actionBinding.inputChat.ivGallery.isSelected = false
//
//        actionBinding.searchSticker.hide()
//        actionBinding.emojiPopupLayout.hidePopupView()
//        actionBinding.lnMic.hide()
//        hideKeyboard(actionBinding.inputChat.edtChat)
//
//        actionBinding.extensions.isVisible = !actionBinding.extensions.isVisible
//        actionBinding.inputChat.ibMore.isSelected = actionBinding.extensions.isVisible
//        pauseRecording(0)
//        initAudio()
//    }

    //Thao tác với ô input chat
//    private fun toolbarChatActionEditText() {
//        pauseRecording(0)//Ngưng record và xóa audio
//        initAudio()
//        actionBinding.inputChat.ibEmojiSticker.isSelected = false
//        actionBinding.inputChat.ibMore.isSelected = false
//        actionBinding.inputChat.btnMic.isSelected = false
//        actionBinding.inputChat.ivGallery.isSelected = false
//        actionBinding.extensions.hide()
//        actionBinding.lnMic.hide()
//        actionBinding.extensions.hide()
//        actionBinding.lnMic.hide()
//    }

    @SuppressLint("NotifyDataSetChanged")
    private fun send() {
        val s = actionBinding.inputChat.edtChat.text.toString()
        if (s.length > 2000) {
            val sendCount = if (s.length % 2000 == 0) {
                (s.length / 2000)
            } else {
                (s.length / 2000) + 1
            }

            for (i in 0 until sendCount) {
                when (i) {
                    0 -> sendSocket(s.substring(0, 2000))

                    1 -> calculateSendMessage(s, 2000)

                    2 -> calculateSendMessage(s, 4000)

                    3 -> calculateSendMessage(s, 6000)

                    4 -> calculateSendMessage(s, 8000)
                }
            }
        } else {
            sendSocket(s)
        }
    }

    private fun sendSocket(message: String) {
        messageReply.message = message
        var normalMessage = message

        messageSend = MessageEmit()
        messageSend.message = normalMessage
        messageSend.conversationId = group.conversationId
        messageSend.type = MessageTypeChatConstants.TEXT
//        messageSend.keyError = generateRandomString(10)
        ChatUtils.emitSocket(SocketChatConstants.EMIT_MESSAGE, messageSend)
        onDoChat(ArrayList(), arrayListOf())
    }

    private fun calculateSendMessage(
            str: String, n: Int
    ) {
        if (str.length - n in 1..2000) {
            sendSocket(str.substring(n, str.length))
        } else {
            sendSocket(str.substring(n, n + 1999))
        }
    }


    private fun checkPermissionsFile(): Boolean {
        val result = ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    fun generateRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }






    //--------------------------------------------------------------------------------------------//

    //-------------------------------------Handle Upload------------------------------------------//

//    private fun getImageUpload(
//            listMedia: List<LocalMedia>,
//            keyUpload: String,
//            cutPath: String
//    ) {
//        for (i in listMedia.indices) {
//            val media = Medias()
//            val type = AppUtils.checkMimeTypeVideo(listMedia[i].realPath)
//            if (type) {
//                media.type = 1
//            }
//            media.name = listMedia[i].fileName
//            media.size = listMedia[i].size
//            media.width = listMedia[i].width
//            media.height = listMedia[i].height
//            media.isKeep = 1
//            medias.add(media)
//        }
//        EasyHttp.post(this).api(
//                GetLinkAvatarApi.params(medias)
//        ).request(object : HttpCallbackProxy<HttpData<ArrayList<MediaList>>>(this) {
//            @SuppressLint("SuspiciousIndentation")
//            override fun onHttpSuccess(data: HttpData<ArrayList<MediaList>>) {
//                if (data.isRequestSucceed()) {
//                    val chatImage = MessageEmit()
//                    val chatVideo = MessageEmit()
//                    val mediaImage = ArrayList<MediaList>()
//                    val mediaVideo = ArrayList<MediaList>()
//                    val mediaImageLocal = ArrayList<LocalMedia>()
//                    val mediaVideoLocal = ArrayList<LocalMedia>()
//                    for (i in data.getData()!!.indices) {
//                        if (data.getData()!![i].type == MediaConstants.IMAGE) {
//                            mediaImage.add(data.getData()!![i])
//                            mediaImageLocal.add(listMedia[i])
//                            val medias = MediasNode()
//                            medias.content = ""
//                            medias.mediaId = data.getData()!![i].mediaId
//                            chatImage.media.add(medias.mediaId!!)
//                        } else {
//                            mediaVideo.add(data.getData()!![i])
//                            mediaVideoLocal.add(listMedia[i])
//                            val medias = MediasNode()
//                            medias.content = ""
//                            medias.mediaId = data.getData()!![i].mediaId
//                            chatVideo.media.add(medias.mediaId!!)
//                        }
//                    }
//
//                    if (keyUpload == ChatConstants.EMIT_UPLOAD) {
//                        AppUtils.onUpload(this@ConversationDetailActivity, data.getData()!!, listMedia)
//                        //Đưa tin nhắn hình ảnh và video lên trước
//                        val messageLocal = Message()
//                        val userPost = User()
//                        userPost.id = UserCache.getUser().id
//                        messageLocal.media
//
//                        if (chatImage.media.size > 0) {
//                            messageSend = chatImage
//                            ChatUtils.emitSocket(SocketChatConstants.EMIT_CHAT_IMAGE, messageSend)
//                            onDoChat(mediaImage, StickerModel(), mediaImageLocal)
//                        }
//                        if (chatVideo.media.size > 0) {
//                            messageSend = chatVideo
//                            ChatUtils.emitSocket(SocketChatConstants.EMIT_CHAT_VIDEO, chatVideo)
//                            onDoChat(mediaVideo, StickerModel(), mediaVideoLocal)
//                        }
//                    } else {
//                        idbackground = data.getData()!!.first().mediaId
//                        AppUtils.onUpload(this@ConversationDetailActivity, data.getData()!!, listMedia)
//                        PhotoLoadUtils.loadImageBackgroundByGlide(
//                                headerBinding.ivBackground,
//                                cutPath
//                        )
//                    }
//
//                    medias.clear()
//                } else {
//                    if (data.isRequestError()) {
//                        toast(data.getMessage())
//                    }
//                    Timber.e(
//                            "${
//                                AppApplication.applicationContext()
//                                        .getString(vn.techres.line.R.string.error_message)
//                            } ${data.getMessage()}"
//                    )
//                }
//            }
//
//            override fun onHttpFail(e: java.lang.Exception?) {
//
//                Timber.e(
//                        "${
//                            AppApplication.applicationContext()
//                                    .getString(vn.techres.line.R.string.error_message)
//                        } ${e?.message}"
//                )
//            }
//        })
//    }




    //--------------------------------------------------------------------------------------------//

    override fun onYoutubePlayer(
            youTubePlayer: YouTubePlayer?,
            youTubePlayerView: YouTubePlayerView?
    ) {
        initializedYouTubePlayer = youTubePlayer
        this.youTubePlayerView = youTubePlayerView
    }


    override fun onRevoke(messagesByGroup: Message, view: View, y: Int, textView: TextView?) {
//        setLongClickItemMessageNew(messagesByGroup, view, y)
    }

//    private fun setLongClickItemMessageNew(
//            messages: Message,
//            view: View,
//            y: Int
//    ) {
//        if (!isFinishing) {
//            try {
//                if (initializedYouTubePlayer != null) {
//                    initializedYouTubePlayer!!.pause()
//                }
//                if (mediaPlayer != null) {
//                    mediaPlayer!!.release()
//                }
//
//                if (youTubePlayerView != null) {
//                    youTubePlayerView!!.release()
//                }
//                if (positionPlayer != -1) {
//                    messageList[positionPlayer].media.first().stop = true
//                    messageAdapter?.notifyItemChanged(positionPlayer)
//                }
//                currentAudioPlay.release()
//
//                val screen = IntArray(2)
//                DialogMessageAction.Builder(
//                        this, messages, view, y, messageAdapter!!, screen, group
//                ).setListener(object : DialogMessageAction.ActionListener {
//                    override fun copyMess(message: Message) {
//                        actionCopy(message)
//                    }
//
//                    override fun deleteMess(message: Message) {
//                        if (message.isErrorMessage) {
//                            val index =
//                                    messageList.indexOfFirst { it.messageId == message.messageId }
//                            if (index != -1) {
//                                messageList.removeAt(index)
//                                messageAdapter?.notifyItemRemoved(index)
//                            }
//                        } else if (!message.isServerResponse) {
//                            toast(
//                                    AppApplication.applicationContext()
//                                            .getString(R.string.you_can_not_action_this_message)
//                            )
//                        } else {
//                            apiDeleteMessenger(message)
//                        }
//                    }
//
//                    @SuppressLint("NewApi")
//                    override fun replyMess(message: Message) {
//                        actionReply(message)
//                    }
//
//                }).show()
//            } catch (ignored: java.lang.Exception) {
//                ignored.printStackTrace()
//            }
//        }
//    }


    private fun apiDeleteMessenger(messages: Message) {
//        EasyHttp.post(this).api(DeleteMessengerApi.params(messages.messageId))
//                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
//                    @SuppressLint("NotifyDataSetChanged")
//                    override fun onHttpSuccess(data: HttpData<Any>) {
//                        if (data.isRequestSucceed()) {
//                            messageAdapter?.removeItem(messages)
//                            messageAdapter?.notifyDataSetChanged()
//                        } else {
//                            if (data.isRequestError()) {
//                                toast(data.getMessage())
//                            }
//                            Timber.e(
//                                    "${
//                                        AppApplication.applicationContext()
//                                                .getString(vn.techres.line.R.string.error_message)
//                                    } ${data.getMessage()}"
//                            )
//                        }
//                    }
//
//                    override fun onHttpFail(e: java.lang.Exception?) {
//
//                        Timber.e(
//                                "${
//                                    AppApplication.applicationContext()
//                                            .getString(vn.techres.line.R.string.error_message)
//                                } ${e?.message}"
//                        )
//                    }
//                })
    }







    override fun scrollToReplyMessage(message: Message) {
//        val scrollIndex =
//                messageList.indexOfFirst { x -> x.messageId == message.messageObjectInteracted.messageId }
//        if (message.messageObjectInteracted.type == MessageTypeChatConstants.PINNED_VOTE) {
//        } else {
//            if (scrollIndex != -1) {
//                contentBinding.rcvChat.smoothScrollToPosition(scrollIndex)
//                contentBinding.rcvChat.post {
//                    messageList[scrollIndex].highlight = 1
//                    messageAdapter?.notifyItemChanged(scrollIndex)
//                }
//            } else {
//                scrollOption = ChatConstants.SCROLL_TO_REPLY
//                getMessage(
//                        message.messageObjectInteracted.position,
//                        isScrollToBottom = false,
//                        scrollOption
//                )
//            }
//        }
    }



    //-----------------------------------------EventBus-------------------------------------------//

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onChangeBackground(event: EventbusChangeBackground) {
//        if (event.key == 1) {
//            //Setup header
//            ImmersionBar.setTitleBar(this, contentBinding.headerBackground.llHeaderBackground)
//            contentBinding.headerBackground.llBackgroundView.show()
//            headerBinding.rltHeader.hide()
//            contentBinding.pinned.llPinned.hide()
//        }
//    }
//
//    @Subscribe
//    fun onSticker(event: EventBusStickerClick) {
//        messageSend = MessageEmit()
//        messageSend.stickerId = event.stickerId
//        actionBinding.link.rlnLinkClip.hide()
//        actionBinding.llLinkMessage.hide()
//        actionBinding.link.youtubeClip.hide()
//        actionBinding.searchSticker.hide()
//        ChatUtils.emitSocket(SocketChatConstants.EMIT_STICKER, messageSend)
//        onDoChat(
//                ArrayList(),
//                StickerModel(event.stickerId, Original(event.stickerUrl)),
//                arrayListOf()
//        )
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEventScrollToPinned(event: EventBusScrollToPin) {
//        val message = Message()
//        message.messageObjectInteracted.messageId = event.message.messageId
//        message.messageObjectInteracted.position = event.message.position
//        message.messageObjectInteracted.type = event.message.type
//        scrollToReplyMessage(message)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onTriggerSearchInput(eventBus: TriggerSearchInputEventBus) {
//        headerBinding.header.ibSearch.performClick()
//    }

    //--------------------------------------------------------------------------------------------//

    //----------------------------------------Handle Chat-----------------------------------------//
//    @SuppressLint("NotifyDataSetChanged")
    private fun onDoChat(
            mediaList: ArrayList<String>,
            mediaLocalList: ArrayList<LocalMedia>
    ) {
        val itemMessage = Message()
        with(itemMessage) {
            this.messageId = messageSend.keyError
            this.message = messageSend.message
            this.user = Sender(
                UserCache.getUser().id,
                UserCache.getUser().name,
                UserCache.getUser().avatar
            )
            this.userTarget = ArrayList()
            this.media.addAll(mediaList)
            with(this.conversation) {
                this.conversationId = group.conversationId
                this.name = group.name
                this.avatar = group.avatar
                this.type = group.type
                this.isPinned = group.isPinned
                this.isNotify = group.isNotify
                this.isConfirmNewMember = group.isConfirmNewMember
                this.myPermission = group.myPermission
                this.noOfMember = group.noOfMember
                this.lastActivity = group.lastActivity
                this.position = group.position
                this.createdAt = group.createdAt
                this.updatedAt = group.updatedAt
            }
            this.createdAt = messageSend.createAt
            this.updatedAt = messageSend.createAt
            this.position = messageSend.keyError
            this.keyError = messageSend.keyError
            this.isServerResponse = false
            this.isErrorMessage = false
        }

        if ((itemMessage.type == MessageTypeChatConstants.IMAGE || itemMessage.type == MessageTypeChatConstants.VIDEO) && mediaLocalList.isNotEmpty()) {
            for (i in itemMessage.media.indices) {
                itemMessage.media[i] = mediaLocalList[i].realPath
            }
        }

        //Xử lý dữ liệu để cập nhật ở màn hình bên ngoài (GroupFragment)
        handleLastMessage(itemMessage)

        messageList.add(0, itemMessage)
        messageAdapter?.notifyItemInserted(0)
        if (messageList.size > 1) {
            messageAdapter?.notifyItemChanged(1)
        }
        contentBinding.rcvChat.post {
            contentBinding.rcvChat.smoothScrollToPosition(0)
        }

        //-----Handle 10s sau mà ko có phản hồi gì từ server thì tin nhắn này là tin nhắn lỗi-----//
        Handler(Looper.getMainLooper()).postDelayed({
            val index = messageList.indexOfFirst { it.messageId == itemMessage.keyError }
            if (index != -1) {
                if (messageList[index].messageId == itemMessage.keyError) {
                    messageList[index].isErrorMessage = true
                    messageList[index].isServerResponse = false
                    messageAdapter?.notifyItemChanged(index)
                }
            }
        }, 10000)
        //----------------------------------------------------------------------------------------//

        contentBinding.scrollMessage.hide()
        actionBinding.llReplyMessage.hide()
        //reset tất cả dữ liệu cần thiết cho chat
        messageSend = MessageEmit()
        imageClip.clear()
        imageClipAdapter?.notifyDataSetChanged()
        alTagged.clear() //khởi tạo lại list rỗng :v
        messageReply = MessageEmit()
        isLink = false
        isReplyMessage = false
        actionBinding.inputChat.edtChat.setTextColor(getColor(R.color.black))
        actionBinding.inputChat.edtChat.setText("")
    }

    //--------------------------------------------------------------------------------------------//
}