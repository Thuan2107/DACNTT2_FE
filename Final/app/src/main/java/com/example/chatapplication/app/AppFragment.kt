package com.example.chatapplication.app

import com.example.chatapplication.base.BaseFragment
import com.hjq.http.listener.OnHttpListener
import okhttp3.Call
import com.example.chatapplication.action.ToastAction
import com.example.chatapplication.model.HttpData


abstract class AppFragment<A : AppActivity> : BaseFragment<A>(),
    ToastAction, OnHttpListener<Any> {

    /**
     * Hộp thoại tải hiện tại có hiển thị hay không
     */
    open fun isShowDialog(): Boolean {
        val activity: A = getAttachActivity() ?: return false
        return activity.isShowDialog()
    }

    override fun initView() {
    }
    /**
     * Hiển thị hộp thoại tải
     */
    open fun showDialog() {
         getAttachActivity()?.showDialog()
    }
    /**
     * Ẩn hộp thoại tải
     */
    open fun hideDialog() {
        getAttachActivity()?.hideDialog()
    }

    /**
     * [OnHttpListener]
     */
    override fun onHttpStart(call: Call) {
        showDialog()
    }

    override fun onHttpSuccess(result: Any) {
        if (result !is HttpData<*>) {
            return
        }
        toast(result.getMessage())
    }

    override fun onHttpFail(throwable: Throwable?) {
        toast(throwable)
    }

    override fun onHttpEnd(call: Call) {
        hideDialog()
    }
}