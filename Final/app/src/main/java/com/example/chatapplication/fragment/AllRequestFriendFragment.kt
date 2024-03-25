package com.example.chatapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.activity.InfoCustomerActivity
import com.example.chatapplication.activity.RequestFriendActivity
import com.example.chatapplication.adapter.AllRequestFriendAdapter
import com.example.chatapplication.api.AcceptAddFriendApi
import com.example.chatapplication.api.CreateConversationApi
import com.example.chatapplication.api.GetListWaitingConfirmApi
import com.example.chatapplication.api.NotAcceptFriendApi
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.app.AppFragment
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.FragmentReceivedBinding
import com.example.chatapplication.eventbus.RequestEventBus
import com.example.chatapplication.eventbus.RequestFriendEventBus
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.model.entity.GroupId
import com.example.chatapplication.other.CustomLoadingListItemCreator
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.paginate.Paginate
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class AllRequestFriendFragment : AppFragment<RequestFriendActivity>(),
    BaseAdapter.OnItemClickListener,
    AllRequestFriendAdapter.FriendRequestInterface {

    private lateinit var binding: FragmentReceivedBinding
    private var friendRequestFromList: ArrayList<Friend> = ArrayList()
    private var adapterFriendRequestFrom: AllRequestFriendAdapter? = null
    private var totalRequest: Int = 0
    private var totalPage: Int = 0
    private var loading: Boolean = false
    private var position: String = ""
    private var currentPage: Int = 1
    private var limit: Int = 20

    override fun getLayoutView(): View {
        binding = FragmentReceivedBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        fun newInstance(): AllRequestFriendFragment {
            return AllRequestFriendFragment()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initView() {
        adapterFriendRequestFrom = AllRequestFriendAdapter(requireContext())
        adapterFriendRequestFrom?.setOnItemClickListener(this)
        adapterFriendRequestFrom!!.setClickFriendRequest(this)
        AppUtils.initRecyclerViewVertical(binding.rcvFriendRequest, adapterFriendRequestFrom)
        adapterFriendRequestFrom?.setData(friendRequestFromList)
    }

    override fun initData() {
        getFriendRequest("")
        paginate()
    }

    /**
     * Gọi api lấy danh sách lời mời kết bạn
     */
    @SuppressLint("HardwareIds")
    private fun getFriendRequest(position: String) {
        if (currentPage == 1) {
            binding.sflAllSuggest.show()
            binding.rcvFriendRequest.hide()
            binding.lnEmpty.hide()
            binding.sflAllSuggest.startShimmer()
        }
        EasyHttp.get(this)
            .api(GetListWaitingConfirmApi.params(AppConstants.WAITING_CONFIRM))
            .request(object : HttpCallbackProxy<HttpData<List<Friend>>>(this) {
                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<List<Friend>>) {
                    if (data.isRequestSucceed()) {
                        if (currentPage == 1) {
                            binding.sflAllSuggest.hide()
                            binding.sflAllSuggest.stopShimmer()
                            binding.rcvFriendRequest.show()

                            friendRequestFromList.clear()
                            adapterFriendRequestFrom!!.notifyDataSetChanged()

                            if (data.getData()!!.isEmpty()) {
                                binding.rcvFriendRequest.hide()
                                binding.lnEmpty.show()
                            } else {
                                binding.rcvFriendRequest.show()
                                binding.lnEmpty.hide()
                            }

                            totalPage = AppUtils.calculateTotalPage(totalRequest, limit)

                            friendRequestFromList.addAll(data.getData()!!)
                            adapterFriendRequestFrom!!.notifyDataSetChanged()


                        } else {
                            friendRequestFromList.addAll(data.getData()!!)
                            adapterFriendRequestFrom?.notifyDataSetChanged()
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
                        createGroup(id)
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

    private fun paginate() {
        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                postDelayed({
                    if (currentPage < totalPage) {
                        currentPage += 1
                        getFriendRequest(friendRequestFromList[friendRequestFromList.size - 1].position)
                        loading = false
                    }
                }, 200)
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                if (totalRequest < limit) return true
                return currentPage == totalPage

            }

        }

        Paginate.with(binding.rcvFriendRequest, callback)
            .setLoadingTriggerThreshold(2)
            .addLoadingListItem(true)
            .setLoadingListItemCreator(CustomLoadingListItemCreator())
            .build()
    }


    /**
     * Gọi api từ chối kết bạn
     */
    private fun notAccept(id: String) {
        EasyHttp.post(this)
            .api(NotAcceptFriendApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        EventBus.getDefault().post(RequestEventBus(--totalRequest))
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

    @SuppressLint("IntentWithNullActionLaunch")
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
//        try {
//            val intent = Intent(context, Class.forName(ModuleClassConstants.INFO_CUSTOMER))
//            val bundle = Bundle()
//            bundle.putInt(AccountConstants.ID, friendRequestFromList[position].userId.toInt())
//            intent.putExtras(bundle)
//            startActivity(intent)
//        } catch (e: Exception) {
//            Timber.e(e.message)
//        }
    }

    override fun clickAgree(item: Friend) {
        acceptAddFriend(item.userId)
        EventBus.getDefault().post(RequestFriendEventBus(item.userId, isAccept = 1))
    }

    override fun clickClose(item: Friend) {
        notAccept(item.userId)
        EventBus.getDefault().post(RequestFriendEventBus(item.userId, isAccept = 0))
    }

    override fun clickProfile(position: Int) {
        val bundle = Bundle()
        val intent = Intent(
            requireContext(),
            InfoCustomerActivity::class.java
        )
        bundle.putString(
            AppConstants.ID_USER, friendRequestFromList[position].userId)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun clickAvatar(position: Int) {
        AppUtils.showMediaAvatar(requireContext(), friendRequestFromList[position].avatar, 0)
    }

    /**
     * api tạo cuộc trò chuyện
     */
    private fun createGroup(id: String) {
        EasyHttp.post(this)
            .api(CreateConversationApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<GroupId>>(this) {
                override fun onHttpSuccess(data: HttpData<GroupId>) {
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


}