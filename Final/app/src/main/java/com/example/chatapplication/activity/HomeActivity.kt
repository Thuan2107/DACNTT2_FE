package com.example.chatapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.chatapplication.R
import com.example.chatapplication.adapter.NavigationAdapter
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication.Companion.socketChat
import com.example.chatapplication.app.AppFragment
import com.example.chatapplication.base.FragmentPagerAdapter
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.MessageTypeChatConstants
import com.example.chatapplication.databinding.HomeActivityBinding
import com.example.chatapplication.dialog.ActionGroupChatDialog
import com.example.chatapplication.fragment.EmptyFragment
import com.example.chatapplication.fragment.PhoneBookFragment
import com.example.chatapplication.fragment.GroupFragment
import com.example.chatapplication.fragment.SettingAccountFragment
import com.example.chatapplication.model.entity.ChatSocket
import com.example.chatapplication.model.entity.Title
import com.example.chatapplication.utils.AppUtils.invisible
import com.google.gson.Gson
import io.socket.emitter.Emitter
import timber.log.Timber


class HomeActivity : AppActivity(), NavigationAdapter.OnNavigationListener {
    lateinit var binding: HomeActivityBinding
    private var titleMessage = Title()
    private var titleDirectory = Title()
    private var titleDiscover = Title()
    private var titleDiary = Title()
    private var titleIndividual = Title()


    companion object {
        private const val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
        private const val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"
        var lastItem = 0
    }

    private var navigationAdapter: NavigationAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null


    override fun getLayoutView(): View {
        binding = HomeActivityBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun initView() {
        titleMessage.title = getString(R.string.home_nav_message)
        titleDirectory.title = getString(R.string.home_nav_directory)
        titleDiary.title = getString(R.string.home_nav_diary)
        titleIndividual.title = getString(R.string.home_nav_individual)
        navigationAdapter = NavigationAdapter(this).apply {
            addItem(
                    NavigationAdapter.MenuItem(
                        titleMessage,
                            ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_chat_selector)
                    )
            )
            addItem(
                    NavigationAdapter.MenuItem(
                        titleDirectory,
                            ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_contact_selector)
                    )
            )
            addItem(
                    NavigationAdapter.MenuItem(
                        titleDiary,
                            ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_time_fast_selector)
                    )
            )
            addItem(
                    NavigationAdapter.MenuItem(
                        titleIndividual,
                            ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_account_selector)
                    )
            )
            setOnNavigationListener(this@HomeActivity)
        }

        binding.recyclerViewNavigation.adapter = navigationAdapter

    }

    override fun initData() {
        pagerAdapter = FragmentPagerAdapter<AppFragment<*>>(this).apply {
            addFragment(
                GroupFragment()
            )
            addFragment(
                PhoneBookFragment()
            )
//            addFragment(
//                EmptyFragment()
//            )
            addFragment(
                EmptyFragment()
            )
            addFragment(
                SettingAccountFragment()
            )
        }
        binding.nsvPager.adapter = pagerAdapter
        binding.nsvPager.offscreenPageLimit = 4
        switchFragment(0)


        //Kiểm tra connect các socket
//        if (socketChat != null) {
//            Timber.d(
//                    "Socket chat Aloline " +
//                            "\n Domain: ${ConfigCache.getConfig().apiConnection}" +
//                            "\n Trạng thái kết nối: ${socketChat!!.connected()}"
//            )
//        } else {
//            if (ConfigCache.getConfig().apiConnection != "") {
//                val options = IO.Options.builder()
//                        .setExtraHeaders(
//                                Collections.singletonMap(
//                                        "Authorization",
//                                        listOf(
//                                                UserCache.accessToken()
//                                        )
//                                )
//                        )
//                        .setReconnection(true)
//                        .setTransports(arrayOf(Polling.NAME, WebSocket.NAME, PollingXHR.NAME))
//                        .build()
//                socketChat = IO.socket(ConfigCache.getConfig().apiConnection, options).connect()
//            }
//        }




    }

    override fun onResume() {
        super.onResume()
        eventClickBtnSearch()
        eventClickCreateGroup()
    }

