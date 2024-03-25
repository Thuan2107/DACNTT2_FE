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
import com.example.chatapplication.api.CreateGroupApi
import com.example.chatapplication.api.GetListFriend
import com.example.chatapplication.api.GroupApi
import com.example.chatapplication.api.GroupPinnedApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.UploadTypeConstants
import com.example.chatapplication.databinding.ActivityCreateGroupChatBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.model.entity.MediaList
import com.example.chatapplication.model.entity.Medias
import com.example.chatapplication.other.CustomLoadingListItemCreator
import com.example.chatapplication.other.queryAfterTextChangedSV
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.example.chatapplication.utils.PhotoPickerUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.paginate.Paginate
import com.tencent.mmkv.MMKV
import okhttp3.Call
import timber.log.Timber


class CreateGroupChatActivity : AppActivity(), MyFriendAdapter.ChooseItemListener,
    ChooseFriendAdapter.RemoveItemListener {
    private lateinit var binding: ActivityCreateGroupChatBinding
    private var friendList: ArrayList<Friend> = ArrayList()
    private var chooseFriendList: ArrayList<Friend> = ArrayList()
    private var medias: ArrayList<Medias> = ArrayList()
    private var groupDataList: ArrayList<GroupChat> = ArrayList()
    private var localMedia: LocalMedia? = null
    private var myFriendAdapter: MyFriendAdapter? = null
    private var chooseFriendAdapter: ChooseFriendAdapter? = null
    private var name = ""
    private var avt = ""
    private var member: ArrayList<String> = ArrayList()
    private var nameGroup: ArrayList<String> = ArrayList()
    private var keySearch = ""
    private var btnTwo = 1
    private var btnThree = 0

    private var isLoadMore = true
    private var paginate: Paginate? = null
    private var loading = false
    private var limit = 20

    override fun getLayoutView(): View {
        binding = ActivityCreateGroupChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.itemViewTop.tbHeader)
        myFriendAdapter = MyFriendAdapter(this, 0)
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
        binding.itemViewTop.tvChoose.text =
            String.format("%s %s", getString(R.string.choose), member.size)

        binding.lnBottomView.hide()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        getGroupChatPinned("")
        search()
        setOnClickListener(
            binding.ibCreate,
            binding.itemViewTop.ibCamera,
            binding.itemViewTop.tvLately,
            binding.itemViewTop.tvPhoneBook,
            binding.itemViewTop.lnHeader,
            binding.itemViewTop.ibClose
        )
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

    /**
     * sự kiện onclick
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View) {
        when (view) {
            binding.ibCreate -> {
                AppUtils.disableClickAction(binding.ibCreate, 1000)
                name = binding.itemViewTop.edtName.text.toString()
//                if (localMedia != null) {
//                    getLinkAvatarUpload()
//                } else {
                    createGroup(name, avt, member)
//                }
                return
            }

            binding.itemViewTop.ibCamera -> {
                PhotoPickerUtils.showImagePickerChooseAvatar(
                    this, pickerImageIntent
                )
            }

            binding.itemViewTop.tvLately -> {
                if (btnTwo == 0) {
                    btnTwo = 1
                    btnThree = 0
                    selected()
                    friendList.clear()
                    myFriendAdapter!!.notifyDataSetChanged()
                    getGroupChatPinned("")
                }
            }

            binding.itemViewTop.tvPhoneBook -> {
                if (btnThree == 0) {
                    btnThree = 1
                    btnTwo = 0
                    selected()
                    friendList.clear()
                    myFriendAdapter!!.notifyDataSetChanged()
                    getFriend(0, "")
                }
            }

            binding.itemViewTop.lnHeader -> {
                finish()
            }

            binding.itemViewTop.ibClose -> {
                finish()
            }
        }

    }

    /**
     * Sự kiện thay đổi avatar
     */
    private var pickerImageIntent: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent = it.data!!
                localMedia = PictureSelector.obtainSelectorList(data)[0]
                PhotoLoadUtils.loadImageAvatarByGlide(
                    binding.itemViewTop.ibCamera,
                    localMedia!!.realPath
                )
            }
        }

