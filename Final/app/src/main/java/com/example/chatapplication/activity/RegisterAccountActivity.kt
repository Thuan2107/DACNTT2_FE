package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chatapplication.R
import com.example.chatapplication.api.RegisterAccountApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.constant.LoginConstants
import com.example.chatapplication.databinding.ActivityRegisterAccountBinding
import com.example.chatapplication.dialog.DateDialog
import com.example.chatapplication.manager.ActivityManager
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Medias
import com.example.chatapplication.other.queryAfterTextChanged
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.example.chatapplication.utils.PhotoPickerUtils
import com.example.chatapplication.utils.TimeUtils
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.tencent.mmkv.MMKV
import io.socket.client.IO
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.PollingXHR
import io.socket.engineio.client.transports.WebSocket
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

import java.io.UnsupportedEncodingException
import java.util.Calendar
import java.util.Collections
import java.util.Date


class RegisterAccountActivity : AppActivity() {

    private lateinit var binding: ActivityRegisterAccountBinding
    private var medias: ArrayList<Medias> = ArrayList()
    private var gender: Int = 1 //mặc định chọn 5

    private var localMedia: ArrayList<LocalMedia> = ArrayList()
    val mmkv: MMKV = MMKV.mmkvWithID("push_token")
    private var phone: String? = ""
    private var verifyCode: String? = ""
    private var avatar: String = ""
    private var password: String = ""
    

