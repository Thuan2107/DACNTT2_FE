package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.api.CreateConversationApi
import com.example.chatapplication.api.RemoveRequestFriendApi
import com.example.chatapplication.api.SendRequestFriendApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.databinding.ActivityPeopleInformationBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import timber.log.Timber

class PeopleInformationActivity : AppActivity() {
    private lateinit var binding: ActivityPeopleInformationBinding
    private var user = User()


    override fun getLayoutView(): View {
        binding = ActivityPeopleInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.lnHeader)
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(ChatConstants.USER_DATA)) {
                user = Gson().fromJson(
                    bundleIntent.getString(ChatConstants.USER_DATA),
                    User::class.java
                )
            }
        }
    }



    override fun initData() {
        initUserInformation(user)
        eventAction()
    }

    private fun initUserInformation(user: User) {
        binding.tvName.text = user.fullName

    }

    private fun eventAction() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnSendMessage.setOnClickListener {
            createGroup(user)
        }

        binding.btnAddFriend.setOnClickListener {
            callAddFriend(user)
            binding.btnRemoveFriend.show()
            binding.btnAddFriend.hide()
        }

        binding.btnRemoveFriend.setOnClickListener {
            removeRequestFriend(user)
            binding.btnRemoveFriend.hide()
            binding.btnAddFriend.show()
        }



    }

    private fun createGroup(user: User) {
        EasyHttp.post(this).api(CreateConversationApi.params(user.id))
            .request(object : HttpCallbackProxy<HttpData<GroupId>>(this) {
                @SuppressLint("IntentWithNullActionLaunch")
                override fun onHttpSuccess(data: HttpData<GroupId>) {
                    if (data.isRequestSucceed()) {
                        val bundle = Bundle()
                        val intent = Intent(this@PeopleInformationActivity, ConversationDetailActivity::class.java)
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
    /**
     * Gọi api gửi lời mời kết bạn
     */
    private fun callAddFriend(user: User) {
        EasyHttp.post(this).api(SendRequestFriendApi.params(user.id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    toast("Đã gửi lời mời kết bạn đến ${user.fullName}")
                }
            })
    }
    /**
     * Gọi api thu hồi lời mời kết bạn
     */
    @SuppressLint("HardwareIds")
    private fun removeRequestFriend(user: User) {
        EasyHttp.post(this).api(RemoveRequestFriendApi.params(user.id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    toast("Đã hủy kết bạn với ${user.fullName}")
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empty
                }
            })
    }


}