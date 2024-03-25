package com.example.chatapplication.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import com.example.chatapplication.R
import com.example.chatapplication.api.LoginApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.databinding.ActivityLoginBinding
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.User
import com.example.chatapplication.other.queryAfterTextChanged
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy


class LoginActivity : AppActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var phone: String = ""
    private var password: String = ""
    private var userData: User = User()

    override fun getLayoutView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("TimberArgCount", "SetTextI18n")
    override fun initView() {
        enableLoginButton()

        setOnClickListener(
            binding.btnLogin,
            binding.txtRegister,
        )
        //validate password
        binding.edtPassword.filters = arrayOf(
            AppUtils.specialCharacters, InputFilter.LengthFilter(20)
        )
        binding.edtPassword.typeface = Typeface.DEFAULT
        binding.edtPassword.transformationMethod = PasswordTransformationMethod()
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
                enableLoginButton()

                if (s.toString().length < 10) {
                    binding.tvErrorPhone.show()
                } else {
                    binding.tvErrorPhone.hide()
                }
            }
        })
        binding.edtPassword.queryAfterTextChanged(delay = 500) {
            enableLoginButton()

            if (binding.edtPassword.text.toString().length < 4) {
                binding.tvErrorPassword.show()
            } else {
                binding.tvErrorPassword.hide()
            }
        }

        binding.edtPhone.setText("0111111111")
        binding.edtPassword.setText("123456")
    }

    private fun enableLoginButton() {
        binding.btnLogin.isEnabled =
            binding.edtPhone.text.toString().length >= 10 && binding.edtPassword.text.toString().length >= 4
    }



    //Hiển thị hộp thoại xác thực vân tây

    override fun initData() {
//        val user = UserCache.getUser()
//        phone = user.phone
//
//        if (phone != "") {
//            binding.edtPhone.append(phone)
//        }
//
//        binding.btnClear.setOnClickListener {
//            binding.edtPhone.setText("")
//            binding.btnClear.hide()
//        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onClick(view: View) {
        when (view) {

            binding.txtRegister -> {
                val intent = Intent(this, RegisterAccountActivity::class.java)
                startActivity(intent)
                return
            }

            binding.btnLogin -> {
                if (checkValid()) {
                    onLogin(binding.edtPhone.text.toString(),
                        binding.edtPassword.text.toString())
                }
                return
            }
        }
    }

    //Kiểm tra thông tin đăng nhập
    private fun checkValid(): Boolean {
        if (binding.edtPhone.text.toString().length < 10) {
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

    //Gọi api login

    private fun onLogin(userName: String, passwordLogin: String) {


        EasyHttp.post(this).api(LoginApi.params(userName, passwordLogin)).request(/* listener = */
            object : HttpCallbackProxy<HttpData<User?>>(this){
                override fun onHttpSuccess(result: HttpData<User?>) {
                    super.onHttpSuccess(result)
                    Log.d("login", "success")
                    userData = result.getData()!!
                    userData.password = passwordLogin
                    UserCache.saveUser(userData)

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                }

                override fun onHttpFail(throwable: Throwable?) {
                    super.onHttpFail(throwable)
                    Log.d("login", "success")

                }

                override fun onHttpStart(call: okhttp3.Call?) {
                    super.onHttpStart(call)
                }

                override fun onHttpEnd(call: okhttp3.Call?) {
                    super.onHttpEnd(call)
                }
            })

    }


}


