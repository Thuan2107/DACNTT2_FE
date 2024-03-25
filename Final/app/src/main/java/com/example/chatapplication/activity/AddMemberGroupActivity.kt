package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chatapplication.R
import com.example.chatapplication.adapter.ChooseFriendAdapter
import com.example.chatapplication.adapter.MyFriendAdapter
import com.example.chatapplication.api.AddMemberApi
import com.example.chatapplication.api.GetListFriend
import com.example.chatapplication.api.GroupApi
import com.example.chatapplication.api.GroupPinnedApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.app.AppApplication.Companion.socketChat
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.constant.SocketChatConstants
import com.example.chatapplication.databinding.ActivityAddMemberGroupBinding
import com.example.chatapplication.eventbus.EventbusAddMember
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.other.CustomLoadingListItemCreator
import com.example.chatapplication.other.queryAfterTextChangedSV
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.paginate.Paginate
import io.socket.emitter.Emitter
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class AddMemberGroupActivity : AppActivity(), MyFriendAdapter.ChooseItemListener,
    ChooseFriendAdapter.RemoveItemListener {
    private lateinit var binding: ActivityAddMemberGroupBinding
    private var friendList: ArrayList<Friend> = ArrayList()
    private var groupDataList: ArrayList<GroupChat> = ArrayList()
    private var chooseFriendList: ArrayList<Friend> = ArrayList()
    private var myFriendAdapter: MyFriendAdapter? = null
    private var chooseFriendAdapter: ChooseFriendAdapter? = null
    var group: GroupChat = GroupChat()
    private var member: ArrayList<String> = ArrayList()
    private var localMedia: LocalMedia? = null
    private var keySearch = ""
    private var btnTwo = 1
    private var btnThree = 0
    private var limit = 20
    private var isLoadMore = true
    private var paginate: Paginate? = null
    private var loading = false

    override fun getLayoutView(): View {
        binding = ActivityAddMemberGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(
            R.anim.right_in_activity,
            R.anim.right_out_activity
        )
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
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.itemViewToAddMember.tbHeaderAddMember)

        myFriendAdapter = MyFriendAdapter(this, 1)
        AppUtils.initRecyclerViewVertical(binding.rcvMyFriend, myFriendAdapter)
        myFriendAdapter!!.setClickChooseItem(this)

        chooseFriendAdapter = ChooseFriendAdapter(this)
        AppUtils.initRecyclerViewHorizontal(
            binding.rcvChooseItem,
            chooseFriendAdapter
        )
        chooseFriendAdapter!!.setClickRemove(this)

        chooseFriendAdapter!!.setData(chooseFriendList)
        myFriendAdapter!!.setData(friendList)
        search()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        binding.itemViewToAddMember.tbHeaderAddMember.show()
        binding.itemViewToAddMember.lnGroupChat.show()
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(ChatConstants.GROUP_DATA)) {
                group = Gson().fromJson(
                    bundleIntent.getString(ChatConstants.GROUP_DATA), GroupChat::class.java
                )
            }
        }
        binding.itemViewToAddMember.llNameGroup.hide()
        setOnClickListener(
            binding.ibCreate,
            binding.itemViewToAddMember.btnCamera,
            binding.itemViewToAddMember.tvLately,
            binding.itemViewToAddMember.tvPhoneBook,
            binding.itemViewToAddMember.tvSuggest,
            binding.itemViewToAddMember.lnHeader,
            binding.itemViewToAddMember.ibClose
        )
        getGroupChatPinned("")
        binding.itemViewToAddMember.ibClose.setOnClickListener {
            finish()
        }
        paginate()
    }

    private fun paginate() {
        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                if (isLoadMore && friendList.size > 0) {
                    getFriend(
                        if (btnThree != 1) 0 else 1,
                        friendList[(friendList.size - 1)].position
                    )
                    loading = false
                    isLoadMore = false
                }
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                return !isLoadMore

            }

        }

        paginate = Paginate.with(binding.rcvMyFriend, callback)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(CustomLoadingListItemCreator())
            .build()
    }

    private val onAddMember =
        Emitter.Listener { args: Array<Any> ->
            Thread {
                runOnUiThread {
                    Timber.e("Socket AddMember %s", Gson().toJson(args.first().toString()))
                    getFriend(if (btnThree != 1) 0 else 1, "")
                }
            }.start()
        }

    private fun onAddMember() {
        val addMemberGroup = ArrayList<String>()
        for (i in chooseFriendList.indices) {
            addMemberGroup.add(chooseFriendList[i].userId)
        }

        EasyHttp.post(this).api(AddMemberApi.params(group.conversationId, addMemberGroup))
            .request(object : HttpCallbackProxy<HttpData<ArrayList<Friend>>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpSuccess(data: HttpData<ArrayList<Friend>>) {
                    if (data.isRequestSucceed()) {
                        finish()
                        toast(getString(R.string.already_add_to_waiting_list))

                    } else {
                        Timber.e(
                            "${
                                AppApplication.applicationContext()
                                    .getString(R.string.error_message)
                            } ${data.getMessage()}"
                        )
                        hideDialog()
                    }
                }

                override fun onHttpFail(throwable: Throwable?) {
                    Timber.e(
                        "${
                            AppApplication.applicationContext()
                                .getString(R.string.error_message)
                        } ${throwable}"
                    )
                    hideDialog()
                }
            })
    }

    /**
     * sự kiện onclick
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View) {
        when (view) {

            binding.ibCreate -> {
                AppUtils.disableClickAction(binding.ibCreate, 1000)
                onAddMember()
            }

//            binding.itemViewToAddMember.btnCamera -> {
//                PhotoPickerUtils.showImagePickerChooseAvatar(
//                    this, pickerImageIntent
//                )
//            }

            binding.itemViewToAddMember.tvLately -> {
                if (btnTwo == 0) {
                    btnTwo = 1
                    btnThree = 0
                    selected()
                    friendList.clear()
                    getGroupChatPinned("")

                }
            }

            binding.itemViewToAddMember.tvPhoneBook -> {
                if (btnThree == 0) {
                    btnThree = 1
                    btnTwo = 0
                    selected()
                    friendList.clear()
                    getFriend(1, "")
                }
            }

            binding.itemViewToAddMember.lnHeader -> {
                finish()
            }

            binding.itemViewToAddMember.ibClose -> {
                finish()
            }
        }
    }

    /**
     * Sự kiện thay đổi avatar
     */
    private var pickerImageIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent = it.data!!
            localMedia = PictureSelector.obtainSelectorList(data)[0]
            PhotoLoadUtils.loadImageAvatarByGlide(
                binding.itemViewToAddMember.btnCamera,
                localMedia!!.realPath
            )
        }
    }

    /**
     * Gọi api lấy danh sách bạn bè
     */
    private fun getFriend(isFriend: Int, position: String) {
        if (friendList.isEmpty()) {
            binding.lnEmpty.hide()
            binding.sflMyFriend.show()
            binding.sflMyFriend.startShimmer()
            binding.rcvMyFriend.hide()
        }
        EasyHttp.get(this).api(
            GetListFriend.params(
                AppConstants.FRIEND
            )
        ).request(object : HttpCallbackProxy<HttpData<List<Friend>>>(this) {
            override fun onHttpStart(call: Call?) {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onHttpSuccess(data: HttpData<List<Friend>>) {
                if (data.isRequestSucceed()) {
                    loading = false
                    binding.sflMyFriend.hide()
                    binding.sflMyFriend.stopShimmer()
                    binding.rcvMyFriend.show()
                    data.getData()?.let { response ->
                        isLoadMore = response.size >= limit
                        paginate?.setHasMoreDataToLoad(isLoadMore)
                        for (i in response.indices) {
                            val item = Friend()
                            item.fullName = response[i].fullName
                            item.avatar = response[i].avatar
                            item.userId = response[i].userId
                            item.lastActivity = response[i].lastActivity
                            item.position = response[i].position
                            if (chooseFriendList.all { it.userId != response[i].userId }) {
                                item.isMember = 0
                            } else {
                                item.isMember = 1
                            }
                            friendList.add(item)
                        }
                        myFriendAdapter?.notifyDataSetChanged()
                        if (friendList.isEmpty()) {
                            binding.rcvMyFriend.hide()
                            binding.lnEmpty.show()
                        } else {
                            binding.rcvMyFriend.show()
                            binding.lnEmpty.hide()
                        }
                    }
                } else {
                    Timber.e(
                        "${
                            AppApplication.applicationContext()
                                .getString(R.string.error_message)
                        } ${data.getMessage()}"
                    )
                    hideDialog()
                }
            }

            override fun onHttpFail(throwable: Throwable?) {
                Timber.e(
                    "${
                        AppApplication.applicationContext()
                            .getString(R.string.error_message)
                    } ${throwable}"
                )
                hideDialog()
            }
        })
    }

    /**
     * Gọi api lấy danh sách cuộc trò chuyện
     */
    @SuppressLint("HardwareIds")
    private fun getGroupChatPinned(position: String) {
        EasyHttp.get(this).api(GroupPinnedApi.params(position, "", 100))
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
        EasyHttp.get(this).api(GroupApi.params(position, "", 100))
            .request(/* listener = */ object : HttpCallbackProxy<HttpData<List<GroupChat>>>(this) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<List<GroupChat>>) {
                    if (data.isRequestSucceed()) {
                        groupDataList.addAll(index, data.getData()!!)
                        val data = groupDataList.filter { it.type == 2 }
                        loading = false
                        binding.sflMyFriend.hide()
                        binding.sflMyFriend.stopShimmer()
                        binding.rcvMyFriend.show()
                        data?.let { response ->
                            isLoadMore = response.size >= limit
                            paginate?.setHasMoreDataToLoad(isLoadMore)
                            for (i in response.indices) {
                                val item = Friend()
                                item.fullName = response[i].name
                                item.avatar = response[i].avatar
                                item.userId = response[i].userTargetId
                                item.lastActivity = response[i].lastActivity
                                item.position = response[i].position
                                if (chooseFriendList.all { it.userId != response[i].userTargetId }) {
                                    item.isMember = 0
                                } else {
                                    item.isMember = 1
                                }
                                friendList.add(item)
                            }
                            myFriendAdapter?.notifyDataSetChanged()
                            if (friendList.isEmpty()) {
                                binding.rcvMyFriend.hide()
                                binding.lnEmpty.show()
                            } else {
                                binding.rcvMyFriend.show()
                                binding.lnEmpty.hide()
                            }
                        }
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

    @SuppressLint("NotifyDataSetChanged")
    override fun clickChooseItem(position: Int, isChecked: Boolean, item: Friend) {
        if (isChecked) {
            if (chooseFriendList.size >= 100) {
                friendList[position].isMember = 0
                toast(getString(R.string.max_members))
            } else {
                friendList[position].isMember = 1
            }

            var isHas = 0
            if (chooseFriendList.isNotEmpty()) {
                for (i in 1..chooseFriendList.size) {
                    if (chooseFriendList[i - 1].userId == friendList[position].userId) {
                        isHas = 1
                        break
                    }
                }
            }
            if (isHas == 0) {
                chooseFriendList.add(friendList[position])
                member.add(friendList[position].userId)
            }
            chooseFriendAdapter!!.notifyDataSetChanged()
            checkChooseItem(member)
        } else {
            friendList[position].isMember = 0
            var isHas = 1
            if (chooseFriendList.isNotEmpty()) {
                for (i in 1..chooseFriendList.size) {
                    if (chooseFriendList[i - 1].userId == friendList[position].userId) {
                        isHas = 0
                        break
                    }
                }
            }

            if (isHas == 0) {
                for (i in 1..chooseFriendList.size) {
                    if (chooseFriendList[i - 1].userId == friendList[position].userId) {
                        chooseFriendList.removeAt(i - 1)
                        break
                    }
                }
                member.remove(friendList[position].userId)
                chooseFriendAdapter!!.notifyDataSetChanged()
            }
            checkChooseItem(member)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun removeItem(position: Int) {
        for (i in 1..friendList.size) {
            if (friendList[i - 1].userId == chooseFriendList[position].userId) {
                friendList[i - 1].isMember = 0
                chooseFriendList.removeAt(position)
                member.removeAt(position)
                checkChooseItem(member)
                chooseFriendAdapter!!.notifyDataSetChanged()
                myFriendAdapter!!.notifyDataSetChanged()
                return
            }
        }

        if (chooseFriendList.isNotEmpty()) {
            chooseFriendList.removeAt(position)

            member.removeAt(position)
            chooseFriendAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * api tạo group chat
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun search() {
        binding.itemViewToAddMember.svFriend.queryAfterTextChangedSV(delay = 500) {
            friendList.clear()
            keySearch = it
            getFriend(if (btnThree != 1) 0 else 1, "")
        }
    }

    private fun selected() {
        if (btnTwo == 1) {
            binding.itemViewToAddMember.tvLately.setBackgroundResource(R.drawable.bg_form_input_on)
            binding.itemViewToAddMember.tvLately.setTextColor(
                resources.getColor(
                    R.color.white, null
                )
            )
        } else {
            binding.itemViewToAddMember.tvLately.setBackgroundResource(R.drawable.bg_search)
            binding.itemViewToAddMember.tvLately.setTextColor(
                resources.getColor(
                    R.color.main_bg, null
                )
            )
        }
        if (btnThree == 1) {
            binding.itemViewToAddMember.tvPhoneBook.setBackgroundResource(R.drawable.bg_form_input_on)
            binding.itemViewToAddMember.tvPhoneBook.setTextColor(
                resources.getColor(
                    R.color.white, null
                )
            )
        } else {
            binding.itemViewToAddMember.tvPhoneBook.setBackgroundResource(R.drawable.bg_search)
            binding.itemViewToAddMember.tvPhoneBook.setTextColor(
                resources.getColor(
                    R.color.main_bg, null
                )
            )
        }
    }

    private fun checkChooseItem(array: ArrayList<String>) {
        if (array.size == 0) {
            binding.lnBottomView.hide()
            chooseFriendList.clear()
            member.clear()
        } else {
            binding.lnBottomView.show()
            if (array.size == 1) {
                binding.ibCreate.setBackgroundResource(R.drawable.border_blue_50dp)
                binding.ibCreate.isEnabled = true
            }
        }
    }
}
