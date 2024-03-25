package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.chatapplication.R
import com.example.chatapplication.api.ProFileCustomerApi
import com.example.chatapplication.api.UpdateProfileApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.EditProfileConstant
import com.example.chatapplication.databinding.ActivityEditProfileBinding
import com.example.chatapplication.dialog.DateDialog
import com.example.chatapplication.dialog.SelectDialog
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.Medias
import com.example.chatapplication.model.entity.ProfileCustomerNodeData
import com.example.chatapplication.model.entity.SearchUser
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
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern

class EditProfileActivity : AppActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private var dataProfile: ProfileCustomerNodeData = ProfileCustomerNodeData()
    private var localMedia: ArrayList<LocalMedia> = ArrayList()
    private var medias: ArrayList<Medias> = ArrayList()

    private var username = ""
    private var nickname = ""
    private var gender = EditProfileConstant.MALE
    private var birthDate = ""
    private var email = ""
    private var addressName = ""
    private var cityId = 0
    private var cityName = ""
    private var districtId = 0
    private var districtName = ""
    private var wardId = 0
    private var wardName = ""
    private var isNickName = EditProfileConstant.DISABLE
    private var isGender = EditProfileConstant.DISABLE
    private var isPhone = EditProfileConstant.DISABLE
    private var isBirthday = EditProfileConstant.DISABLE
    private var isEmail = EditProfileConstant.DISABLE
    private var isStreet = EditProfileConstant.DISABLE

    private var oldUsername = ""
    private var oldNickname = ""
    private var oldGender = EditProfileConstant.MALE
    private var oldBirthDate = ""
    private var oldEmail = ""
    private var oldAddressName = ""
    private var oldCityId = 0
    private var oldCityName = ""
    private var oldDistrictId = 0
    private var oldDistrictName = ""
    private var oldWardId = 0
    private var oldWardName = ""
    private var oldIsCheckNickname = EditProfileConstant.DISABLE
    private var oldIsGender = EditProfileConstant.DISABLE
    private var oldIsPhone = EditProfileConstant.DISABLE
    private var oldIsBirthday = EditProfileConstant.DISABLE
    private var oldIsEmail = EditProfileConstant.DISABLE
    private var oldIsStreet = EditProfileConstant.DISABLE

    override fun getLayoutView(): View {
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.header.llTitle)
    }

    override fun initData() {
        //==========================================Action========================================//
        binding.etNameUser.addTextChangedListener(object : TextWatcher {
            var selection = 0
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                selection = binding.etNameUser.selectionEnd
            }

            override fun afterTextChanged(editable: Editable) {
                binding.etNameUser.removeTextChangedListener(this)
                if (AppUtils.containsSpecialCharacters(binding.etNameUser.text.toString())) {
                    binding.etNameUser.setText(AppUtils.removeSpecialCharacters(binding.etNameUser.text.toString()))
                }
                binding.etNameUser.setSelection(binding.etNameUser.text.toString().length)
                if (selection < binding.etNameUser.text.toString().length) {
                    binding.etNameUser.setSelection(selection)
                } else {
                    binding.etNameUser.setSelection(binding.etNameUser.text!!.length)
                }
                username = binding.etNameUser.text.toString()
                enableButtonSave()
                binding.etNameUser.addTextChangedListener(this)
            }
        })

        binding.etNameUser.setRawInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)

        binding.etNameUser.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.etNameUser.append("")
                return@OnKeyListener true
            }
            false
        })

        binding.ibName.setOnClickListener {
            binding.etNameUser.isEnabled = true
            binding.etNameUser.setSelection(binding.etNameUser.text!!.length)
            binding.etNameUser.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etNameUser, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.rbCheckedNickName.setOnClickListener {
            binding.rbCheckedNickName.hide()
            binding.rbNickName.show()
            isNickName = EditProfileConstant.DISABLE
            enableButtonSave()
        }

        binding.rbNickName.setOnClickListener {
            binding.rbCheckedNickName.show()
            binding.rbNickName.hide()
            isNickName = EditProfileConstant.ENABLE
            enableButtonSave()
        }

        binding.etNickName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Empty
            }

            override fun afterTextChanged(s: Editable?) {
                binding.etNickName.removeTextChangedListener(this)
                nickname = binding.etNickName.text.toString()
                enableButtonSave()
                binding.etNickName.addTextChangedListener(this)
            }
        })

        binding.ibNickname.setOnClickListener {
            binding.etNickName.isEnabled = true
            binding.etNickName.setSelection(binding.etNickName.text!!.length)
            binding.etNickName.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etNickName, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.llEditGender.setOnClickListener {
            SelectDialog.Builder(this).setTitle(getString(R.string.choose_gender))
                .setList(getString(R.string.female), getString(R.string.male)).setSingleSelect()
                .setSelect(gender).setListener(object : SelectDialog.OnListener<String> {
                    override fun onSelected(dialog: BaseDialog?, data: HashMap<Int, String>) {
                        binding.tvTextGender.text = data.values.toList()[0]
                        gender = if (binding.tvTextGender.text == getString(R.string.male)) {
                            EditProfileConstant.MALE
                        } else {
                            EditProfileConstant.FEMALE
                        }
                        enableButtonSave()
                    }
                }).show()
        }



        binding.llEditBirthday.setOnClickListener {
            AppUtils.disableClickAction(binding.llEditBirthday, 1000)
            val cal = Calendar.getInstance()
            cal.time =
                TimeUtils.getDateFromString(dataProfile.birthday, "dd/MM/yyyy", "dd/MM/yyyy", false)
            DateDialog.Builder(
                this,
                cal.get(Calendar.DATE),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.YEAR)
            ).setTitle(getString(R.string.date_title))
                .setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener(object : DateDialog.OnDateDialogListener {
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
                        birthDate = "${sDay}/${sMonth}/${year}"
                        binding.tvBirthday.text = birthDate
                        enableButtonSave()
                    }
                }).show()
        }

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Empty
            }

            override fun afterTextChanged(s: Editable?) {
                binding.etEmail.removeTextChangedListener(this)
                email = binding.etEmail.text.toString()
                enableButtonSave()
                binding.etEmail.addTextChangedListener(this)
            }
        })

        binding.etEmail.setOnClickListener {
            if (isEmail == 1) {
                binding.etEmail.setSelection(binding.etEmail.text.toString().length)
                binding.etEmail.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.etPhone, InputMethodManager.SHOW_IMPLICIT)
            }
        }


        binding.etStreetHome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Empty
            }

            override fun afterTextChanged(s: Editable?) {
                binding.etStreetHome.removeTextChangedListener(this)
                addressName = binding.etStreetHome.toString()
                enableButtonSave()
                binding.etStreetHome.addTextChangedListener(this)
            }
        })




        binding.rllAvatar.setOnClickListener {
            PhotoPickerUtils.showImagePickerChooseAvatar(
                this, pickerImageIntent
            )
        }

        binding.header.ivExitEditProfile.setOnClickListener {
            hideKeyboard(binding.etEmail)
            hideKeyboard(binding.llEditEmail)
            finish()
        }

        binding.header.btnSave.setOnClickListener {
            if (validateUpdate()) {
                callUpdateProfileApi()
            }
            hideKeyboard(binding.etNameUser)
            hideKeyboard(binding.etNickName)
            hideKeyboard(binding.etEmail)
            hideKeyboard(binding.etPhone)
            hideKeyboard(binding.etStreetHome)
        }
        //========================================================================================//
        callProfileCustomer()
    }

    @SuppressLint("SuspiciousIndentation")
    fun validateUpdate(): Boolean {
        if (binding.tvBirthday.text.toString().isNotEmpty()) {
            val date = TimeUtils.getDateFromString(
                binding.tvBirthday.text.toString(),
                "dd/MM/yyyy",
                "dd/MM/yyyy",
                false
            )
            if (date > Date()) {
                toast(getString(R.string.validate_birthday))
                return false
            }
        }
        if (binding.tvBirthday.text.toString().isEmpty()) {
            toast(getString(R.string.validate_birthday_null))
            return false
        }
        val trimNickName = binding.etNickName.text.toString().trim()
        if (oldNickname != trimNickName || isNickName == EditProfileConstant.ENABLE) {
            if (trimNickName.length < EditProfileConstant.SECONDS) {
                toast(getString(R.string.string_min_max))
                return false
            }
        }
        val trimName = binding.etNameUser.text.toString().trim()
        if (trimName.length < EditProfileConstant.SECONDS) {
            toast(getString(R.string.string_min_max_name))
            return false
        }
        if (binding.etEmail.text.toString()
                .isNotEmpty() && !isValid(binding.etEmail.text.toString())
        ) {
            toast(getString(R.string.validate_email))
            return false
        }
        return true
    }

    private fun isValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}\$"
        val inputStr: CharSequence = email
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(inputStr)
        return matcher.matches()
    }

    private fun callProfileCustomer() {
        binding.sflShimmerEditProfile.show()
        binding.sflShimmerEditProfile.startShimmer()
        binding.llEditProFile.hide()
        EasyHttp.get(this).api(ProFileCustomerApi.params(UserCache.getUser().id))
            .request(object : HttpCallbackProxy<HttpData<SearchUser>>(this) {
                override fun onHttpEnd(call: Call?) {
                    //empty
                }

                override fun onHttpStart(call: Call?) {
                    //empty
                }

                override fun onHttpSuccess(data: HttpData<SearchUser>) {
                    if (data.isRequestSucceed()) {
                        data.getData()?.let {
                            dataProfile = it.user
                        }

                        dataCustomer()
                        dataIsShow()
                        setDataOld()

                        binding.sflShimmerEditProfile.hide()
                        binding.sflShimmerEditProfile.stopShimmer()
                        binding.llEditProFile.show()
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


    private fun dataCustomer() {
        //Set Data
        username = dataProfile.fullName
        nickname = dataProfile.nickName
        gender = dataProfile.gender
        birthDate = dataProfile.birthday
        email = dataProfile.email
        addressName = dataProfile.address

        //Set Data len View
        PhotoLoadUtils.loadImageAvatarByGlide(
            binding.ivAvatarUser, dataProfile.avatar
        )
        binding.etNameUser.setText(dataProfile.fullName)
        binding.etNickName.setText(dataProfile.nickName)
        binding.tvTextGender.text = if (dataProfile.gender == EditProfileConstant.FEMALE) {
            getString(R.string.female)
        } else {
            getString(R.string.male)
        }
        binding.etPhone.setText(dataProfile.phone)
        binding.tvBirthday.text = dataProfile.birthday
        binding.etEmail.setText(dataProfile.email)
        binding.etStreetHome.setText(dataProfile.address)

    }

    private fun dataIsShow() {
        //Set Data
        isNickName = dataProfile.isDisplayNickName
        isGender = dataProfile.isEnableGender
        isPhone = dataProfile.isEnablePhone
        isBirthday = dataProfile.isEnableBirthday
        isEmail = dataProfile.isEnableEmail
        isStreet = dataProfile.isEnableAddress

        //Set Data len View
        binding.rbCheckedNickName.isVisible = isNickName != EditProfileConstant.DISABLE
        binding.rbNickName.isVisible = isNickName == EditProfileConstant.DISABLE


    }

    private fun setDataOld() {
        oldUsername = binding.etNameUser.text.toString()
        oldNickname = binding.etNickName.text.toString()
        oldGender = gender
        oldBirthDate = binding.tvBirthday.text.toString()
        oldEmail = binding.etEmail.text.toString()
        oldAddressName = binding.etStreetHome.text.toString()
        enableButtonSave()
    }

    private fun enableButtonSave() {
        val recentUsername = binding.etNameUser.text.toString()
        val recentNickname = binding.etNickName.text.toString()
        val recentGender = if (binding.tvTextGender.text == getString(R.string.male)) {
            EditProfileConstant.MALE
        } else {
            EditProfileConstant.FEMALE
        }
        val recentBirthdate = binding.tvBirthday.text.toString()
        val recentEmail = binding.etEmail.text.toString()
        val recentAddressName = binding.etStreetHome.text.toString()
        if (oldUsername != recentUsername ||
            oldNickname != recentNickname ||
            oldGender != recentGender ||
            oldBirthDate != recentBirthdate ||
            oldEmail != recentEmail ||
            oldAddressName != recentAddressName
        ) {
            binding.header.btnSave.isEnabled = true
            binding.header.btnSave.isSelected = true
        } else {
            binding.header.btnSave.isEnabled = false
            binding.header.btnSave.isSelected = false
        }
    }

    //---------------------------------------------API--------------------------------------------//

    private fun callUpdateProfileApi() {
        EasyHttp.post(this).api(
            UpdateProfileApi.params(
                binding.etNameUser.text.toString().trim(),
                UserCache.getUser().avatar,
                binding.etEmail.text.toString().trim(),
                binding.tvBirthday.text.toString(),
                gender,
                binding.etStreetHome.text.toString().trim(),
                binding.etNickName.text.toString(),
                isNickName
            )
        ).request(object : HttpCallbackProxy<HttpData<ProfileCustomerNodeData>>(this) {
            override fun onHttpEnd(call: Call?) {
                //empty
            }

            override fun onHttpStart(call: Call?) {
                //empty
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

            override fun onHttpSuccess(data: HttpData<ProfileCustomerNodeData>) {
                if (data.isRequestSucceed()) {
                    postDelayed({
                        hideDialog()
                        val user = UserCache.getUser()
                        user.name = binding.etNameUser.text.toString()
                        user.email = binding.etEmail.text.toString()
                        if (gender == 0) {
                            user.gender = 0
                        } else {
                            user.gender = 1
                        }
                        UserCache.saveUser(user)
                        toast(getString(R.string.update_profile_info))
                        setDataOld()
                    }, 1000)
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
    //--------------------------------------------------------------------------------------------//

    /**
     * Sự kiện thay đổi avatar
     */

    private var pickerImageIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent = it.data!!
            localMedia = PictureSelector.obtainSelectorList(data) as ArrayList<LocalMedia>
            PhotoLoadUtils.loadImageAvatarByGlide(
                binding.ivAvatarUser, localMedia[0].realPath
            )
            uploadToCloudinary(localMedia[0])
        }
    }

    private fun uploadToCloudinary(localMedia : LocalMedia) {
        Log.d("A", "sign up uploadToCloudinary- ")
        MediaManager.get().upload(localMedia.realPath).callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                Log.d("start", "start")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                Log.d("Uploading... ", "Uploading...")
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val avatar = resultData["url"].toString()
                val user = UserCache.getUser()
                user.avatar = avatar
                UserCache.saveUser(user)
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                Log.d("error " + error.description, "error")
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                Log.d("Reshedule " + error.description, "Reshedule")
            }
        }).dispatch()
    }
}

