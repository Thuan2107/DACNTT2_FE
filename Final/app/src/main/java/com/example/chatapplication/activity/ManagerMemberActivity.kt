package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.databinding.ActivityManageMemberBinding
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.MemberList
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.example.chatapplication.adapter.GroupMemberAdapter
import com.example.chatapplication.adapter.MemberGroupAdapter
import com.example.chatapplication.api.CreateConversationApi
import com.example.chatapplication.api.GroupMemberApi
import com.example.chatapplication.api.RemoveMemberGroupApi
import com.example.chatapplication.api.SendRequestFriendApi
import com.example.chatapplication.api.UpdatePermissionApi
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.constant.SocketChatConstants
import com.example.chatapplication.dialog.DialogRemoveMember
import com.example.chatapplication.eventbus.SetContactTypeEventBus
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.model.entity.RemoveMember
import com.example.chatapplication.other.CustomLoadingListItemCreator
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.luck.picture.lib.thread.PictureThreadUtils
import com.paginate.Paginate
import io.socket.emitter.Emitter
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class ManagerMemberActivity : AppActivity(),
    GroupMemberAdapter.ManagerMemberListener,
    GroupMemberAdapter.RemoveMemberListener,
    MemberGroupAdapter.ContactListener {
    private lateinit var binding: ActivityManageMemberBinding
    private var group: GroupChat = GroupChat()
    var limit: Int = 20
    private var page: Int = 1
    private var position: Int = 0
    private var memberList: ArrayList<MemberList> = ArrayList()
    private var adapter: GroupMemberAdapter? = null
    private var loading: Boolean = false
    private var dialogRemoveMember: DialogRemoveMember.Builder? = null
    private var totalMember: Int = 0
    override fun getLayoutView(): View {
        binding = ActivityManageMemberBinding.inflate(layoutInflater)
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

    companion object {
    }

    override fun onResume() {
        super.onResume()
        getGroupMember(group.conversationId)

    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.tbHeader)
    }

    override fun initData() {
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(ChatConstants.GROUP_DATA)) {
                group = Gson().fromJson(
                    bundleIntent.getString(ChatConstants.GROUP_DATA), GroupChat::class.java
                )
            }
        }

        adapter = GroupMemberAdapter(this)
        AppUtils.initRecyclerViewVertical(binding.rcvMember, adapter)
        adapter!!.setUserListener(this)
        adapter!!.setRemoveMember(this)
        adapter!!.setContactGroup(this)
        getGroupMember(group.conversationId)

        paginate()


        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ibAddMember.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(
                this, AddMemberGroupActivity::class.java
            )
            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private val onChangePermission =
        Emitter.Listener { args: Array<Any> ->
            Thread {
                PictureThreadUtils.runOnUiThread {
                    Timber.e("Socket ChangPermission %s", Gson().toJson(args[0]))
                    try {
                        val intent = Intent(
                            this,
                           ConversationDetailActivity::class.java
                        )
                        val bundle = Bundle()
                        bundle.putString(
                            AppConstants.GROUP_DATA, Gson().toJson(group)
                        )
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Timber.e(e.message)
                    }
                }
            }.start()
        }

    private fun getGroupMember(id: String) {
        EasyHttp.get(this).api(
            GroupMemberApi.params(
                id
            )
        )
            .request(object : HttpCallbackProxy<HttpData<ArrayList<MemberList>>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpSuccess(data: HttpData<ArrayList<MemberList>>) {
                    if (data.isRequestSucceed()) {
                        memberList.clear()
                        memberList.addAll(data.getData()!!)
                        binding.shimmerGroupContainer.show()
                        binding.shimmerGroupContainer.startShimmer()
                        binding.shimmerGroupContainer.hide()
                        binding.llData.hide()

                        binding.shimmerGroupContainer.hide()
                        binding.shimmerGroupContainer.stopShimmer()
                        binding.llData.show()
                        totalMember = memberList.size
                        binding.tvCountMember.text =
                            String.format("%s (%s)", getString(R.string.member), memberList.size)
                        adapter!!.setData(memberList)
                    }
                }
            })
    }

    private fun paginate() {

        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                postDelayed({
                    if (page < memberList.size) {
                        page += 1
                        getGroupMember(group.conversationId)
                        loading = false
                    }
                }, 200)
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                if (memberList.size < limit)
                    return true
                return page == memberList.size

            }

        }

        Paginate.with(binding.rcvMember, callback)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(CustomLoadingListItemCreator())
            .build()
    }

    private fun callAddFriend(id: String) {
        EasyHttp.post(this).api(SendRequestFriendApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (!data.isRequestSucceed()) {

                        Timber.e(
                            "${
                                AppApplication.applicationContext()
                                    .getString(R.string.error_message)
                            } ${data.getMessage()}"
                        )
                        hideDialog()
                    } else {
                        EventBus.getDefault()
                            .post(SetContactTypeEventBus(AppConstants.WAITING_RESPONSE))
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

    fun onRemoveMember(idUser: String) {
        EasyHttp.post(this)
            .api(RemoveMemberGroupApi.params(group.conversationId, idUser))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empy
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

                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        //empty
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
            })
    }

    //thêm quyền phó nhóm
    private fun onAddDeputyMember(idUser: String) {
        EasyHttp.post(this)
            .api(
                UpdatePermissionApi.params(
                    group.conversationId,
                    idUser,
                    ConversationTypeConstants.PERMISSION_DEPUTY
                )
            )
            .request(object : HttpCallbackProxy<HttpData<ArrayList<Friend>>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpSuccess(data: HttpData<ArrayList<Friend>>) {
                    if (data.isRequestSucceed()) {
                        getGroupMember(group.conversationId)
                    } else {
                        toast(data.getData())
                    }

                }
            })
    }

    // bỏ quyền phó nhóm
    fun onRemoveDeputyMember(idUser: String) {
        EasyHttp.post(this).api(
            UpdatePermissionApi.params(
                group.conversationId,
                idUser,
                ConversationTypeConstants.PERMISSION_MEMBER
            )
        )
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empy
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

                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        getGroupMember(group.conversationId)
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
            })
    }

    private fun createGroup(item: MemberList) {
        EasyHttp.post(this)
            .api(
                CreateConversationApi.params(item.userId)
            )
            .request(object : HttpCallbackProxy<HttpData<GroupId>>(this) {
                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empy
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

                @SuppressLint("IntentWithNullActionLaunch")
                override fun onHttpSuccess(data: HttpData<GroupId>) {
                    if (data.isRequestSucceed()) {
                        try {
                            val bundle = Bundle()
                            val intent = Intent(
                                this@ManagerMemberActivity,
                                ConversationDetailActivity::class.java
                            )
                            val group = GroupChat()
                            with(group) {
                                conversationId = data.getData()!!.conversationId
                                type = AppConstants.TYPE_PRIVATE
                                name = item.fullName
                                avatar = item.avatar
                            }
                            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
                            intent.putExtras(bundle)
                            startActivity(intent)
                            finish()
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
            })
    }


    override fun clickMember(permission: Int, id: Int, position: Int, data: MemberList) {
        TODO("Not yet implemented")
    }

    override fun onRemoveMember(position: Int, idUser: String, permission: Int, data: MemberList) {
        if (idUser != UserCache.getUser().id) {
            dialogRemoveMember = DialogRemoveMember.Builder(
                this,
                permission,
                group.myPermission,
                data
            ).setListener(object : DialogRemoveMember.ClickButton {
                @SuppressLint("NotifyDataSetChanged")
                override fun onReviewConfirm(num: Int) {
                    if (num == 0) {
                        totalMember -= 1
                        binding.tvCountMember.text =
                            String.format("%s (%s)", getString(R.string.member), totalMember)
                        val removeMember = RemoveMember()
                        removeMember.conversationId = group.conversationId
                        removeMember.memberId = idUser
                        onRemoveMember(idUser)
                        memberList.removeAt(position)
                        adapter!!.notifyDataSetChanged()
                        dialogRemoveMember!!.dismiss()
                    } else if (num == 1) {
                        if (idUser == UserCache.getUser().id) {
                            finish()
                            startActivity(HomeActivity::class.java)
                        } else {
//                            val intent = Intent(
//                                this,
//                                Class.forName(ModuleClassConstants.INFO_CUSTOMER)
//                            )
//                            val bundle = Bundle()
//                            bundle.putInt(AccountConstants.ID, idUser)
//                            intent.putExtras(bundle)
//                            startActivity(intent)
                        }

                    } else if (num == 2) {
                        onAddDeputyMember(idUser)
                    } else if (num == 3) {
                        onRemoveDeputyMember(idUser)
                    } else if (num == 4) {
                        memberList.removeAt(position)
                        adapter!!.notifyDataSetChanged()
                        dialogRemoveMember!!.dismiss()
                    } else {
                        createGroup(data)
                    }

                    dialogRemoveMember!!.dismiss()
                }
            })
            dialogRemoveMember!!.show()
        } else {
            try {
//                val intent = Intent(
//                    context, Class.forName(ModuleClassConstants.INFO_CUSTOMER)
//                )
//                intent.putExtra(AppConstants.ID_USER, idUser)
//                startActivity(intent)

            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }

    override fun onAddFriendChat(idUser: String, contactType: Int, position: Int) {
        if (contactType == 1) {
            for (i in memberList.indices) {
                memberList[position].contactType =
                    AppConstants.WAITING_RESPONSE
            }
            adapter!!.notifyItemChanged(position)
            callAddFriend(idUser)
        }
    }


}