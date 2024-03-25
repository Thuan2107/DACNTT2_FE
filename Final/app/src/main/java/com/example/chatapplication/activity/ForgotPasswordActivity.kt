package com.example.chatapplication.activity

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import com.example.chatapplication.R
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.databinding.ActivityForgotPasswordBinding
import com.example.chatapplication.manager.ActivityManager
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import timber.log.Timber

/**
 * @Author: Phạm Văn Nhân
 * @Date: 27/09/2022
 */
class ForgotPasswordActivity : AppActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun getLayoutView(): View {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        setOnClickListener(binding.btnGetOtp, binding.txtGotoHome)

        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //This method is called when the text is being changed
                if (s.toString().isEmpty())
                    binding.btnClear.hide()
                else
                    binding.btnClear.show()

            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed
                binding.btnGetOtp.isEnabled = binding.edtPhone.text.toString().length >= 10

                if (binding.edtPhone.text.toString().length < 10) {
                    binding.tvErrorPhone.show()
                } else {
                    binding.tvErrorPhone.hide()
                }
            }
        })
    }

    override fun initData() {
        binding.btnClear.setOnClickListener {
            binding.edtPhone.setText("")
            binding.btnClear.hide()
        }

    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            .statusBarColor(R.color.gray_background_login)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnGetOtp -> {
                if (checkValid()) {
                }
                return
            }

            binding.txtGotoHome -> {
                ActivityManager.getInstance().finishAllActivities()
                startActivity(LoginActivity::class.java)
                return
            }
        }
    }

    //Kiểm tra thông tin đăng nhập
    private fun checkValid(): Boolean {
        if (binding.edtPhone.text!!.length < 10) {
            checkAnimationValidate(binding.edtPhone)
            toast(
                getString(R.string.check_valid_phone_less_than_ten)
            )
            return false
        }
        return true
    }

    //Hiệu ứng khi nhập không đúng dữ liệu
    private fun checkAnimationValidate(view: View) {
        view.startAnimation(
            AnimationUtils.loadAnimation(
                getContext(), R.anim.shake_anim
            )
        )
    }


    //Gọi api quên mật khẩu
//    private fun onForgotPassword(phone: String) {
//        EasyHttp.post(this).api(ForgotPasswordApi.params(phone))
//            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
//
//                override fun onHttpStart(call: Call?) {
//                    //empty
//                }
//
//                override fun onHttpEnd(call: Call?) {
//                    //empty
//                }
//
//
//                override fun onHttpSuccess(data: HttpData<Any>) {
//                    if (data.isRequestSucceed()) {
//                        val intent =
//                            Intent(this@ForgotPasswordActivity, VerifyCodeActivity::class.java)
//                        intent.putExtra(
//                            LoginConstants.ACTION_SCREEN, LoginConstants.FORGOT_PASSWORD
//                        )
//                        intent.putExtra(LoginConstants.PHONE, phone)
//                        postDelayed({
//                            hideDialog()
//                            startActivity(intent)
//                        }, 1000)
//                    } else {
//                        hideDialog()
//                        toast(
//                            data.getMessage()
//                        )
//                        checkAnimationValidate(binding.edtPhone)
//                    }
//
//                }
//            })
//    }

}