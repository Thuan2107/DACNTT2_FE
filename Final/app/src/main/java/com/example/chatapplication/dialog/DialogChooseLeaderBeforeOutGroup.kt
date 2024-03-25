package com.example.chatapplication.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import com.example.chatapplication.R
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.adapter.MemberAdapter
import com.example.chatapplication.api.GroupMemberApi
import com.example.chatapplication.api.UpdatePermissionApi
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.databinding.DialogChooseLeaderBeforeOutGroupBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.MemberList
import com.example.chatapplication.other.queryAfterTextChangedSV
import com.example.chatapplication.utils.AppUtils
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.hjq.http.listener.OnHttpListener
import com.hjq.toast.ToastUtils
import okhttp3.Call
import timber.log.Timber

class DialogChooseLeaderBeforeOutGroup {
    class Builder(
            context: Context,
            private val lifecycle: LifecycleOwner,
            private val conversationId: String
    ) : BaseDialog.Builder<Builder>(context), OnHttpListener<Any>, MemberAdapter.OnChooseMemberListener {
        private val binding = DialogChooseLeaderBeforeOutGroupBinding.inflate(LayoutInflater.from(context))

        private var listener: OnChangeLeaderListener? = null
        private var memberId: String = ""
        private var memberList: ArrayList<MemberList> = ArrayList()
        private var adapterMember: MemberAdapter? = null

        fun setOnChangeLeaderListener(listener: OnChangeLeaderListener): Builder = apply {
            this.listener = listener
        }

        init {
            setContentView(binding.root)
            setCancelable(true)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(getResources().displayMetrics.widthPixels)
            setHeight(getResources().displayMetrics.heightPixels * 4 / 5)

            adapterMember = MemberAdapter(context)
            adapterMember?.setData(memberList)
            adapterMember?.setOnChooseMemberListener(this)
            AppUtils.initRecyclerViewVertical(binding.rcvListMember, adapterMember)

            listMember(conversationId)

            binding.btnCancel.setOnClickListener {
                AppUtils.disableClickAction(binding.btnCancel, 1000)
                dismiss()
            }

            binding.btnAccept.setOnClickListener {
                if (memberId.isEmpty()) {
                    ToastUtils.show(getString(R.string.please_choose_member_you_want_to_be_leader))
                } else {
                    binding.btnAccept.isEnabled = false
                    postLeader()
                }
            }

            binding.svSearch.queryAfterTextChangedSV(500) {
                listMember(conversationId)
            }
        }

        private fun listMember(id: String) {
            EasyHttp.get(lifecycle).api(
                    GroupMemberApi.params(
                            id
                    )
            ).request(object : HttpCallbackProxy<HttpData<ArrayList<MemberList>>>(this) {
                override fun onHttpStart(call: Call) {
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onHttpSuccess(data: HttpData<ArrayList<MemberList>>) {
                    if (data.isRequestSucceed()) {
                        memberList.clear()
                        memberList.addAll(data.getData()!!.filter{it.userId != UserCache.getUser().id})
                        adapterMember?.notifyDataSetChanged()
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

        private fun postLeader() {
            EasyHttp.post(lifecycle).api(UpdatePermissionApi.params(conversationId, memberId, ConversationTypeConstants.PERMISSION_OWNER))
                    .request(object : HttpCallbackProxy<HttpData<ArrayList<Any>>>(this) {
                        override fun onHttpStart(call: Call?) {
                            //empty
                        }

                        override fun onHttpSuccess(data: HttpData<ArrayList<Any>>) {
                            if (data.isRequestSucceed()) {
                                binding.btnAccept.isEnabled = true
                                listener?.onChangeLeader()
                                dismiss()
                            } else {
                                binding.btnAccept.isEnabled = true
                                Timber.e(
                                        "${
                                            AppApplication.applicationContext()
                                                    .getString(R.string.error_message)
                                        } ${data.getMessage()}"
                                )
                            }
                        }

                        override fun onHttpFail(throwable: Throwable?) {
                            binding.btnAccept.isEnabled = true
                            Timber.e(
                                    "${
                                        AppApplication.applicationContext()
                                                .getString(R.string.error_message)
                                    } ${throwable}"
                            )
                        }
                    })

        }

        override fun onHttpSuccess(result: Any?) {
            if (result !is HttpData<*>) {
                return
            }
            ToastUtils.show(result.getMessage())
        }

        override fun onChooseMember(member: MemberList, isCheck: Boolean) {
            memberList.mapIndexed { index, item ->
                if (item.isSelected) {
                    item.isSelected = false
                    adapterMember?.notifyItemChanged(index)
                }
            }
            val index = memberList.indexOfFirst { it.userId == member.userId }
            if (index != -1) {
                memberId = if (isCheck) {
                    member.userId
                } else {
                    ""
                }
                memberList[index].isSelected = isCheck
                adapterMember?.notifyItemChanged(index)
            } else {
                memberId = ""
            }
        }

        override fun onHttpFail(throwable: Throwable?) {
            ToastUtils.show(throwable)
        }
    }

    interface OnChangeLeaderListener {
        fun onChangeLeader()
    }
}