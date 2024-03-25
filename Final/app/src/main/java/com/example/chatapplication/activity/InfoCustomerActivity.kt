package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.chatapplication.model.HttpData
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.adapter.MyFriendAdapter
import com.example.chatapplication.api.AcceptAddFriendApi
import com.example.chatapplication.api.CreateConversationApi
import com.example.chatapplication.api.DetailGroupApi
import com.example.chatapplication.api.NotAcceptFriendApi
import com.example.chatapplication.api.ProFileCustomerApi
import com.example.chatapplication.api.RemoveRequestFriendApi
import com.example.chatapplication.api.SendRequestFriendApi
import com.example.chatapplication.api.UnFriendApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.InfoCustomerConstants
import com.example.chatapplication.databinding.ActivityInfoCustomerBinding
import com.example.chatapplication.dialog.DialogFeedBack
import com.example.chatapplication.dialog.FriendActionDialog
import com.example.chatapplication.eventbus.ContactEventBus
import com.example.chatapplication.eventbus.CurrentFragmentEventBus
import com.example.chatapplication.eventbus.SetContactTypeEventBus
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.model.entity.ProfileCustomerNodeData
import com.example.chatapplication.model.entity.SearchUser
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.google.android.exoplayer2.util.Log
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.net.URLDecoder


class InfoCustomerActivity : AppActivity(),
    BaseAdapter.OnItemClickListener,
    BaseDialog.OnCancelListener{

    private lateinit var binding: ActivityInfoCustomerBinding
    private var id = ""
    var dataProfile: ProfileCustomerNodeData = ProfileCustomerNodeData()
    private var dialogFriendAction: FriendActionDialog.Builder? = null
    private var friendDataList: ArrayList<Friend> = ArrayList()
    private var user: Friend = Friend()
    private var isLoadMore: Boolean = true
    private var loading: Boolean = false
    private var limit: Int = 5
    private var dialogFeedBack: DialogFeedBack.Builder? = null

    override fun getLayoutView(): View {
        binding = ActivityInfoCustomerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(
            R.anim.window_ios_in,
            R.anim.window_ios_out
        )
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
            R.anim.window_ios_in,
            R.anim.window_ios_out
        )
    }

    override fun onStop() {
        super.onStop()
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.profileHeader.tbHeader)
    }


    @SuppressLint("IntentWithNullActionLaunch")
    override fun initData() {
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.ID_USER)) {
                id = bundleIntent.getString(AppConstants.ID_USER, "")
                Log.d("id", id)
            }
        }
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.ID)) {
                id = bundleIntent.getString(AppConstants.ID, "")
            }
        }
        infoCustomer()
        binding.profileHeader.ibExitInfoCustomer.setOnClickListener {
            finish()
        }

