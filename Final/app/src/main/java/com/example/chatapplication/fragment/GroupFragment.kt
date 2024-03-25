package com.example.chatapplication.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.activity.ConversationDetailActivity
import com.example.chatapplication.activity.HomeActivity
import com.example.chatapplication.adapter.GroupAdapter
import com.example.chatapplication.api.DeleteGroupApi
import com.example.chatapplication.api.DetailGroupApi
import com.example.chatapplication.api.GroupApi
import com.example.chatapplication.api.GroupPinnedApi
import com.example.chatapplication.api.OutGroupApi
import com.example.chatapplication.api.PinnedGroupApi
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.app.AppApplication.Companion.socketChat
import com.example.chatapplication.app.AppFragment
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.constant.MessageTypeChatConstants
import com.example.chatapplication.constant.SocketChatConstants
import com.example.chatapplication.databinding.FragmentGroupBinding
import com.example.chatapplication.dialog.DialogChooseLeaderBeforeOutGroup
import com.example.chatapplication.dialog.DialogOutGroup
import com.example.chatapplication.dialog.DialogSelectionChat
import com.example.chatapplication.eventbus.DeleteEventBus
import com.example.chatapplication.eventbus.EvenBusConversation
import com.example.chatapplication.eventbus.EventBusLastMessage
import com.example.chatapplication.eventbus.OutGroupEventBus
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.model.entity.GroupSocket
import com.example.chatapplication.model.entity.JoinAndLeaveRoom
import com.example.chatapplication.model.entity.ListenJoinRoomSocket
import com.example.chatapplication.model.entity.Message
import com.example.chatapplication.other.CustomLoadingListItemCreator
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PictureThreadUtils.runOnUiThread
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.luck.picture.lib.thread.PictureThreadUtils
import com.paginate.Paginate
import io.socket.emitter.Emitter
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import timber.log.Timber