    override fun getLayoutView(): View {
        binding = ActivityRegisterAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        //validate password
        binding.edtPassword.filters = arrayOf(
            AppUtils.specialCharacters, InputFilter.LengthFilter(20)
        )
        //validate password
        binding.edtRetypePassword.filters = arrayOf(
            AppUtils.specialCharacters, InputFilter.LengthFilter(20)
        )

        binding.edtPassword.typeface = Typeface.DEFAULT
        binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        binding.edtRetypePassword.typeface = Typeface.DEFAULT
        binding.edtRetypePassword.transformationMethod = PasswordTransformationMethod()
        binding.rltAvatar.setOnClickListener {
            PhotoPickerUtils.showImagePickerChooseAvatar(
                this, pickerImageIntent
            )
        }

        setOnClickListener(binding.btnComplete, binding.txtBirthday, binding.txtGotoHome)

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

                if (s.toString().length < 10) {
                    binding.tvErrorPhone.show()
                } else {
                    binding.tvErrorPhone.hide()
                }
            }
        })

        binding.edtFullName.addTextChangedListener(object : TextWatcher {
            var selection = 0
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                selection = binding.edtFullName.selectionEnd
            }

            override fun afterTextChanged(editable: Editable) {
                binding.edtFullName.removeTextChangedListener(this)
                if (AppUtils.containsSpecialCharacters(binding.edtFullName.text.toString())) {
                    binding.edtFullName.setText(AppUtils.removeSpecialCharacters(binding.edtFullName.text.toString()))
                }
                binding.edtFullName.setSelection(binding.edtFullName.text.toString().length)
                if (selection < binding.edtFullName.text.toString().length) {
                    binding.edtFullName.setSelection(selection)
                } else {
                    binding.edtFullName.setSelection(binding.edtFullName.text!!.length)
                }
                binding.edtFullName.addTextChangedListener(this)
            }
        })

        //kiểm tra click chọn giới tính
        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            gender = when (checkedId) {
                R.id.rbMale -> 1
                R.id.rbFemale -> 0
                else -> 2
            }
        }

        binding.txtBirthday.addTextChangedListener(textBirthdayWatcher)

        binding.edtFullName.queryAfterTextChanged(delay = 500) {
            binding.btnComplete.isEnabled =
                binding.edtFullName.length() >= 2 && binding.edtPassword.length() >= 4 && binding.txtBirthday.text.toString() != "" && binding.edtRetypePassword.length() >= 4 
            if (binding.edtFullName.length() < 2) {
                binding.tvErrorName.show()
            } else {
                binding.tvErrorName.hide()
            }
        }

        binding.edtPassword.queryAfterTextChanged(delay = 500) {
            binding.btnComplete.isEnabled =
                binding.edtFullName.length() >= 2 && binding.edtPassword.length() >= 4 && binding.txtBirthday.text.toString() != "" && binding.edtRetypePassword.length() >= 4 

            if (binding.edtPassword.length() < 4) {
                binding.tvErrorPassword.show()
            } else {
                binding.tvErrorPassword.hide()
            }
        }


        binding.edtRetypePassword.queryAfterTextChanged(delay = 500) {
            binding.btnComplete.isEnabled =
                binding.edtFullName.length() >= 2 && binding.edtPassword.length() >= 4 && binding.txtBirthday.text.toString() != "" && binding.edtRetypePassword.length() >= 4 
            if (binding.edtRetypePassword.length() < 4) {
                binding.tvErrorRetypePassword.show()
            } else {
                binding.tvErrorRetypePassword.hide()
            }
        }

        
        binding.rltAvatar.setOnClickListener {
            PhotoPickerUtils.showImagePickerChooseAvatar(
                this, pickerImageIntent
            )
        }
    }

    private val textBirthdayWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable) {
            val date = TimeUtils.getDateFromString(
                binding.txtBirthday.text.toString(),
                "dd/MM/yyyy",
                "dd/MM/yyyy",
                false
            )
            binding.btnComplete.isEnabled =
                binding.edtFullName.length() >= 2 && binding.edtPassword.length() >= 4 && binding.txtBirthday.text.toString() != "" && binding.edtRetypePassword.length() >= 4  && date <= Date()
            postDelayed({
                if (date > Date()) {
                    binding.tvErrorBirthday.show()
                } else {
                    binding.tvErrorBirthday.hide()
                }
            }, 500)

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // onTextChanged
        }
    }

    private var pickerImageIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent = it.data!!
            localMedia = PictureSelector.obtainSelectorList(data) as ArrayList<LocalMedia>
            PhotoLoadUtils.loadImageAvatarByGlide(
                binding.imvAvatarUser, localMedia[0].realPath
            )
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            .statusBarColor(R.color.gray_background_login)

    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnComplete -> {
                showDialog()
                if (checkRegistry()) {
                    onRegisterAccount()
                }
                return
            }

            binding.txtGotoHome -> {
                ActivityManager.getInstance().finishAllActivities()
                startActivity(LoginActivity::class.java)
                return
            }

            binding.txtBirthday -> {
                AppUtils.disableClickAction(binding.txtBirthday, 1000)
                val cal = Calendar.getInstance()
                // Chọn ngày
                DateDialog.Builder(
                    this,
                    cal.get(Calendar.DATE),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR)
                ).setTitle(getString(R.string.date_title))
                    // Văn bản nút OK
                    .setConfirm(getString(R.string.common_confirm))
                    // Đặt null để không hiển thị nút hủy
                    .setCancel(getString(R.string.common_cancel))
                    // Đặt ngày
                    //.setDate("2018-12-31")
                    //.setDate("20181231")
                    //.setDate(1546263036137)
                    // Đặt năm
                    //.setYear(1999)
                    // Đặt tháng
                    //.setMonth(8)
                    // Đặt ngày
                    //.setDay(15)
                    // Không chọn ngày
                    //.setIgnoreDay()
                    .setListener(object : DateDialog.OnDateDialogListener {
                        @SuppressLint("SetTextI18n")
                        override fun onSelected(
                            dialog: BaseDialog, year: Int, month: Int, day: Int
                        ) {
                            val sDay = if (day < 10) {
                                "0${day}"
                            } else {
                                "$day"
                            }

                            val sMonth = if (month < 10) {
                                "0${month}"
                            } else {
                                "$month"
                            }

                            binding.txtBirthday.text = "${sDay}/${sMonth}/${year}"
                        }
                    }).show()
                return
            }
        }
    }

    override fun initData() {
        phone = getString(LoginConstants.PHONE)
        verifyCode = getString(LoginConstants.VERIFY_CODE)
    }


    @SuppressLint("HardwareIds")
    private fun onRegisterAccount() {
        val data: ByteArray
        var base64 = ""
        val stringTest = binding.edtPassword.text.toString()
        try {
            data = stringTest.toByteArray(charset("UTF-8"))
            base64 = Base64.encodeToString(
                data, Base64.NO_WRAP or Base64.URL_SAFE
            )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        EasyHttp.post(this).api(
            RegisterAccountApi.params(
                binding.edtPhone.text.toString(),
                avatar,
                binding.edtFullName.text.toString(),
                binding.txtBirthday.text.toString(),
                gender,
                binding.edtPassword.text.toString()
            )
        ).request(object : HttpCallbackProxy<HttpData<Any>>(this) {

            override fun onHttpSuccess(data: HttpData<Any>) {
                AppUtils.disableClickAction(binding.btnComplete, 200)
                if (data.isRequestSucceed()) {
                    toast("đăng ký thành công")
                    val intent = Intent(this@RegisterAccountActivity, LoginActivity::class.java)
                    startActivity(intent)
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
        })
    }


    @SuppressLint("IntentWithNullActionLaunch")
    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        toast(getString(R.string.register_successful))
        startActivity(intent)
        ActivityManager.getInstance().finishAllActivities()
    }

    //Kiểm tra thông tin đăng ký
    private fun checkRegistry(): Boolean {
        val date = TimeUtils.getDateFromString(
            binding.txtBirthday.text.toString(),
            "dd/MM/yyyy",
            "dd/MM/yyyy",
            false
        )
        if (localMedia.size == 0) {
            toast(getString(R.string.avatar_is_empty))
            checkAnimationValidate(binding.rltAvatar)
            hideDialog()
            return false
        }
        if (date > Date()) {
            toast(R.string.check_valid_birthday_false)
            checkAnimationValidate(binding.txtBirthday)
            hideDialog()
            return false
        }
        if (!binding.rbFemale.isChecked && !binding.rbMale.isChecked) {
            toast(R.string.check_valid_gender_false)
            checkAnimationValidate(binding.rgGender)
            hideDialog()
            return false
        }

        if (binding.edtPassword.text.toString() != binding.edtRetypePassword.text.toString()) {
            toast(
                getString(R.string.check_valid_retype_password_false)
            )
            checkAnimationValidate(binding.edtPassword)
            checkAnimationValidate(binding.edtRetypePassword)
            hideDialog()
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


}