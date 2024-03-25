package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.adapter.GroupMemberAdapter
import com.example.chatapplication.adapter.MemberGroupAdapter
import com.example.chatapplication.api.GroupMemberApi
import com.example.chatapplication.api.SendRequestFriendApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.databinding.ActivityListMemberBinding
import com.example.chatapplication.eventbus.EventbusAddMember
import com.example.chatapplication.eventbus.SetContactTypeEventBus
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.MemberList
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class ListMemberActivity : AppActivity(), GroupMemberAdapter.RemoveMemberListener,
    MemberGroupAdapter.ContactListener {
    private lateinit var binding: ActivityListMemberBinding
    var limit = 20
    var group = GroupChat()
    private var totalMember = 0
//    private var dialogRemoveMember: DialogRemoveMember.Builder? = null
    private var adapterMember: MemberGroupAdapter? = null
    private var memberList: ArrayList<MemberList> = ArrayList()
    override fun getLayoutView(): View {
        binding = ActivityListMemberBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        adapterMember = MemberGroupAdapter(this)
        ImmersionBar.setTitleBar(this, binding.itemViewTop.llHeader)
        AppUtils.initRecyclerViewVertical(binding.rcvListMember, adapterMember)
    }

    override fun initData() {
        binding.itemViewTop.ibBack.setOnClickListener {
            finish()
        }

        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(ChatConstants.GROUP_DATA)) {
                group = Gson().fromJson(
                    bundleIntent.getString(ChatConstants.GROUP_DATA), GroupChat::class.java
                )
                Timber.d("group list member %s", Gson().toJson(group))
            }
        }

        /**
         * Tên và thành viên group
         */
        binding.itemViewTop.tvGroupName.text = group.name

        adapterMember!!.setRemoveMember(this)
        adapterMember!!.setContactGroup(this)
        listMember(group.conversationId)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onListenAddGroup(event: EventbusAddMember?) {
        event?.let {
            it.member?.forEach { mb ->
                memberList.add(
                    MemberList(
                        mb.userId,
                        mb.fullName,
                        mb.avatar,
                        ConversationTypeConstants.PERMISSION_MEMBER.toInt(),
                        mb.contactType,
                    )
                )
            }
            totalMember = memberList.size

            binding.itemViewTop.tvStatus.text = String.format(
                getString(R.string.number_group_chat_content), totalMember
            )
            adapterMember?.notifyDataSetChanged()
        }
    }

    private fun listMember(id: String) {
        binding.llEmpty.hide()
        binding.sflMyFriend.show()
        binding.sflMyFriend.startShimmer()
        binding.rcvListMember.hide()
        EasyHttp.get(this).api(
            GroupMemberApi.params(id)
        )
            .request(object : HttpCallbackProxy<HttpData<ArrayList<MemberList>>>(this) {
                override fun onHttpStart(call: Call) {
                }

                override fun onHttpSuccess(data: HttpData<ArrayList<MemberList>>) {
                    if (data.isRequestSucceed()) {
                        binding.sflMyFriend.hide()
                        binding.sflMyFriend.stopShimmer()
                        binding.rcvListMember.show()
                        if (data.getData()!!.isEmpty()) {
                            binding.rcvListMember.hide()
                            binding.llEmpty.show()
                        } else {
                            binding.rcvListMember.show()
                            binding.llEmpty.hide()
                        }
                        totalMember = data.getData()!!.size

                        binding.itemViewTop.tvStatus.text = String.format(
                            getString(R.string.number_group_chat_content), data.getData()!!.size
                        )
                        memberList.addAll(data.getData()!!)
                        adapterMember!!.setData(memberList)
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
                        toast("Gửi lời mời kết bạn thành công")
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

    override fun onRemoveMember(position: Int, idUser: String, permission: Int, data: MemberList) {
//        if (idUser != UserCache.getUser().id) {
//            dialogRemoveMember = DialogRemoveMember.Builder(
//                this, permission, group.myPermission, data
//            ).setListener(object : DialogRemoveMember.ClickButton {
//                @SuppressLint("NotifyDataSetChanged")
//                override fun onReviewConfirm(num: Int) {
//                    if (num == 0) {
//                        totalMember -= 1
//                        binding.itemViewTop.tvStatus.text = String.format(
//                            getString(R.string.number_group_chat_content), totalMember
//                        )
//                        val removeMember = RemoveMember()
//                        removeMember.conversationId = group.conversationId
//                        removeMember.memberId = idUser
//                        ChatUtils.emitSocket(SocketChatConstants.EMIT_REMOVE_MEMBER, removeMember)
//                        memberList.removeAt(position)
//                        adapterMember!!.notifyDataSetChanged()
//                        dialogRemoveMember!!.dismiss()
//                    } else if (num == 1) {
//                        if (idUser == UserCache.getUser().id) {
//                            EventBus.getDefault().post(CurrentFragmentEventBus(4))
//                            startActivity(HomeActivity::class.java)
//                            finish()
//                        } else {
//                            val intent = Intent(
//                                this@ListMemberActivity,
//                                Class.forName(ModuleClassConstants.INFO_CUSTOMER)
//                            )
//                            val bundle = Bundle()
//                            bundle.putInt(AccountConstants.ID, idUser)
//                            intent.putExtras(bundle)
//                            startActivity(intent)
//                        }
//
//                    } else if (num == 2) {
//                        val changePermission = ChangePermission()
//                        changePermission.permission = 1
//                        changePermission.conversationId = group.conversationId
//                        changePermission.memberId = idUser
//                        ChatUtils.emitSocket(
//                            SocketChatConstants.EMIT_UPDATE_PERMISSION,
//                            changePermission
//                        )
//                        finish()
//                        startActivity(DetailChatActivity::class.java)
//                    } else {
//                        val changePermission = ChangePermission()
//                        changePermission.permission = 0
//                        changePermission.conversationId = group.conversationId
//                        changePermission.memberId = idUser
//                        ChatUtils.emitSocket(
//                            SocketChatConstants.EMIT_UPDATE_PERMISSION,
//                            changePermission
//                        )
//                        finish()
//                        startActivity(DetailChatActivity::class.java)
//                    }
//                    dialogRemoveMember!!.dismiss()
//                }
//            })
//            dialogRemoveMember!!.show()
//        } else {
//            try {
//                val intent = Intent(
//                    this, Class.forName(ModuleClassConstants.INFO_CUSTOMER)
//                )
//                intent.putExtra(AppConstants.ID_USER, idUser)
//                startActivity(intent)
//
//            } catch (e: Exception) {
//                Timber.e(e.message)
//            }
//        }
    }

    override fun onAddFriendChat(idUser: String, contactType: Int, position: Int) {
        if (contactType == 1) {
            for (i in memberList.indices) {
                memberList[position].contactType =
                    AppConstants.WAITING_RESPONSE
            }
            adapterMember!!.notifyItemChanged(position)
            callAddFriend(idUser)
        }
    }

}