//        binding.profileInfo.tvDetailCustomer.setOnClickListener {
//            try {
//                val intent = Intent(
//                    this, Class.forName(ModuleClassConstants.DETAIL_PROFILE)
//                )
//                val bundle = Bundle()
//                bundle.putString(
//                    InfoCustomerConstants.DATA_CUSTOMER, Gson().toJson(dataProfile)
//                )
//                intent.putExtras(bundle)
//
//                startActivity(intent)
//            } catch (e: Exception) {
//                Timber.e(e.message)
//            }
//        }
        binding.profileInfo.tvEditProfile.setOnClickListener {
            try {
                val intent = Intent(
                    this@InfoCustomerActivity, EditProfileActivity::class.java
                )
                startActivity(intent)
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }


        setOnClickListener(
            binding.lnMakeFriend,
            binding.rltAvatar,
            binding.llFeedBack,
            binding.llRecall,
            binding.llFriend,
            binding.lnMessage
        )

        binding.profileHeader.btnSearch.setOnClickListener {
            AppUtils.disableClickAction(binding.profileHeader.btnSearch, 1000)
            val intent = Intent(this, SearchManagerActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("IntentWithNullActionLaunch")
    override fun onClick(view: View) {
        when (view) {
            binding.rltAvatar -> {

            }

            binding.lnMakeFriend -> {
                binding.lnMakeFriend.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.lnMakeFriend.isEnabled = true
                }, 1000)
                callAddFriend()
            }

            binding.llRecall -> {
                binding.llRecall.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.llRecall.isEnabled = true
                }, 1000)
                removeRequestFriend(dataProfile.userId)
            }

            binding.llFeedBack -> {
                dialogFeedBack()
            }

            binding.llFriend -> {
                binding.llFriend.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.llFriend.isEnabled = true
                }, 1000)
                dialogFriendAction()
            }

            binding.lnMessage -> {
                createGroup(dataProfile.userId)
            }
        }
    }

    private fun infoCustomer() {
        EasyHttp.get(this).api(ProFileCustomerApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<SearchUser>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpSuccess(data: HttpData<SearchUser>) {
                    if (data.isRequestSucceed()) {
                        data.getData()?.let {
                            binding.sflProfile.hide()
                            binding.sflProfile.stopShimmer()
                            binding.lnProfile.show()
                            binding.llNewsFeed.show()
                            binding.NestedSV.show()
                            dataProfile = it.user
                            if (dataProfile.isDisplayNickName == 0) {
                                binding.tvNameUser.text = dataProfile.fullName
                            } else {
                                binding.tvNameUser.text =
                                    String.format(
                                        "%s (%s)",
                                        dataProfile.fullName,
                                        dataProfile.nickName
                                    )
                            }
                            PhotoLoadUtils.loadImageAvatarByGlide(
                                binding.ivAvatar, dataProfile.avatar
                            )
                            if (dataProfile.identification == 1) {
                                binding.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(
                                    0, 0, R.drawable.ic_tick, 0
                                )
                            } else {
                                binding.tvNameUser.setCompoundDrawablesWithIntrinsicBounds(
                                    0, 0, 0, 0
                                )
                            }
                            user.userId = dataProfile.userId
                            user.fullName = dataProfile.fullName
                            user.contactType = dataProfile.contactType
                            checkData()
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

    private fun dialogFriendAction() {
        dialogFriendAction = FriendActionDialog.Builder(this, user)
            .setListener(object : FriendActionDialog.OnListener {
                override fun onUnFriendUser(idUser: String) {
                    unFriend(idUser)
                }
            })
        dialogFriendAction!!.show()
    }

    private fun dialogFeedBack() {
        dialogFeedBack =
            DialogFeedBack.Builder(this).setListener(object : DialogFeedBack.ClickAddFriend {
                override fun onClickAddFriend(type: Int) {
                    if (type == 1) {
                        EasyHttp.post(this@InfoCustomerActivity).api(AcceptAddFriendApi.params(id!!))
                            .request(object :
                                HttpCallbackProxy<HttpData<Any>>(this@InfoCustomerActivity) {
                                override fun onHttpStart(call: Call?) {
                                    //empty
                                }

                                override fun onHttpEnd(call: Call?) {
                                    //empty
                                }

                                override fun onHttpSuccess(data: HttpData<Any>) {
                                    if (data.isRequestSucceed()) {
                                        binding.lnMakeFriend.hide()
                                        binding.llFriend.show()
                                        binding.llFeedBack.hide()
                                        binding.llRecall.hide()
                                        EventBus.getDefault()
                                            .post(SetContactTypeEventBus(AppConstants.FRIEND))
                                        dataProfile.contactType = AppConstants.FRIEND
                                        user.contactType = AppConstants.FRIEND
                                    } else toast(
                                        data.getMessage()
                                    )
                                }
                            })
                    } else {
                        EasyHttp.post(this@InfoCustomerActivity).api(NotAcceptFriendApi.params(id!!))
                            .request(object :
                                HttpCallbackProxy<HttpData<Any>>(this@InfoCustomerActivity) {
                                override fun onHttpStart(call: Call?) {
                                    //empty
                                }

                                override fun onHttpEnd(call: Call?) {
                                    //empty
                                }


                                override fun onHttpSuccess(data: HttpData<Any>) {
                                    if (data.isRequestSucceed()) {
                                        binding.lnMakeFriend.show()
                                        binding.llFriend.hide()
                                        binding.llFeedBack.hide()
                                        binding.llRecall.hide()
                                        EventBus.getDefault()
                                            .post(SetContactTypeEventBus(AppConstants.NOT_FRIEND))
                                        dataProfile.contactType = AppConstants.NOT_FRIEND
                                        user.contactType = AppConstants.NOT_FRIEND
                                    } else toast(
                                        data.getMessage()
                                    )
                                }
                            })
                    }

                }
            })
        dialogFeedBack!!.show()
    }


    /**
     * Gọi api thu hồi lời mời kết bạn
     */
    @SuppressLint("HardwareIds")
    private fun removeRequestFriend(id: String) {
        EasyHttp.post(this).api(RemoveRequestFriendApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }


                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        binding.lnMakeFriend.show()
                        binding.llFriend.hide()
                        binding.llFeedBack.hide()
                        binding.llRecall.hide()
                        EventBus.getDefault().post(SetContactTypeEventBus(AppConstants.NOT_FRIEND))
                        dataProfile.contactType = AppConstants.NOT_FRIEND
                        user.contactType = AppConstants.NOT_FRIEND
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
     * gọi api huỷ kết bạn :vvvvvv
     */
    private fun unFriend(idUser: String) {
        EasyHttp.post(this).api(UnFriendApi.params(idUser))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }


                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        EventBus.getDefault().post(SetContactTypeEventBus(AppConstants.NOT_FRIEND))
                        dataProfile.contactType = AppConstants.NOT_FRIEND
                        user.contactType = AppConstants.NOT_FRIEND
                    }
                }
            })
    }

    /**
     * api gửi lời mời kết bạn
     */
    private fun callAddFriend() {
        EasyHttp.post(this).api(SendRequestFriendApi.params(id!!))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpFail(throwable: Throwable?) {

                    Timber.e(
                        "${
                            AppApplication.applicationContext()
                                .getString(R.string.error_message)
                        } ${throwable}"
                    )
                }

                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (!data.isRequestSucceed()) {
                        Timber.e(
                            "${
                                AppApplication.applicationContext()
                                    .getString(R.string.error_message)
                            } ${data.getMessage()}"
                        )
                    } else {
                        binding.lnMakeFriend.hide()
                        binding.llFriend.hide()
                        binding.llFeedBack.hide()
                        binding.llRecall.show()
                        EventBus.getDefault()
                            .post(SetContactTypeEventBus(AppConstants.WAITING_RESPONSE))
                        dataProfile.contactType = AppConstants.WAITING_RESPONSE
                        user.contactType = AppConstants.WAITING_RESPONSE
//                        EventBus.getDefault().post(RemoveRequestFriendEventBus(id))
                    }
                }
            })
    }

    /**
     * api tạo cuộc trò chuyện
     */
    private fun createGroup(memberId: String) {
        EasyHttp.post(this).api(CreateConversationApi.params(memberId))
            .request(object : HttpCallbackProxy<HttpData<GroupId>>(this) {
                @SuppressLint("IntentWithNullActionLaunch")
                override fun onHttpSuccess(data: HttpData<GroupId>) {
                    if (data.isRequestSucceed()) {
                        try {
                            val bundle = Bundle()
                            val intent = Intent(
                                this@InfoCustomerActivity,
                                ConversationDetailActivity::class.java
                            )
                            val group = GroupChat()
                            with(group) {
                                conversationId = data.getData()!!.conversationId
                                type = AppConstants.TYPE_PRIVATE
                                name = dataProfile.fullName
                                avatar = dataProfile.avatar
                            }
                            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
                            intent.putExtras(bundle)
                            startActivity(intent)
                            finish()
                        } catch (e: Exception) {
                            Timber.e(e.message)
                        }
                    }

                }

                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

            })
    }



    override fun onCancel(dialog: BaseDialog?) {
        //
    }

    @SuppressLint("SuspiciousIndentation")
    fun checkData() {

        var address = dataProfile.address


//        if (dataProfile.isEnableAddress == 0) {
//            binding.profileInfo.tvAddress.text = getString(R.string.off_text)
//
//        } else {
            if (address.isEmpty()) binding.profileInfo.tvAddress.text =
                getString(R.string.not_update)
            else
                binding.profileInfo.tvAddress.text = address
//        }

//        if (dataProfile.isEnableEmail == InfoCustomerConstants.DISABLE) {
//            binding.profileInfo.tvEmail.text = getString(R.string.off_text)
//        } else {
            if (dataProfile.email.isEmpty()) binding.profileInfo.tvEmail.text =
                getString(R.string.not_update)
            else binding.profileInfo.tvEmail.text = dataProfile.email
//        }

//        if (dataProfile.isEnablePhone == InfoCustomerConstants.DISABLE) {
//            binding.profileInfo.tvPhone.text = getString(R.string.off_text)
//        } else {
            if (dataProfile.phone.isEmpty()) binding.profileInfo.tvPhone.text =
                getString(R.string.not_update)
            else binding.profileInfo.tvPhone.text = dataProfile.phone
//        }

//        if (dataProfile.isEnableBirthday == InfoCustomerConstants.DISABLE) {
//            binding.profileInfo.tvDate.text = getString(R.string.off_text)
//        } else {
            if (dataProfile.birthday.isEmpty()) binding.profileInfo.tvDate.text =
                getString(R.string.not_update)
            else binding.profileInfo.tvDate.text = dataProfile.birthday
//        }

        when (dataProfile.contactType) {
            InfoCustomerConstants.NOT_FRIEND -> {
                binding.lnMakeFriend.show()
                binding.llFriend.hide()
                binding.llFeedBack.hide()
                binding.llRecall.hide()
                binding.profileInfo.tvEditProfile.hide()
                binding.profileInfo.tvDetailCustomer.show()
                EventBus.getDefault()
                    .post(ContactEventBus(InfoCustomerConstants.NOT_FRIEND, dataProfile.userId))
            }

            InfoCustomerConstants.FRIEND -> {
                binding.lnMakeFriend.hide()
                binding.llFriend.show()
                binding.llFeedBack.hide()
                binding.llRecall.hide()
                binding.profileInfo.tvEditProfile.hide()
                binding.profileInfo.tvDetailCustomer.show()
                EventBus.getDefault()
                    .post(ContactEventBus(InfoCustomerConstants.FRIEND, dataProfile.userId))
            }

            InfoCustomerConstants.WAITING_RESPONSE -> {
                binding.lnMakeFriend.hide()
                binding.llFriend.hide()
                binding.llFeedBack.hide()
                binding.llRecall.show()
                binding.profileInfo.tvEditProfile.hide()
                binding.profileInfo.tvDetailCustomer.show()
                EventBus.getDefault().post(
                    ContactEventBus(
                        InfoCustomerConstants.WAITING_RESPONSE, dataProfile.userId
                    )
                )
            }

            InfoCustomerConstants.WAITING_CONFIRM -> {
                binding.lnMakeFriend.hide()
                binding.llFriend.hide()
                binding.llFeedBack.show()
                binding.llRecall.hide()
                binding.profileInfo.tvEditProfile.hide()
                binding.profileInfo.tvDetailCustomer.show()
                EventBus.getDefault().post(
                    ContactEventBus(
                        InfoCustomerConstants.WAITING_CONFIRM, dataProfile.userId
                    )
                )
            }

            InfoCustomerConstants.ITS_ME -> {
                binding.lnMakeFriend.hide()
                binding.llFriend.hide()
                binding.llFeedBack.hide()
                binding.llRecall.hide()
                binding.lnMessage.hide()
                binding.ivBtnMore.hide()
                binding.profileInfo.tvEditProfile.show()
                binding.profileInfo.tvDetailCustomer.hide()

            }

        }
    }

    @SuppressLint("IntentWithNullActionLaunch")
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        if (friendDataList[position].userId == UserCache.getUser().id) {
            startActivity(HomeActivity::class.java)
            finish()
        } else {
            try {
                val intent = Intent(
                    this, InfoCustomerActivity::class.java
                )
                val bundle = Bundle()
                bundle.putInt(AppConstants.ID, friendDataList[position].userId.toInt())
                intent.putExtras(bundle)
                startActivity(intent)
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }








    /**
     * api lấy chi tiết cuộc trò chuyện
     */
    private fun getDetailGroup(idGroup: String) {
        EasyHttp.get(this).api(DetailGroupApi.params(idGroup))
            .request(object : HttpCallbackProxy<HttpData<GroupChat>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpFail(throwable: Throwable?) {
                    Timber.e(
                        "${
                            AppApplication.applicationContext()
                                .getString(R.string.error_message)
                        } ${throwable}"
                    )
                }

                @SuppressLint("IntentWithNullActionLaunch")
                override fun onHttpSuccess(data: HttpData<GroupChat>) {
                    if (data.isRequestSucceed()) {
                        try {
                            val groupData = data.getData()
                            val bundle = Bundle()
                            val intent = Intent(
                                this@InfoCustomerActivity,
                                ConversationDetailActivity::class.java
                            )
                            bundle.putString(
                                AppConstants.GROUP_DATA, Gson().toJson(groupData)
                            )
                            intent.putExtras(bundle)
                            startActivity(intent)
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
                    }
                }
            })
    }
}