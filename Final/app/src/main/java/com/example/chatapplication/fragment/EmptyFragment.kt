package com.example.chatapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.activity.ConversationDetailActivity
import com.example.chatapplication.activity.HomeActivity
import com.example.chatapplication.activity.PeopleInformationActivity
import com.example.chatapplication.activity.RequestFriendActivity
import com.example.chatapplication.adapter.ContactAdapter
import com.example.chatapplication.api.AcceptAddFriendApi
import com.example.chatapplication.api.CreateConversationApi
import com.example.chatapplication.api.GetListFriend
import com.example.chatapplication.api.NotAcceptFriendApi
import com.example.chatapplication.api.SendRequestFriendApi
import com.example.chatapplication.api.UnFriendApi
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.app.AppFragment
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.FragmentEmptyBinding
import com.example.chatapplication.databinding.FragmentPhoneBookBinding
import com.example.chatapplication.dialog.DialogFeedBack
import com.example.chatapplication.dialog.MoreDialog
import com.example.chatapplication.eventbus.RequestFriendEventBus
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Conversation
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class EmptyFragment : AppFragment<HomeActivity>(){
    private lateinit var binding: FragmentEmptyBinding


    companion object {

    }

    override fun getLayoutView(): View {
        binding = FragmentEmptyBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
    }
}