class GroupFragment : AppFragment<HomeActivity>(),
        GroupAdapter.GroupChatListener {
    private lateinit var binding: FragmentGroupBinding
    private var groupDataList: ArrayList<GroupChat> = ArrayList()
    private var adapterGroupChat: GroupAdapter? = null
    private var limitGroup: Int = 20
    private var isResume: Int = 0
    private var isLoadMore: Boolean = true
    private var loading: Boolean = false
    private var paginate: Paginate? = null
    private var position: String = ""
    private val groups: GroupChat = GroupChat("HeaderFriend")    //add vị trí đầu tiên của mảng
    private var dialogSelectionChat: DialogSelectionChat.Builder? = null

    companion object {
        fun newInstance(): GroupFragment {
            return GroupFragment()
        }
    }

    override fun getLayoutView(): View {
        binding = FragmentGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {
        adapterGroupChat = GroupAdapter(requireActivity())
        adapterGroupChat?.setData(groupDataList)
        adapterGroupChat?.setGroupChatListener(this)
        AppUtils.initRecyclerViewVertical(binding.rclGroupChat, adapterGroupChat)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {

        registrySocket()
        binding.rclGroupChat.suppressLayout(true) // không cho phép cuộn rcv

        isResume = 1
        groupDataList.clear()
        adapterGroupChat?.notifyDataSetChanged()
        position = ""
        getGroupChatPinned(position)
        paginate()
    }





    /**
     * socket
     */
    private fun registrySocket() {

        /**
         * Socket khi có tin nhắn đến
         */
        socketChat?.on(SocketChatConstants.ON_MESSAGE, onLastMessage)

        //Listen Socket Error
        socketChat?.on(SocketChatConstants.ON_SOCKET_ERROR, onSocketError)

        //Listen Socket Join Room
        socketChat?.on("${SocketChatConstants.ON_JOIN_ROOM}/${UserCache.getUser().id}", onJoinRoom)


    }

    override fun onDestroy() {
        super.onDestroy()
        unRegistrySocket()
    }

    private fun unRegistrySocket() {

        socketChat?.off(SocketChatConstants.ON_MESSAGE, onLastMessage)

        socketChat?.off(SocketChatConstants.ON_SOCKET_ERROR, onSocketError)

        socketChat?.off(
                "${SocketChatConstants.ON_JOIN_ROOM}/${UserCache.getUser().id}", onJoinRoom
        )

    }

    private fun paginate() {
        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                postDelayed({
                    if (isLoadMore && groupDataList.size > countNumberOfOAGroup() + 1) {
                        getGroupChatPinned(groupDataList[groupDataList.size - 1].position)
                    }
                }, 200)
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                return !isLoadMore

            }

        }

        paginate = Paginate.with(binding.rclGroupChat, callback).setLoadingTriggerThreshold(0)
                .addLoadingListItem(true).setLoadingListItemCreator(CustomLoadingListItemCreator())
                .build()
    }


    override fun clickGroup(id: String) {
        val clickItem = groupDataList.find { it.id == id }
        if (clickItem != null) {
            try {
                clickItem.conversationId = clickItem.id
                val intent = Intent(
                        getAttachActivity(), ConversationDetailActivity::class.java
                )
                val bundle = Bundle()
                bundle.putString(
                    ChatConstants.GROUP_DATA, Gson().toJson(clickItem)
                )
                intent.putExtras(bundle)
                startActivity(intent)
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }

    override fun onLongClickGroup(position: Int, group: GroupChat) {
        dialogSelectionChat = DialogSelectionChat.Builder(
                requireContext(), group
        ).setListener(object : DialogSelectionChat.OnListener {
            override fun onSelectionChat(
                    idGroup: String, type: Int, dialog: Dialog
            ) {
                when (type) {
                    ChatConstants.TYPE_PINNED -> {
                        setPinned(idGroup, false)
                        dialog.dismiss()
                    }

                    ChatConstants.TYPE_UN_PINNED -> {
                        setPinned(idGroup, true)
                        dialog.dismiss()
                    }


                    ChatConstants.TYPE_DELETE -> {
                        dialog.dismiss()
                        DialogOutGroup.Builder(
                                requireContext(),
                                getString(R.string.common_confirm),
                                getString(R.string.policy_delete_group),
                                getString(R.string.delete_the_group)
                        ).setListener(object : DialogOutGroup.OnListener {
                            override fun onPolicyConfirm(next: Int) {
                                deleteGroup(idGroup)
                            }
                        }).show()
                    }
//
                    ChatConstants.TYPE_OUT_GROUP -> {
                        dialog.dismiss()
                        DialogOutGroup.Builder(
                                requireContext(),
                                getString(R.string.title_out_group),
                                getString(R.string.policy_out_group),
                                getString(R.string.leave_the_group)
                        ).setListener(object : DialogOutGroup.OnListener {
                            override fun onPolicyConfirm(next: Int) {
                                if (group.myPermission == ConversationTypeConstants.OWNER) {
                                    DialogChooseLeaderBeforeOutGroup.Builder(
                                            requireContext(),
                                            this@GroupFragment,
                                            idGroup
                                    ).setOnChangeLeaderListener(object : DialogChooseLeaderBeforeOutGroup.OnChangeLeaderListener {
                                        override fun onChangeLeader() {
                                            setOutConversation(idGroup)
                                        }
                                    }).show()
                                } else {
                                    setOutConversation(idGroup)
                                }
                            }
                        }).show()
                    }
                    else -> {
                        dialog.dismiss()
                    }
                }
            }

        })
        dialogSelectionChat!!.show()
    }

    override fun clickAvatar(position: Int, avatar: String) {
        AppUtils.showMediaAvatar(requireActivity(), avatar, position)

    }


    private fun addNewGroupChat(group: GroupChat) {
        if (groupDataList.isNotEmpty()) {
            if (group.isPinned == 1) {//nếu cuộc trò chuyện đó có không có ghim thì nó nằm ở dưới danh sách group OA
                if (countNumberOfOAGroup() + 1 == groupDataList.size) {
                    groupDataList.add(group)
                } else {
                    groupDataList.add(countNumberOfOAGroup() + 1, group)
                }
            } else {//nếu có ghim thì cho dưới phần tử ghim cuối cùng
                if (countNumberOfOAGroup() + countNumberOfPinnedGroup() + 1 == groupDataList.size) {
                    groupDataList.add(group)
                } else {
                    groupDataList.add(
                            countNumberOfOAGroup() + countNumberOfPinnedGroup() + 1,
                            group
                    )
                }
            }
            adapterGroupChat?.notifyItemInserted(getPositionToInsertGroup(group))
        }
    }

    private fun getPositionToInsertGroup(group: GroupChat): Int {
        return if (group.isPinned == 1) {//nếu cuộc trò chuyện được ghim thì nó nằm ở dưới danh sách group OA
            countNumberOfOAGroup() + 1
        } else {//nếu có ghim thì cho dưới phần tử ghim cuối cùng
            countNumberOfOAGroup() + countNumberOfPinnedGroup() + 1
        }
    }

    private fun countNumberOfPinnedGroup(): Int {
        return groupDataList.count { it.conversationSystemId.isEmpty() && it.conversationId.isNotEmpty() && it.isPinned == 1 }
    }

    private fun countNumberOfOAGroup(): Int {
        return groupDataList.count { it.conversationSystemId.isNotEmpty() && it.conversationId.isEmpty() }
    }

    //---------------------------------------LISTEN SOCKET----------------------------------------//

    private val onSocketError = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {
                Timber.e("ON SOCKET ERROR%s", args[0].toString())
            }
        }.start()
    }

    private val onJoinRoom = Emitter.Listener { args: Array<Any> ->
        Thread {
            PictureThreadUtils.runOnUiThread {
                Timber.d(SocketChatConstants.ON_JOIN_ROOM + args[0].toString())
                val joinRoomSocket =
                    Gson().fromJson(args[0].toString(), ListenJoinRoomSocket::class.java)
                val index =
                    groupDataList.indexOfFirst { it.conversationId == joinRoomSocket.data.conversationId && it.conversationSystemId.isEmpty() }
                if (index != -1) {
                    groupDataList[index].noOfNotSeen = 0
                    adapterGroupChat?.notifyItemChanged(index)
                }
            }
        }.start()
    }

    @SuppressLint("NewApi", "NotifyDataSetChanged")
    private val onLastMessage = Emitter.Listener { args: Array<Any> ->
        Thread {
            runOnUiThread {

                val jsonObject = args[0] as JSONObject
                val messageJson = jsonObject.getJSONObject("message")
                val lastMess = Gson().fromJson(
                    messageJson.toString(), Message::class.java)

                val group = GroupChat()
                group.conversationId = lastMess.conversation.conversationId
                group.name = if (lastMess.type == MessageTypeChatConstants.UPDATE_NAME) lastMess.message else lastMess.conversation.name
                group.avatar = lastMess.conversation.avatar
                group.isOnline = 1//Tin nhắn mới thì hẳn là đang on
                group.position = lastMess.conversation.position
                group.type = lastMess.conversation.type
                group.noOfMember = lastMess.conversation.noOfMember
                group.lastActivity = lastMess.conversation.lastActivity
                group.lastMessage.messageId = lastMess.messageId
                group.lastMessage.message = lastMess.message
                group.lastMessage.type = lastMess.type
                group.lastMessage.user = lastMess.user
                group.lastMessage.userTarget = lastMess.userTarget
                group.userStatus = AppConstants.STATUS_ACTIVE
                val indexItem =
                        groupDataList.indexOfFirst { it.conversationId == lastMess.conversation.conversationId }

                if (indexItem != -1) {//Nếu tìm thấy group này trong list group chat
                    val foundItem = groupDataList[indexItem]
                    groupDataList.removeAt(indexItem)//Xóa item cũ ra khỏi list
                    adapterGroupChat?.notifyDataSetChanged()
                    if (!(lastMess.type == MessageTypeChatConstants.REMOVE_USER && lastMess.userTarget.any { it.userId == UserCache.getUser().id })) {
                        group.noOfNotSeen = foundItem.noOfNotSeen + 1
                        group.isNotify = foundItem.isNotify
                        group.isPinned = foundItem.isPinned
                        group.myPermission = foundItem.myPermission
                        group.isConfirmNewMember = foundItem.isConfirmNewMember
                        group.isHidden = foundItem.isHidden
                        addNewGroupChat(group)
                    }
                } else {//Không có thì call api chi tiết để lấy
                    getDetailGroup(group)
                }
            }
        }.start()
    }


    //--------------------------------------------------------------------------------------------//

    //--------------------------------------------API---------------------------------------------//

    /**
     * Out group
     */
    private fun setOutConversation(idGroup: String) {
        EasyHttp.post(this).api(OutGroupApi.params(idGroup))
                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onHttpSuccess(data: HttpData<Any>) {
                        if (data.isRequestSucceed()) {
                            val index = groupDataList.indexOfFirst { it.id == idGroup }
                            if (index != -1) {
                                groupDataList.removeAt(index)
                                adapterGroupChat?.notifyItemRemoved(index)
                            }
                        } else {
                            toast(data.getData())
                        }
                    }

                    override fun onHttpEnd(call: Call?) {
                        //empty
                    }

                    override fun onHttpStart(call: Call?) {
                        //empty
                    }

                })
    }

    /**
     * api ghim cuộc trò chuyện
     */
    private fun setPinned(idGroup: String, typeAll: Boolean) {
        EasyHttp.post(this).api(PinnedGroupApi.params(idGroup))
                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onHttpSuccess(data: HttpData<Any>) {
                        if (data.isRequestSucceed()) {
                            if (typeAll) {
                                toast(getString(R.string.unpin_1_chat))
                            } else {
                                toast(getString(R.string.pin_1_chat))
                            }
                            getGroupChatPinned("")
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

                    override fun onHttpStart(call: Call?) {
                        //empty
                    }

                    override fun onHttpEnd(call: Call?) {
                        //empty
                    }

                })
    }



    /**
     * api xoá lịch sử trò chuyện
     */
    private fun deleteGroup(idGroup: String) {
        EasyHttp.post(this).api(DeleteGroupApi.params(idGroup))
                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                    @SuppressLint("NotifyDataSetChanged", "NewApi")
                    override fun onHttpSuccess(data: HttpData<Any>) {
                        if (data.isRequestSucceed()) {
                            val index = groupDataList.indexOfFirst { it.id == idGroup }
                            if (index != -1) {
                                groupDataList.removeAt(index)
                                adapterGroupChat?.notifyDataSetChanged()
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

    /**
     * Gọi api lấy danh sách cuộc trò chuyện pinned
     */
    @SuppressLint("HardwareIds")
    private fun getGroupChatPinned(position: String) {
        EasyHttp.get(this).api(GroupPinnedApi.params(position, "", limitGroup))
            .request(/* listener = */ object : HttpCallbackProxy<HttpData<List<GroupChat>>>(this) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<List<GroupChat>>) {
                    if (data.isRequestSucceed()) {
                        val index = data.getData()!!.size
                        groupDataList.clear()
                        groupDataList.addAll(data.getData()!!)
                        getGroupChat(position, index)
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpFail(throwable: Throwable?) {
                    loading = false
                }

                override fun onHttpStart(call: okhttp3.Call?) {
                    super.onHttpStart(call)
                }

                override fun onHttpEnd(call: okhttp3.Call?) {
                    super.onHttpEnd(call)
                }

            })
    }



    /**
     * Gọi api lấy danh sách cuộc trò chuyện
     */
    @SuppressLint("HardwareIds")
    private fun getGroupChat(position: String, index: Int) {
        EasyHttp.get(this).api(GroupApi.params(position, "", limitGroup))
                .request(/* listener = */ object : HttpCallbackProxy<HttpData<List<GroupChat>>>(this) {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onHttpSuccess(data: HttpData<List<GroupChat>>) {
                        if (data.isRequestSucceed()) {
                            loading = false
                            if (data.getData() == null) {
                                isLoadMore = false
                            } else {
                                isLoadMore = data.getData()!!.size + index >= limitGroup
                                paginate?.setHasMoreDataToLoad(isLoadMore)
                                groupDataList.addAll(index, data.getData()!!)
                                adapterGroupChat?.notifyDataSetChanged()
                                if (groupDataList.size == 0) {
                                    binding.lnEmptyGroupChat.show()
                                    binding.rclGroupChat.hide()
                                } else {
                                    binding.lnEmptyGroupChat.hide()
                                    binding.rclGroupChat.show()
                                }
                            }
                            isResume = 0
                            binding.rclGroupChat.suppressLayout(false)
                        }else{
                            loading = false

                            Timber.e(
                                "${
                                    AppApplication.applicationContext()
                                        .getString(R.string.error_message)
                                } ${data.getMessage()}"
                            )
                            isResume = 0
                            binding.rclGroupChat.suppressLayout(false)  // cho phép cuộn rcv
                        }

                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onHttpFail(throwable: Throwable?) {
                        loading = false
                    }

                    override fun onHttpStart(call: okhttp3.Call?) {
                        super.onHttpStart(call)
                    }

                    override fun onHttpEnd(call: okhttp3.Call?) {
                        super.onHttpEnd(call)
                    }

                })
    }





    //Api lấy detail group
    private fun getDetailGroup(group: GroupChat) {
        EasyHttp.get(this).api(DetailGroupApi.params(group.conversationId))
                .request(object : HttpCallbackProxy<HttpData<GroupChat>>(this) {
                    override fun onHttpEnd(call: Call?) {
                        //empty
                    }

                    override fun onHttpStart(call: Call?) {
                        //empty
                    }

                    @SuppressLint("IntentWithNullActionLaunch", "NotifyDataSetChanged")
                    override fun onHttpSuccess(data: HttpData<GroupChat>) {
                        if (data.isRequestSucceed()) {
                            data.getData()?.let {
                                val indexItem =
                                        groupDataList.indexOfFirst { gr -> gr.conversationId == group.conversationId }
                                if (indexItem != -1) {//Nếu tìm thấy group này trong list group chat
                                    groupDataList.removeAt(indexItem)//Xóa item cũ ra khỏi list
                                    adapterGroupChat?.notifyDataSetChanged()
                                }
                                if (!(group.lastMessage.type == MessageTypeChatConstants.REMOVE_USER &&
                                                group.lastMessage.userTarget.any { lastMess -> lastMess.userId == UserCache.getUser().id })
                                ) {
                                    group.noOfNotSeen = it.noOfNotSeen + 1
                                    group.isNotify = it.isNotify
                                    group.isPinned = it.isPinned
                                    group.myPermission = it.myPermission
                                    group.isConfirmNewMember = it.isConfirmNewMember
                                    group.isHidden = it.isHidden
                                    addNewGroupChat(group)
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

    //--------------------------------------------------------------------------------------------//

    //-----------------------------------------EVENT BUS------------------------------------------//

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOutGroup(event: OutGroupEventBus) {
        val index = groupDataList.indexOfFirst { it.id == event.groupId }
        if (index != -1) {
            groupDataList.removeAt(index)
            adapterGroupChat?.notifyDataSetChanged()
            if (groupDataList.size == 0) {
                binding.lnEmptyGroupChat.show()
            } else {
                binding.lnEmptyGroupChat.hide()
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged", "NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLastMessageJoinRoom(event: EventBusLastMessage) {
        val indexItem =
                groupDataList.indexOfFirst { it.conversationId == event.groupChat.conversationId }
        if (indexItem != -1) {
            groupDataList.removeAt(indexItem)//Xóa item cũ ra khỏi list
            adapterGroupChat?.notifyDataSetChanged()
        }

        if (!(event.groupChat.type == MessageTypeChatConstants.REMOVE_USER && event.groupChat.lastMessage.userTarget.any { it.userId == UserCache.getUser().id })) {
            addNewGroupChat(event.groupChat)
        }
        if (groupDataList.size < 2) {
            binding.lnEmptyGroupChat.show()
        } else {
            binding.lnEmptyGroupChat.hide()
        }
    }


    //Xóa lịch sử cuộc trò chuyện
    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeleteConversation(event: DeleteEventBus) {
        val index = groupDataList.indexOfFirst { it.conversationId == event.idGroup }
        if (index != -1) {
            groupDataList.removeAt(index)
            adapterGroupChat?.notifyDataSetChanged()

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetListConversation(event: EvenBusConversation) {
        getGroupChatPinned("")
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

   

    //--------------------------------------------------------------------------------------------//
}