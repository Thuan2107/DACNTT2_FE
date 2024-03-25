package com.example.chatapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.activity.ConversationDetailActivity
import com.example.chatapplication.activity.HomeActivity
import com.example.chatapplication.activity.InfoCustomerActivity
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
import com.example.chatapplication.constant.ChatConstants
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

class PhoneBookFragment : AppFragment<HomeActivity>(), BaseAdapter.OnItemClickListener,
    ContactAdapter.MyFriendInterface,
    ContactAdapter.AddFriendInterface{
    private lateinit var binding: FragmentPhoneBookBinding
    private var dataList: ArrayList<Friend> = ArrayList()
    private var adapterMyContact: ContactAdapter? = null
    private var dialogMore: MoreDialog.Builder? = null
    private var dialogFeedBack: DialogFeedBack.Builder? = null

    companion object {

    }

    override fun getLayoutView(): View {
        binding = FragmentPhoneBookBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {
        adapterMyContact = ContactAdapter(requireActivity())
        adapterMyContact?.setMyFriendInterFace(this)
        adapterMyContact?.setOnItemClickListener(this)
        adapterMyContact?.setOnClickAddFriend(this)
        adapterMyContact?.setOnClickMore(this)
        adapterMyContact?.setData(dataList)
        AppUtils.initRecyclerViewVertical(binding.itemRcv.recyclerViewMyFriend, adapterMyContact)


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {
        binding.lnListRequestFriend.setOnClickListener {
            try {
                val intent = Intent(
                    requireContext(),
                    RequestFriendActivity::class.java
                )
                startActivity(intent)
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getListFriend()

    }

    private fun getListFriend() {
        binding.itemRcv.lnEmpty.hide()
        binding.itemRcv.recyclerViewMyFriend.hide()

        EasyHttp.get(this).api(GetListFriend.params(AppConstants.FRIEND))
            .request(object : HttpCallbackProxy<HttpData<ArrayList<Friend>>>(this) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<ArrayList<Friend>>) {
                    if (data.isRequestSucceed()) {
                        dataList.clear()
                        if(data.getData()!!.isNotEmpty()){
                            binding.itemRcv.recyclerViewMyFriend.show()
                            dataList.addAll(data.getData()!!)
                            adapterMyContact!!.notifyDataSetChanged()
                            binding.itemRcv.lnEmpty.hide()
                        }else{
                            binding.itemRcv.recyclerViewMyFriend.hide()
                            binding.itemRcv.lnEmpty.show()
                        }
                    }
                }
            })
    }

    /**
     * Tạo cuộc trò chuyện 1-1
     */
    private fun createGroup(position: Int) {
        EasyHttp.post(this).api(CreateConversationApi.params(dataList[position].userId))
            .request(object : HttpCallbackProxy<HttpData<GroupId>>(this) {
                @SuppressLint("IntentWithNullActionLaunch")
                override fun onHttpSuccess(data: HttpData<GroupId>) {
                    if (data.isRequestSucceed()) {
                        val bundle = Bundle()
                        val intent = Intent(requireContext(), ConversationDetailActivity::class.java)
                        val group = GroupChat()
                        with(group) {
                            conversationId = data.getData()!!.conversationId
                            id = data.getData()!!.conversationId
                            type = AppConstants.TYPE_PRIVATE
                            name = dataList[position].fullName
                            avatar = dataList[position].avatar
                        }
                        Timber.d("data create group %s", Gson().toJson(group))
                        bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(group))
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
     * gọi api huỷ kết bạn :vvvvvv
     */
    private fun unFriend(id: String) {
        EasyHttp.post(this).api(UnFriendApi.params(id))
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
                        val index = dataList.indexOfFirst { it.userId == id }
                        if(index != -1){
                            dataList.removeAt(index)
                            adapterMyContact!!.notifyDataSetChanged()
                            toast("Xóa kết bạn thành công")
                            if(dataList.isNotEmpty()){
                                binding.itemRcv.recyclerViewMyFriend.show()
                                binding.itemRcv.lnEmpty.hide()
                            }else{
                                binding.itemRcv.recyclerViewMyFriend.hide()
                                binding.itemRcv.lnEmpty.show()
                            }
                        }
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

                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }
            })
    }

    /**
     * gọi api kết bạn :vvvvvv
     */

    private fun callAddFriend(id: String) {
        EasyHttp.post(this).api(SendRequestFriendApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (!data.isRequestSucceed()) {
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

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empty
                }
            })
    }

    /**
     * Gọi api đồng ý kết bạn
     */
    private fun acceptAddFriend(id: String) {
        EasyHttp.post(this)
            .api(AcceptAddFriendApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
//                        EventBus.getDefault().post(RequestEventBus(--totalRequest))
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

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empty
                }
            })
    }

    private fun dialogFeedBack(id: String, position: Int) {
        dialogFeedBack =
            DialogFeedBack.Builder(requireContext()).setListener(object : DialogFeedBack.ClickAddFriend {
                @SuppressLint("NotifyDataSetChanged")
                override fun onClickAddFriend(type: Int) {
                    if (type == 1) {
                        acceptAddFriend(id)
                        dataList[position].contactType = AppConstants.FRIEND
                        adapterMyContact!!.notifyDataSetChanged()
                    } else {
                        EasyHttp.post(requireActivity()).api(NotAcceptFriendApi.params(id))
                            .request(object : HttpCallbackProxy<HttpData<Any>>(this@PhoneBookFragment) {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onHttpSuccess(data: HttpData<Any>) {
                                    if (data.isRequestSucceed()) {
                                        dataList[position].contactType = AppConstants.NOT_FRIEND
                                        adapterMyContact!!.notifyDataSetChanged()
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

                                override fun onHttpEnd(call: Call?) {
                                    //empty
                                }

                                override fun onHttpStart(call: Call?) {
                                    //empty
                                }
                            })
                    }

                }
            })
        dialogFeedBack!!.show()
    }

    override fun clickMore(item: Friend, position: Int) {
        dialogMore = MoreDialog.Builder(requireContext(), item, 1).setListener(object : MoreDialog.Builder.OnListener {
            override fun onUnFriendUser(idUser: String) {
                unFriend(idUser)
                dataList[position].contactType = AppConstants.NOT_FRIEND
            }
        })
        dialogMore!!.show()
    }

    override fun clickWall(position: Int) {
        val bundle = Bundle()
        val intent = Intent(
            requireContext(),
            InfoCustomerActivity::class.java
        )
        bundle.putString(
            AppConstants.ID_USER, dataList[position].userId)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun clickMessage(position: Int) {
        createGroup(position)
    }

    override fun clickAvatar(position: Int) {
        AppUtils.showMediaAvatar(requireContext(), dataList[position].avatar, 0)
    }

    override fun clickAddFriend(id: String, position: Int) {
        callAddFriend(id)
        dataList[position].contactType = AppConstants.WAITING_RESPONSE
    }

    override fun clickFeedBack(id: String, position: Int) {
        dialogFeedBack(id, position)
    }

    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        createGroup(position)
    }

//    @SuppressLint("NotifyDataSetChanged")
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: RequestFriendEventBus?) {
//        event?.let {
//            if (it.isAccept == 1) {
//                postDelayed({
//                    getListFriend()
//                }, 1000)
//                CreateConversationApi(it.userId)
//            }
//        }
//    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        EventBus.getDefault().unregister(this)
//    }
}