//    private fun registerSocketNewMessage() {
//        /**
//         * Socket khi có tin nhắn đến
//         */
//        socketChat?.on(SocketChatConstants.ON_CHAT_TEXT, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHAT_IMAGE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHAT_VIDEO, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHAT_FILE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHAT_AUDIO, newMessage)
//        socketChat?.on(SocketChatConstants.ON_STICKER, newMessage)
//        socketChat?.on(SocketChatConstants.ON_REPLY, newMessage)
//        socketChat?.on(SocketChatConstants.ON_PINNED, newMessage)
//        socketChat?.on(SocketChatConstants.ON_REMOVE_PINNED, newMessage)
//        socketChat?.on(SocketChatConstants.ON_ADD_MEMBER, newMessage)
//        socketChat?.on(SocketChatConstants.ON_REMOVE_MEMBER, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHANGE_NAME_GROUP, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHANGE_AVATAR_GROUP, newMessage)
//        socketChat?.on(SocketChatConstants.ON_OUT_GROUP, newMessage)
//        socketChat?.on(SocketChatConstants.ON_UPDATE_PERMISSION_MESSAGE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_UPDATE_OWNER_CONVERSATION, newMessage)
//        socketChat?.on(SocketChatConstants.ON_UPDATE_ADD_DEPUTY_CONVERSATION, newMessage)
//        socketChat?.on(SocketChatConstants.ON_OFF_IS_CONFIRM_CONVERSATION, newMessage)
//        socketChat?.on(SocketChatConstants.ON_WAITING_CONFIRM_CONVERSATION, newMessage)
//        socketChat?.on(SocketChatConstants.ON_UPDATE_REMOVE_DEPUTY_CONVERSATION, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CREATE_VOTE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_MESSAGE_VOTE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHANGE_VOTE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_ADD_OPTION_VOTE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_BLOCK_VOTE, newMessage)
//        socketChat?.on(SocketChatConstants.ON_CHANGE_BACKGROUND, newMessage)
//        socketChat?.on(SocketChatConstants.ON_SHARE_SOCKET, newMessage)
//        socketChat?.on("${SocketChatConstants.ON_JOIN_ROOM}/${UserCache.getUser().id}", onJoinRoom)
//        //Listen delete conversation history
//        socketChat?.on(
//                "${SocketChatConstants.ON_DELETE_CONVERSATION_HISTORY}/${UserCache.getUser().id}",
//                onDeleteConversation
//        )
//    }

