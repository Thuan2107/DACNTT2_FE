package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import com.example.chatapplication.R
import com.example.chatapplication.api.CreateConversationApi
import com.example.chatapplication.api.GetGroupInfoFromLinkGroupApi
import com.example.chatapplication.api.SearchUserApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.constant.InfoCustomerConstants
import com.example.chatapplication.databinding.ActivitySearchManagerBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.model.entity.GroupInfo
import com.example.chatapplication.model.entity.ProfileCustomerNodeData
import com.example.chatapplication.model.entity.SearchUser
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class SearchManagerActivity : AppActivity() {
    private lateinit var binding: ActivitySearchManagerBinding
    private var userFind = ProfileCustomerNodeData()
    private var isSearchGroup : Boolean ?= null

    companion object {
        var keySearch: String = ""
    }

    override fun getLayoutView(): View {
        binding = ActivitySearchManagerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.lnHeader)
    }

    override fun initData() {
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.JOIN_GROUP)) {
                isSearchGroup = bundleIntent.getBoolean(AppConstants.JOIN_GROUP)
            }
        }
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboardManager.hasPrimaryClip()) {
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val pasteData = clipData.getItemAt(0).text
                binding.svSearch.setQuery(pasteData, false)
            }
        }
        eventAction()
    }

    private fun eventAction() {

        binding.svSearch.requestFocus()
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.lnItemUser.show()
                if(isSearchGroup!!) getGroupInfoWithLink(query!!) else findUserByPhone(query!!)
                hideKeyboard(binding.svSearch)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.lnItemUser.hide()
                keySearch = newText ?: ""
                return true
            }
        })

        binding.btnAction.setOnClickListener {
            createGroup(userFind)
        }

        binding.lnItemUser.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(
                this@SearchManagerActivity,
                InfoCustomerActivity::class.java
            )
            bundle.putString(
                AppConstants.ID_USER, userFind.userId
            )
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    //-------------------------------------------API----------------------------------------------//
    private fun getGroupInfoWithLink(linkJoinGroup: String) {
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
                    val intent =
                        Intent(this@SearchManagerActivity, GroupInfoActivity::class.java)
                    intent.putExtra(AppConstants.KEY_LINK_GROUP, data.getData()!!.linkJoin)
                    startActivity(intent)
                } else {
                    Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${data.getMessage()}")
                }
            }
        })
    }

    private fun findUserByPhone(phone: String) {
        EasyHttp.get(this)
            .api(SearchUserApi.params(phone))
            .request(object : HttpCallbackProxy<HttpData<SearchUser>>(this) {
                override fun onHttpStart(call: Call?) {
                    //Empty
                }

                override fun onHttpFail(throwable: Throwable?) {
                    Timber.e(
                        "${
                            AppApplication.applicationContext()
                                .getString(R.string.error_message)
                        } ${throwable}"
                    )
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<SearchUser>) {
                    if (data.isRequestSucceed()) {
                        if(data.getData()!!.user != null){
                            binding.lnItemUser.show()
                            binding.lnEmpty.hide()
                            userFind = data.getData()!!.user
                            binding.tvUserName.text = userFind.fullName
                            when(userFind.contactType){
                                InfoCustomerConstants.ITS_ME -> {
                                    binding.tvContactType.visibility = View.GONE

                                }

                                InfoCustomerConstants.NOT_FRIEND -> {
                                    binding.tvContactType.text = "Người dùng"
                                }

                                InfoCustomerConstants.WAITING_CONFIRM -> {
                                    binding.tvContactType.text = "Chờ xác nhận kết bạn"
                                }

                                InfoCustomerConstants.WAITING_RESPONSE -> {
                                    binding.tvContactType.text = "Đã gửi lời mời kết bạn"
                                }

                                InfoCustomerConstants.FRIEND -> {
                                    binding.tvContactType.text = "Bạn bè"
                                }
                            }
                        }else{
                            binding.lnItemUser.hide()
                            binding.lnEmpty.show()
                        }

                    } else {
                        binding.lnEmpty.show()
                        binding.lnItemUser.hide()
                        Timber.e(
                            "${
                                AppApplication.applicationContext()
                                    .getString(R.string.error_message)
                            } ${data.getMessage()}"
                        )
                    }
                }
            })
    }

    private fun createGroup(user: ProfileCustomerNodeData) {
        EasyHttp.post(this).api(CreateConversationApi.params(user.userId))
            .request(object : HttpCallbackProxy<HttpData<GroupId>>(this) {
                @SuppressLint("IntentWithNullActionLaunch")
                override fun onHttpSuccess(data: HttpData<GroupId>) {
                    if (data.isRequestSucceed()) {
                        val bundle = Bundle()
                        val intent = Intent(this@SearchManagerActivity, ConversationDetailActivity::class.java)
                        val group = GroupChat()
                        with(group) {
                            conversationId = data.getData()!!.conversationId
                            type = AppConstants.TYPE_PRIVATE
                            name = user.fullName
                            avatar = user.avatar
                        }
                        bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()
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
                    hideDialog()
                    Timber.e(
                        "${
                            AppApplication.applicationContext()
                                .getString(R.string.error_message)
                        } ${throwable}"
                    )
                }

                override fun onHttpStart(call: Call?) {
                    showDialog()
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }
            })
    }



}