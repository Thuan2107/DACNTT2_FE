package com.example.chatapplication.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.chatapplication.R
import com.example.chatapplication.action.HandlerAction
import com.example.chatapplication.model.HttpData
import com.hjq.bar.TitleBar
import com.hjq.http.listener.OnHttpListener
import okhttp3.Call
import com.example.chatapplication.base.BaseActivity
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.action.TitleBarAction
import com.example.chatapplication.action.ToastAction
import com.gyf.immersionbar.ImmersionBar


abstract class AppActivity : BaseActivity(),
    ToastAction, TitleBarAction, OnHttpListener<Any> {



    /** đối tượng thanh tiêu đề */
    private var titleBar: TitleBar? = null

    /** Ngâm thanh trạng thái */
    private var immersionBar: ImmersionBar? = null

    /** Tải hộp thoại */
    private var dialog: BaseDialog? = null

    /** số hộp thoại */
    private var dialogCount: Int = 0

    /**
     * Hộp thoại tải hiện tại có hiển thị hay không
     */
    open fun isShowDialog(): Boolean {
        return dialog != null && dialog!!.isShowing
    }
    

    /**
     * Hiển thị hộp thoại tải
     */
    open fun showDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        dialogCount++
        postDelayed(Runnable {
            if ((dialogCount <= 0) || isFinishing || isDestroyed) {
                return@Runnable
            }

//            if (!dialog!!.isShowing) {
//                dialog!!.show()
//            }
        }, 300)
    }

    /**
     * Ẩn hộp thoại tải
     */
    open fun hideDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        if (dialogCount > 0) {
            dialogCount--
        }
//        if ((dialogCount != 0) || (dialog == null) || !dialog!!.isShowing) {
//            return
//        }
        dialog?.dismiss()
    }

    override fun initLayout() {
        super.initLayout()

        val titleBar = getTitleBar()
        titleBar?.setOnTitleBarListener(this)

        // Khởi tạo thanh trạng thái nhập vai
        if (isStatusBarEnabled()) {
            getStatusBarConfig().init()

            // Đặt độ chìm của thanh tiêu đề
            if (titleBar != null) {
                ImmersionBar.setTitleBar(this, titleBar)
                titleBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
                titleBar.leftIcon = AppCompatResources.getDrawable(this, R.drawable.ic_back_black)
                titleBar.titleView.isAllCaps = true
//                titleBar.setTitleColor(R.color.black)
            }
        }
    }

    /**
     * Có sử dụng thanh trạng thái nhập vai hay không
     */
    protected open fun isStatusBarEnabled(): Boolean {
        return true
    }

    /**
     * Chế độ tối phông chữ trên thanh trạng thái
     */
    open fun isStatusBarDarkFont(): Boolean {
        return true
    }
    /**
     * Lấy đối tượng cấu hình ngâm thanh trạng thái
     */
    open fun getStatusBarConfig(): ImmersionBar {
        if (immersionBar == null) {
            immersionBar = createStatusBarConfig()
        }
        return immersionBar!!
    }

    /**
     * Khởi tạo thanh trạng thái nhập vai
     */
    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this) // Màu phông chữ thanh trạng thái mặc định là màu đen
            .statusBarDarkFont(isStatusBarDarkFont()) // Chỉ định màu nền của thanh điều hướng
            .navigationBarColor(R.color.white) // Font chữ của status bar và nội dung của navigation bar sẽ tự động đổi màu Bạn phải chỉ định màu của status bar và màu sắc của navigation bar để nó tự động đổi màu
            .autoDarkModeEnable(true, 0.2f)
    }

    /**
     * Đặt tiêu đề của thanh tiêu đề
     */
    override fun setTitle(@StringRes id: Int) {
        title = getString(id)
    }

    /**
     * Đặt tiêu đề của thanh tiêu đề
     */
    override fun setTitle(title: CharSequence?) {
        super<BaseActivity>.setTitle(title)
        getTitleBar()?.title = title
    }

    override fun getTitleBar(): TitleBar? {
        if (titleBar == null) {
            titleBar = obtainTitleBar(getContentView())
        }
        return titleBar
    }



    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        overridePendingTransition(R.anim.right_in_activity, R.anim.right_out_activity)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity)
    }

    /**
     * [OnHttpListener]
     */
    override fun onHttpStart(call: Call) {
        showDialog()
    }

    override fun onHttpSuccess(result: Any) {
        if (result is HttpData<*>) {
            toast(result.getMessage())
        }
        hideDialog()
    }

    override fun onHttpFail(throwable: Throwable?) {
        toast(throwable)
        hideDialog()
        dialog = null
    }

    override fun onHttpEnd(call: Call) {
        hideDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isShowDialog()) {
            hideDialog()
        }
        dialog = null

    }

    override fun onLeftClick(view: View?) {
        onBackPressed()
    }

    override fun onTitleClick(view: View?) {

    }

    override fun onRightClick(view: View?) {

    }

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (currentFocus != null) {
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
//        }
//        return super.dispatchTouchEvent(ev)
//    }
}