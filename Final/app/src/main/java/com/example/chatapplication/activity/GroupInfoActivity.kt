package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.adapter.MemberGroupInfoAdapter
import com.example.chatapplication.api.DetailGroupApi
import com.example.chatapplication.api.GetGroupInfoFromLinkGroupApi
import com.example.chatapplication.api.JoinLinkGroupApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.databinding.ActivityGroupInfoBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.GroupInfo
import com.example.chatapplication.model.entity.JoinGroupData
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.PhotoLoadUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import timber.log.Timber



class GroupInfoActivity : AppActivity() {
    private lateinit var binding: ActivityGroupInfoBinding
    private var listMember = ArrayList<User>()
    private var adapter: MemberGroupInfoAdapter? = null
    private var linkJoinGroup = ""

    override fun getLayoutView(): View {
        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.llHeader)
        adapter = MemberGroupInfoAdapter(this)
        adapter?.setData(listMember)
        AppUtils.initRecyclerViewHorizontal(binding.rcvListMember, adapter)
        try {
            linkJoinGroup = intent.getStringExtra(AppConstants.KEY_LINK_GROUP)!!
        } catch (e: Exception) {
            linkJoinGroup = ""
            toast(getString(R.string.empty_link_join_group))
        }
    }

    override fun initData() {
        if (linkJoinGroup.isNotEmpty()) {
            getGroupInfo()
            binding.btnJoinChat.setOnClickListener {
                binding.btnJoinChat.isEnabled = false
                joinGroup()
            }
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun joinGroup() {
        EasyHttp.post(this).api(
            JoinLinkGroupApi.params(linkJoinGroup)
        ).request(object : HttpCallbackProxy<HttpData<JoinGroupData>>(this) {
            override fun onHttpStart(call: Call?) {

            }

            override fun onHttpFail(throwable: Throwable?) {
                Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${throwable}")
                hideDialog()
                binding.btnJoinChat.isEnabled = true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onHttpSuccess(data: HttpData<JoinGroupData>) {
                if (data.isRequestSucceed()) {
                    if (data.getData() != null) {
                        getDetailGroup(data.getData()!!.conversationId)
                    }
                } else {
                    
                    Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${data.getMessage()}")
                    hideDialog()
                    binding.btnJoinChat.isEnabled = true
                }
            }
        })
    }

    /**
     * api lấy chi tiết cuộc trò chuyện
     */
    private fun getDetailGroup(idGroup: String) {
        EasyHttp.get(this).api(DetailGroupApi.params(idGroup))
            .request(object : HttpCallbackProxy<HttpData<GroupChat>>(this) {
                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpFail(throwable: Throwable?) {
                    Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${throwable}")
                    hideDialog()
                    binding.btnJoinChat.isEnabled = true
                }

                override fun onHttpSuccess(data: HttpData<GroupChat>) {
                    if (data.isRequestSucceed()) {
                        try {
                            val group = data.getData()
                            val bundle = Bundle()
                            val intent = Intent(
                                this@GroupInfoActivity,
                                ConversationDetailActivity::class.java
                            )
                            group!!.id = group!!.conversationId
                            bundle.putString(
                                ChatConstants.GROUP_DATA, Gson().toJson(group)
                            )
                            intent.putExtras(bundle)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Timber.e(e.message)
                        }
                    } else {
                        
                        Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${data.getMessage()}")
                        hideDialog()
                    }
                }
            })
    }

    private fun getGroupInfo() {
        EasyHttp.get(this).api(
            GetGroupInfoFromLinkGroupApi.params(linkJoinGroup)
        ).request(object : HttpCallbackProxy<HttpData<GroupInfo>>(this) {
            override fun onHttpStart(call: Call?) {

            }

            override fun onHttpFail(throwable: Throwable?) {
                Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${throwable}")
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onHttpSuccess(data: HttpData<GroupInfo>) {
                if (data.isRequestSucceed()) {
                    setDataGroupInfo(data.getData())
                } else {
                    Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${data.getMessage()}")
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataGroupInfo(data: GroupInfo?) {
        if (data != null) {
            PhotoLoadUtils.loadImageAvatarByGlide(binding.ivAvatarGroup, data.avatar)
            binding.GroupOwner.text = data.members.first().fullName
            binding.tvMemberCount.text = data.noOfMember.toString()
            listMember.clear()
            listMember.addAll(data.members)
            adapter!!.notifyDataSetChanged()
        }
    }


}