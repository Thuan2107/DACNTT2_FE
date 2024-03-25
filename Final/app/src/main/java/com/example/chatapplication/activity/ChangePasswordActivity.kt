package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.provider.Settings
import android.text.InputFilter
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.api.ChangePasswordApi
import com.example.chatapplication.api.LoginApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.databinding.ActivityChangePasswordBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.other.queryAfterTextChanged
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import okhttp3.Call
import timber.log.Timber

class ChangePasswordActivity : AppActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun getLayoutView(): View {
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun initView() {
        //validate password
        binding.edtCurrentPassword.filters = arrayOf(
            AppUtils.specialCharacters, InputFilter.LengthFilter(20)
        )
        //validate password
        binding.edtNewPassword.filters = arrayOf(
            AppUtils.specialCharacters, InputFilter.LengthFilter(20)
        )
        //validate password
        binding.edtConfirmNewPassword.filters = arrayOf(
            AppUtils.specialCharacters, InputFilter.LengthFilter(20)
        )
        binding.edtCurrentPassword.typeface = Typeface.DEFAULT

        binding.edtCurrentPassword.transformationMethod = PasswordTransformationMethod()

        binding.edtNewPassword.typeface = Typeface.DEFAULT

        binding.edtNewPassword.transformationMethod = PasswordTransformationMethod()

        binding.edtConfirmNewPassword.typeface = Typeface.DEFAULT

        binding.edtConfirmNewPassword.transformationMethod = PasswordTransformationMethod()
        setOnClickListener(binding.btnComplete)



        binding.edtCurrentPassword.queryAfterTextChanged(delay = 500) {
            binding.btnComplete.isEnabled =
                binding.edtCurrentPassword.length() >= 4 && binding.edtNewPassword.length() >= 4 && binding.edtConfirmNewPassword.length() >= 4

            if (binding.edtCurrentPassword.length() < 4) {
                binding.tvErrorCurrentPassword.show()
            } else {
                binding.tvErrorCurrentPassword.hide()
            }
        }

        binding.edtNewPassword.queryAfterTextChanged(delay = 500) {
            binding.btnComplete.isEnabled =
                binding.edtCurrentPassword.length() >= 4 && binding.edtNewPassword.length() >= 4 && binding.edtConfirmNewPassword.length() >= 4 && binding.edtNewPassword.text.toString() == binding.edtConfirmNewPassword.text.toString()

            if (binding.edtNewPassword.length() < 4) {
                binding.tvErrorNewPassword.show()
            } else {
                binding.tvErrorNewPassword.hide()
            }


        }

        binding.edtConfirmNewPassword.queryAfterTextChanged(delay = 500) {
            binding.btnComplete.isEnabled =
                binding.edtCurrentPassword.length() >= 4 && binding.edtNewPassword.length() >= 4 && binding.edtConfirmNewPassword.length() >= 4 && binding.edtNewPassword.text.toString() == binding.edtConfirmNewPassword.text.toString()

            if (binding.edtConfirmNewPassword.length() < 4) {
                binding.tvErrorConfirmNewPassword.show()
            } else {
                binding.tvErrorConfirmNewPassword.hide()
            }
        }
    }


    override fun onClick(view: View) {
        when (view) {
            binding.btnComplete -> {
                if (checkValid()) {
                    setPassword(
                        binding.edtNewPassword.text.toString(),
                    )
                }
                return
            }
        }
    }

    override fun initData() {
    }

    private fun checkValid(): Boolean {
        if (binding.edtNewPassword.text.toString() == UserCache.getUser().password) {
            toast(
                getString(R.string.current_password_alike)
            )
            return false
        }

        if (binding.edtCurrentPassword.text.toString() != UserCache.getUser().password) {
            toast(
                getString(R.string.current_password_not_match)
            )
            return false
        }

        if (!binding.edtNewPassword.text?.toString()
                .equals(binding.edtConfirmNewPassword.text.toString())
        ) {
            toast(
                getString(R.string.password_not_match)
            )
            return false
        }

        if (binding.edtCurrentPassword.text?.count()!! < 4 || binding.edtNewPassword.text?.count()!! < 4 || binding.edtConfirmNewPassword.text?.count()!! < 4) {
            toast(
                getString(R.string.check_valid_password_minimum)
            )
            return false
        }

        return true
    }

    private fun setPassword(
        password: String
    ) {
        EasyHttp.post(this).api(
            ChangePasswordApi.params(
                password
            )
        ).request(object : HttpCallbackProxy<HttpData<Any>>(this) {

            override fun onHttpStart(call: Call?) {
                //empty
            }

            override fun onHttpEnd(call: Call?) {
                //empty
            }

            override fun onHttpFail(throwable: Throwable?) {
                Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${throwable}")
                hideDialog()
            }

            override fun onHttpSuccess(data: HttpData<Any>) {
                if (data.isRequestSucceed()) {
//                    onLogin(UserCache.getUser().phone, password)
                    val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${data.getMessage()}")
                    hideDialog()
                }
            }
        })
    }


    @SuppressLint("HardwareIds")
    private fun onLogin(userName: String, passwordLogin: String) {

        EasyHttp.post(this).api(
            LoginApi.params(
                 userName, passwordLogin
            )
        ).request(object : HttpCallbackProxy<HttpData<User?>>(this) {
            override fun onHttpStart(call: Call?) {
                //empty
            }

            override fun onHttpEnd(call: Call?) {
                //empty
            }

            override fun onHttpFail(throwable: Throwable?) {
                
                Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${throwable}")
                hideDialog()
            }

            override fun onHttpSuccess(data: HttpData<User?>) {
                if (data.isRequestSucceed()) {
                    val user = data.getData()
                    user!!.password = passwordLogin
                    UserCache.saveUser(user)

                    postDelayed({
                        toast(R.string.successfully_change_password)
                        hideDialog()
                        finish()
                    }, 1000)
                } else {
                    Timber.e("${AppApplication.applicationContext().getString(R.string.error_message)} ${data.getMessage()}")
                    hideDialog()
                }
            }
        })
    }

}