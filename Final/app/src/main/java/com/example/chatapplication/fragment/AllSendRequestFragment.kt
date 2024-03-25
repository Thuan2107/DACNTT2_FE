package com.example.chatapplication.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.activity.RequestFriendActivity
import com.example.chatapplication.adapter.AllSendRequestAdapter
import com.example.chatapplication.api.GetListWaitingResponseApi
import com.example.chatapplication.api.RemoveRequestFriendApi
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.app.AppFragment
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.FragmentSendBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Friend
import com.example.chatapplication.other.CustomLoadingListItemCreator
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.paginate.Paginate
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class AllSendRequestFragment : AppFragment<RequestFriendActivity>(), BaseAdapter.OnItemClickListener,
    AllSendRequestAdapter.FriendRequestInterface {
    private lateinit var binding: FragmentSendBinding
    private var sendRequestList = ArrayList<Friend>()
    private var adapterSendRequestFrom: AllSendRequestAdapter? = null
    private var totalSend: Int = 0
    private var totalPage: Int = 0
    private var loading: Boolean = false
    private var position: String = ""
    private var currentPage: Int = 1

    override fun getLayoutView(): View {
        binding = FragmentSendBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        fun newInstance(): AllSendRequestFragment {
            return AllSendRequestFragment()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initView() {
        adapterSendRequestFrom = AllSendRequestAdapter(requireContext())
        adapterSendRequestFrom?.setOnItemClickListener(this)
        adapterSendRequestFrom!!.setClickFriendRequest(this)
        AppUtils.initRecyclerViewVertical(binding.rcvSend, adapterSendRequestFrom)

    }

    override fun initData() {
        getFriendRequestSend(position)
        paginate()
    }

    /**
     * Gọi api lấy danh sách lời mời đã gửi
     */
    @SuppressLint("HardwareIds")
    private fun getFriendRequestSend(position: String) {
        if (currentPage == 1) {
            binding.sflAllSend.show()
            binding.sflAllSend.startShimmer()
            binding.rcvSend.hide()
            binding.lnEmpty.hide()

        }
        EasyHttp.get(this).api(GetListWaitingResponseApi.params(AppConstants.WAITING_RESPONSE))
            .request(object : HttpCallbackProxy<HttpData<List<Friend>>>(this) {
                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpFail(throwable: Throwable?) {
                    
                    Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${throwable}")
                    hideDialog()
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<List<Friend>>) {
                    if (data.isRequestSucceed()) {
                        if (currentPage == 1) {
                            binding.sflAllSend.hide()
                            binding.sflAllSend.stopShimmer()
                            binding.rcvSend.show()
                            sendRequestList.clear()


                            if (data.getData()!!.isEmpty()) {
                                binding.rcvSend.hide()
                                binding.lnEmpty.show()
                            } else {
                                binding.rcvSend.show()
                                binding.lnEmpty.hide()
                            }

                            sendRequestList.addAll(data.getData()!!)
                            adapterSendRequestFrom?.setData(sendRequestList)
                            totalPage = AppUtils.calculateTotalPage(totalSend, 20)
                            adapterSendRequestFrom!!.notifyDataSetChanged()
                        } else {
                            sendRequestList.addAll(data.getData()!!)
                            adapterSendRequestFrom!!.notifyDataSetChanged()
                        }
                    } else {
                        Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${data.getMessage()}")
                        hideDialog()
                    }
                }
            })
    }

    /**
     * Gọi api thu hồi lời mời kết bạn
     */
    @SuppressLint("HardwareIds")
    private fun removeRequestFriend(id: String) {
        EasyHttp.post(this).api(RemoveRequestFriendApi.params(id))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    toast("Đã thu hồi kết bạn thành công")
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
     * phân trang
     */
    private fun paginate() {
        val callback: Paginate.Callbacks = object : Paginate.Callbacks {
            override fun onLoadMore() {
                loading = true
                postDelayed({
                    if (currentPage < totalPage) {
                        currentPage += 1
                        getFriendRequestSend(sendRequestList[sendRequestList.size - 1].position)
                        loading = false

                    }
                }, 200)
            }

            override fun isLoading(): Boolean {
                return loading
            }

            override fun hasLoadedAllItems(): Boolean {
                if (totalSend < 20) return true
                return currentPage == totalPage

            }

        }

        Paginate.with(binding.rcvSend, callback).setLoadingTriggerThreshold(0)
            .addLoadingListItem(true).setLoadingListItemCreator(CustomLoadingListItemCreator())
            .build()
    }

    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
//        try {
//            val intent = Intent(context, Class.forName(ModuleClassConstants.INFO_CUSTOMER))
//            val bundle = Bundle()
//            bundle.putInt(AccountConstants.ID, sendRequestList[position].userId.toInt())
//            intent.putExtras(bundle)
//            startActivity(intent)
//        } catch (e: Exception) {
//            Timber.e(e.message)
//        }
    }

    override fun clickAgree(position: Int) {}
    override fun clickClose(item: Friend) {
        removeRequestFriend(item.userId)
    }

    override fun clickProfile(position: Int) {}
    override fun clickAvatar(position: Int) {
//        val original = Original()
//        original.url = sendRequestList[position].avatar
//        original.name = sendRequestList[position].fullName
//        AppUtils.showMediaAvatar(requireContext(), MediaList(original, 0), 0)
    }

}