//    private fun getLinkAvatarUpload() {
//        val media = Medias()
//        media.name = localMedia!!.fileName
//        media.type = UploadTypeConstants.UPLOAD_IMAGE
//        media.size = localMedia!!.size
//        media.isKeep = 1
//        medias.add(media)
//        EasyHttp.post(this).api(
//            GetLinkAvatarApi.params(medias)
//        ).request(object : HttpCallbackProxy<HttpData<ArrayList<MediaList>>>(this) {
//            override fun onHttpStart(call: Call?) {
//                //Empty
//            }
//
//            override fun onHttpSuccess(data: HttpData<ArrayList<MediaList>>) {
//                if (data.isRequestSucceed()) {
//                    AppUtils.onUpload(
//                        this@CreateGroupChatActivity,
//                        data.getData()!!,
//                        arrayListOf(localMedia!!)
//                    )
//                    avt = data.getData()!![0].mediaId
//                    val mmkv: MMKV = MMKV.mmkvWithID(AppConstants.CACHE_UPLOAD_TYPE_KEY)
//                    mmkv.putInt(AppConstants.CACHE_UPLOAD_TYPE_KEY, AppConstants.UPLOAD_AVATAR)
//                        .commit()
//                    createGroup(name, avt, member)
//                    localMedia = null
//                } else {
//
//                    Timber.e(
//                        "${
//                            AppApplication.applicationContext()
//                                .getString(vn.techres.line.R.string.error_message)
//                        } ${data.getMessage()}"
//                    )
//                    hideDialog()
//                }
//            }
//
//            override fun onHttpFail(e: java.lang.Exception?) {
//
//                Timber.e(
//                    "${
//                        AppApplication.applicationContext()
//                            .getString(vn.techres.line.R.string.error_message)
//                    } ${e?.message}"
//                )
//                hideDialog()
//            }
//        })
//    }

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
                nameGroup.add(friendList[position].fullName)
            }

            binding.itemViewTop.tvChoose.text =
                String.format("%s %s", getString(R.string.choose), member.size)
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
                //chooseFriendList.remove(friendList[position])
                for (i in 1..chooseFriendList.size) {
                    if (chooseFriendList[i - 1].userId == friendList[position].userId) {
                        chooseFriendList.removeAt(i - 1)
                        break
                    }
                }
                member.remove(friendList[position].userId)
                nameGroup.remove(friendList[position].fullName)

                binding.itemViewTop.tvChoose.text =
                    String.format("%s %s", getString(R.string.choose), member.size)
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
                nameGroup.removeAt(position)
                checkChooseItem(member)
                binding.itemViewTop.tvChoose.text =
                    String.format("%s %s", getString(R.string.choose), member.size)
                chooseFriendAdapter!!.notifyDataSetChanged()
                myFriendAdapter!!.notifyDataSetChanged()
                return
            }
        }

        if (chooseFriendList.isNotEmpty()) {
            chooseFriendList.removeAt(position)

            member.removeAt(position)
            nameGroup.removeAt(position)
            chooseFriendAdapter!!.notifyDataSetChanged()
        }
    }


    /**
     * api tạo group chat
     */
    private fun createGroup(nameGroupChat: String, avt: String, member: ArrayList<String>) {
        var groupName = nameGroupChat
        if (nameGroupChat.trim()
                .isEmpty()
        ) {//Nếu không có tên group thì lấy tên của member gộp lại (tối đa 40 ký tự)
            for (i in nameGroup.indices) {
                groupName += if (i == nameGroup.size - 1)
                    nameGroup[i]
                else
                    nameGroup[i] + ", "
            }
            if (groupName.length > 40) {
                groupName = regexGroupName(groupName)
            }
        }

        EasyHttp.post(this)
            .api(CreateGroupApi.params(groupName, avt, member))
            .request(object : HttpCallbackProxy<HttpData<GroupId>>(this) {
                @SuppressLint("IntentWithNullActionLaunch")
                override fun onHttpSuccess(data: HttpData<GroupId>) {
                    if (data.isRequestSucceed()) {
                        try {
                            val bundle = Bundle()
                            val intent = Intent(
                                this@CreateGroupChatActivity,
                                ConversationDetailActivity::class.java
                            )
                            val group = GroupChat()

                            group.name = groupName
                            group.noOfMember = member.size + 1
                            group.avatar = avt
                            group.conversationId = data.getData()!!.conversationId
                            group.type = AppConstants.TYPE_GROUP
                            group.conversationId = data.getData()!!.conversationId

                            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
                            intent.putExtras(bundle)

                            startActivity(intent)

                            postDelayed({
                                finish()
                            }, 500)
                        } catch (e: Exception) {
                            Timber.e(e.message)
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
                        } ${throwable }"
                    )
                    hideDialog()
                }
            })
    }

    private fun regexGroupName(groupName: String): String {
        var newGroupName = groupName.substring(0, 40)
        Timber.d(newGroupName.length.toString())
        val lastWord = getLastWord(newGroupName)
        if (!checkLastWordValid(
                lastWord,
                groupName
            )
        ) {//Kiểm tra từ cuối có phải là từ trọn vẹn ko(VD: "Nguyễn" nhưng từ cuối chỉ là "Ngu"). Nếu ko là từ trọn vẹn từ cắt từ đó đi luôn
            val startPosition = newGroupName.length - lastWord.length
            newGroupName = newGroupName.removeRange(startPosition until newGroupName.length)
        }
        return newGroupName
    }

    private fun getLastWord(input: String): String {
        val words = input.trim().split("\\s+".toRegex())
        return words.last()
    }

    private fun checkLastWordValid(lastWord: String, groupName: String): Boolean {
        val words = groupName.trim().split("\\s+".toRegex())
        return words.contains(lastWord)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun search() {
        binding.itemViewTop.svFriend.queryAfterTextChangedSV(delay = 500) {
            friendList.clear()
            myFriendAdapter!!.notifyDataSetChanged()
            keySearch = it
            getFriend(if (btnThree != 1) 0 else 1, "")
        }

    }

    private fun selected() {
        if (btnTwo == 1) {
            binding.itemViewTop.tvLately.setBackgroundResource(R.drawable.bg_form_input_on)
            binding.itemViewTop.tvLately.setTextColor(
                resources.getColor(
                    R.color.white, null
                )
            )
        } else {
            binding.itemViewTop.tvLately.setBackgroundResource(R.drawable.bg_search)
            binding.itemViewTop.tvLately.setTextColor(
                resources.getColor(
                    R.color.main_bg, null
                )
            )
        }
        if (btnThree == 1) {
            binding.itemViewTop.tvPhoneBook.setBackgroundResource(R.drawable.bg_form_input_on)
            binding.itemViewTop.tvPhoneBook.setTextColor(
                resources.getColor(
                    R.color.white, null
                )
            )
        } else {
            binding.itemViewTop.tvPhoneBook.setBackgroundResource(R.drawable.bg_search)
            binding.itemViewTop.tvPhoneBook.setTextColor(
                resources.getColor(
                    R.color.main_bg, null
                )
            )
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun checkChooseItem(array: ArrayList<String>) {
        if (array.size == 0) {
            binding.lnBottomView.hide()
            chooseFriendList.clear()
            member.clear()
        } else {
            binding.lnBottomView.show()
            if (array.size == 1) {
                binding.ibCreate.setBackgroundResource(R.drawable.border_gray_50dp)
                binding.ibCreate.isEnabled = false
            } else {
                binding.ibCreate.setBackgroundResource(R.drawable.border_blue_50dp)
                binding.ibCreate.isEnabled = true
            }
        }

    }


}