//    private fun unregisterSocketNewMessage() {
//        socketChat?.off(SocketChatConstants.ON_CHAT_TEXT, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHAT_IMAGE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHAT_VIDEO, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHAT_FILE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHAT_AUDIO, newMessage)
//        socketChat?.off(SocketChatConstants.ON_STICKER, newMessage)
//        socketChat?.off(SocketChatConstants.ON_REPLY, newMessage)
//        socketChat?.off(SocketChatConstants.ON_PINNED, newMessage)
//        socketChat?.off(SocketChatConstants.ON_REMOVE_PINNED, newMessage)
//        socketChat?.off(SocketChatConstants.ON_ADD_MEMBER, newMessage)
//        socketChat?.off(SocketChatConstants.ON_REMOVE_MEMBER, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHANGE_NAME_GROUP, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHANGE_AVATAR_GROUP, newMessage)
//        socketChat?.off(SocketChatConstants.ON_OUT_GROUP, newMessage)
//        socketChat?.off(SocketChatConstants.ON_UPDATE_PERMISSION_MESSAGE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_UPDATE_OWNER_CONVERSATION, newMessage)
//        socketChat?.off(SocketChatConstants.ON_UPDATE_ADD_DEPUTY_CONVERSATION, newMessage)
//        socketChat?.off(SocketChatConstants.ON_OFF_IS_CONFIRM_CONVERSATION, newMessage)
//        socketChat?.off(SocketChatConstants.ON_WAITING_CONFIRM_CONVERSATION, newMessage)
//        socketChat?.off(SocketChatConstants.ON_UPDATE_REMOVE_DEPUTY_CONVERSATION, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CREATE_VOTE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_MESSAGE_VOTE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHANGE_VOTE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_ADD_OPTION_VOTE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_BLOCK_VOTE, newMessage)
//        socketChat?.off(SocketChatConstants.ON_CHANGE_BACKGROUND, newMessage)
//        socketChat?.off(SocketChatConstants.ON_SHARE_SOCKET, newMessage)
//        socketChat?.off("${SocketChatConstants.ON_JOIN_ROOM}/${UserCache.getUser().id}", onJoinRoom)
//        socketChat?.off(
//                "${SocketChatConstants.ON_DELETE_CONVERSATION_HISTORY}/${UserCache.getUser().id}",
//                onDeleteConversation
//        )
//    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        pagerAdapter?.let {
            switchFragment(it.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.nsvPager.let {
            // Lưu vị trí chỉ mục Fragment hiện tại
            outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, it.currentItem)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Khôi phục vị trí chỉ mục Fragment hiện tại
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX))
    }

    private fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }
        when (fragmentIndex) {
            0, 1, 2, 3, 4 -> {
                binding.nsvPager.currentItem = fragmentIndex
                navigationAdapter?.setSelectedPosition(fragmentIndex)
            }
        }
    }







    /**
     * [NavigationAdapter.OnNavigationListener]
     */
    override fun onNavigationItemSelected(position: Int): Boolean {
        lastItem= binding.nsvPager.currentItem
        when (position) {
            1 -> {
//                if (binding.nsvPager.currentItem == 1) {
//                    EventBus.getDefault().post(EventBusClickScrollChat())
//                }
                binding.nsvPager.currentItem = position
            }

            2 -> {
//                if (binding.nsvPager.currentItem == 2) {
//                    EventBus.getDefault().post(EventBusClickScrollNewsfeed())
//                }
                binding.nsvPager.currentItem = position
            }

            3 -> {
//                if (binding.nsvPager.currentItem == 3) {
//                    EventBus.getDefault().post(EventBusClickScrollNotify())
//                }
                binding.nsvPager.currentItem = position
            }

            4 -> {
//                if (binding.nsvPager.currentItem == 3) {
//                    EventBus.getDefault().post(EventBusClickScrollNotify())
//                }
                binding.nsvPager.currentItem = position
            }

            else -> {
                binding.nsvPager.currentItem = position
            }
        }

        binding.createGroup.isVisible = position == 0
//
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
//        unregisterSocketNewMessage()
        binding.nsvPager.adapter = null
        binding.recyclerViewNavigation.adapter = null
        navigationAdapter?.setOnNavigationListener(null)
        if (socketChat != null && socketChat!!.connected()) {
            socketChat!!.disconnect()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 5469 && Settings.canDrawOverlays(this)) {
//            if (!OverlayService.initialized && Settings.canDrawOverlays(this)) {
//                val service = Intent(this, OverlayService::class.java)
//                service.putExtra("token", "ss")
//                startService(service)
//            }
//        }
    }

    /**
     * Socket khi có tin nhắn đến
     */
    private val newMessage = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.d("NEW_MESSAGE" + args[0].toString())
                val chatSocket =
                        Gson().fromJson(args[0].toString(), ChatSocket::class.java)

                if (chatSocket.type == MessageTypeChatConstants.REMOVE_USER) {
                    if (chatSocket.user.userId != UserCache.getUser().id && chatSocket.userTarget.all { it.userId != UserCache.getUser().id }) {
                        titleMessage.amount += 1
                        navigationAdapter?.notifyItemChanged(1)
                    }
                } else {
                    if (chatSocket.user.userId != UserCache.getUser().id) {
                        titleMessage.amount += 1
                        navigationAdapter?.notifyItemChanged(1)
                    }
                }
            }
        }.start()
    }

   
    /**
     * Socket xóa lịch sử cuộc trò chuyện
     */
//    @SuppressLint("NotifyDataSetChanged")
//    private val onDeleteConversation = Emitter.Listener { args: Array<Any> ->
//        Thread {
//            PictureThreadUtils.runOnUiThread {
//                Timber.d(SocketChatConstants.ON_DELETE_CONVERSATION_HISTORY + args[0].toString())
//                val group = Gson().fromJson(args[0].toString(), DeleteHistoryConversation::class.java)
//                dataCount.noOfMessageNotSeen -= group.noOfNotSeen
//                if (dataCount.noOfMessageNotSeen < 0) {
//                    dataCount.noOfMessageNotSeen = 0
//                }
//                titleMessage.amount -= group.noOfNotSeen
//                if (titleMessage.amount < 0) {
//                    titleMessage.amount = 0
//                }
//                navigationAdapter?.notifyDataSetChanged()
//            }
//        }.start()
//    }

    private fun eventClickBtnSearch() {
        binding.tvSearchViewForChat.setOnClickListener {
            val intent = Intent(this, SearchManagerActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean(AppConstants.JOIN_GROUP, false)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun eventClickCreateGroup() {
        binding.createGroup.setOnClickListener {
            ActionGroupChatDialog.Builder(this).show()
        }
    }




}