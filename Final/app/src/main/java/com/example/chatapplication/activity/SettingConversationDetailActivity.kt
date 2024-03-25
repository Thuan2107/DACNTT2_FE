package com.example.chatapplication.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.chatapplication.R
import com.example.chatapplication.api.ChangeAvatarGroupApi
import com.example.chatapplication.api.ChangeBackgroundGroupApi
import com.example.chatapplication.api.ChangeNameGroupApi
import com.example.chatapplication.api.DeleteGroupApi
import com.example.chatapplication.api.DetailGroupApi
import com.example.chatapplication.api.OutGroupApi
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.constant.ConversationTypeConstants
import com.example.chatapplication.databinding.ActivitySettingConversationdetailBinding
import com.example.chatapplication.dialog.DialogChooseLeaderBeforeOutGroup
import com.example.chatapplication.dialog.DialogOutGroup
import com.example.chatapplication.eventbus.CurrentFragmentEventBus
import com.example.chatapplication.eventbus.DeleteEventBus
import com.example.chatapplication.eventbus.OutGroupEventBus
import com.example.chatapplication.model.HttpData
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.model.entity.Medias
import com.example.chatapplication.model.entity.Sender
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.example.chatapplication.utils.PhotoPickerUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import com.example.chatapplication.dialog.DialogChangeNameGroup
import com.example.chatapplication.model.entity.ChangeName
import com.example.chatapplication.utils.ChatUtils


class SettingConversationDetailActivity : AppActivity() {
    private lateinit var binding: ActivitySettingConversationdetailBinding
    private var localMedia: ArrayList<LocalMedia> = ArrayList()
    private var dialogOutGroup: DialogOutGroup.Builder? = null
    private var medias: ArrayList<Medias> = ArrayList()
    private var avatar: String = ""
    private var linkAvatar: String = ""
    private var linkBackGround: String = ""
    var groupChat: GroupChat = GroupChat()
    private var member: ArrayList<Sender> = ArrayList()
    private var dialogChangeNameGroup: DialogChangeNameGroup.Builder? = null
    private var btnNotify = true
    private var swHide = true
    private var swPinned = true
    private var imageType = -1

    override fun getLayoutView(): View {
        binding = ActivitySettingConversationdetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(
                R.anim.right_in_activity,
                R.anim.right_out_activity
        )
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
                R.anim.left_in_activity,
                R.anim.left_out_activity
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("IntentWithNullActionLaunch")
    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.tbHeader)
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(ChatConstants.GROUP_DATA)) {
                groupChat = Gson().fromJson(
                        bundleIntent.getString(ChatConstants.GROUP_DATA), GroupChat::class.java
                )
                setDataDetail()
            }
        }
    }

    @SuppressLint("IntentWithNullActionLaunch")
    override fun initData() {
//        binding.llMediaList.setOnClickListener {
//            val intent = Intent(
//                    this, DetailMediaActivity::class.java
//            )
//            val bundle = Bundle()
//            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        binding.llMediaShowChat.setOnClickListener {
//            val intent = Intent(
//                    this, DetailMediaActivity::class.java
//            )
//            val bundle = Bundle()
//            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        binding.itemGroup.llSettingPersonalGroup.setOnClickListener {
//            val bundle = Bundle()
//            val intent = Intent(
//                    this, Class.forName(ModuleClassConstants.SETTING_USER)
//            )
//            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        binding.itemGroup.llApproveMember.setOnClickListener {
//            try {
//                val intent = Intent(
//                        this@SettingConversationDetailActivity,
//                        SettingBrowseMemberActivity::class.java
//                )
//                val bundle = Bundle()
//                bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(groupChat))
//                bundle.putInt(
//                        ChatConstants.IS_CONFIRM_NEW_MEMBER,
//                        groupChat.isConfirmNewMember
//                )
//                intent.putExtras(bundle)
//                startActivity(intent)
//            } catch (e: Exception) {
//                Timber.e(e.message)
//            }
//        }
//
//        binding.itemGroup.listPinned.setOnClickListener {
//            val intent = Intent(
//                    this@SettingConversationDetailActivity,
//                    GroupNewsManagerActivity::class.java
//            )
//            val bundle = Bundle()
//            bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(groupChat))
//            bundle.putInt(AppConstants.KEY_GROUP_NEWS, 0)
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        binding.itemGroup.llGroupCalendar.setOnClickListener {
//            val intent = Intent(
//                    this@SettingConversationDetailActivity,
//                    GroupCalendarActivity::class.java
//            )
////            val bundle = Bundle()
////            bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(groupChat))
////            bundle.putInt(AppConstants.KEY_GROUP_NEWS, 0)
////            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        /**
//         * bình chọn
//         */
//        binding.itemGroup.llVote.setOnClickListener {
//            val intent = Intent(
//                    this@SettingConversationDetailActivity,
//                    GroupNewsManagerActivity::class.java
//            )
//            val bundle = Bundle()
//            bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(groupChat))
//            bundle.putInt(AppConstants.KEY_GROUP_NEWS, 1)
//            bundle.putInt(ConversationTypeConstants.PERMISSION_VOTE, groupChat.myPermission)
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
        binding.llChangeBackground.setOnClickListener {
            imageType = 0
            PhotoPickerUtils.showImagePickerChooseAvatar(
                this, pickerImageIntent
            )
            PhotoLoadUtils.loadImageAvatarGroupByGlide(
                binding.ivAvatarGroup,
                linkBackGround
            )
        }

        binding.ivAvatarGroup.setOnClickListener {
            AppUtils.showMediaAvatar(getContext(), groupChat.avatar, 0)
        }

    }

    override fun onResume() {
        super.onResume()
        getDetailGroup(groupChat.conversationId)
    }

    @SuppressLint("IntentWithNullActionLaunch")
    override fun onClick(view: View) {
        when (view) {
//            binding.llMediaList -> {
//                val intent = Intent(
//                        this, DetailMediaActivity::class.java
//                )
//                val bundle = Bundle()
//                bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
//                intent.putExtras(bundle)
//                startActivity(intent)
//            }
//
//            binding.itemGroup.llSettingPersonalGroup -> {
//                val bundle = Bundle()
//                val intent = Intent(
//                        this, Class.forName(ModuleClassConstants.SETTING_USER)
//                )
//                bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
//                intent.putExtras(bundle)
//                startActivity(intent)
//            }
//
//            binding.ibNotify -> {
//                btnNotify = !btnNotify
//                if (!btnNotify) {
//                    binding.ibNotify.setImageResource(R.drawable.ic_off_notification)
//                    binding.tvNotify.text = getString(R.string.turn_on_notify)
//                    setNotify(groupChat.conversationId, btnNotify)
//
//                } else {
//                    binding.ibNotify.setImageResource(R.drawable.ic_notification_news_header)
//                    binding.tvNotify.text = getString(R.string.turn_off_notify)
//                    setNotify(groupChat.conversationId, btnNotify)
//                }
//
//            }
        }
    }

    //------------------------------------Handle SocketChat---------------------------------------//




    /**
     * Sự kiện thay đổi ảnh nhóm
     */
    private var pickerImageIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult())  {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent = it.data!!
            localMedia = PictureSelector.obtainSelectorList(data) as ArrayList<LocalMedia>
            uploadToCloudinary(localMedia[0])
            Timber.d("localMeddia %s",localMedia[0])
            when (groupChat.type) {
                AppConstants.TYPE_PRIVATE -> {
                    PhotoLoadUtils.loadImageAvatarGroupByGlide(
                            binding.ivAvatarGroup,
                            localMedia[0].realPath
                    )
                }

                else -> {
                    PhotoLoadUtils.loadImageAvatarGroupByGlide(
                            binding.ivAvatarGroup,
                            localMedia[0].realPath
                    )
                }
            }
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
                val url = resultData["url"].toString()
                if(imageType == 1){
                    changeAvatarGroup(groupChat.conversationId, url)
                }else{
                    changeBackgroundGroup(groupChat.conversationId, url)
                }
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                Log.d("error " + error.description, "error")
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                Log.d("Reshedule " + error.description, "Reshedule")
            }
        }).dispatch()
    }



    private fun dialogChangeGroupChat(conversationId: String, nameGroup: String) {
        dialogChangeNameGroup = DialogChangeNameGroup.Builder(
                this, nameGroup
        ).setListener(object : DialogChangeNameGroup.OnListener {
            override fun onChangeNameGroupChat(nameGroup: String) {
                val changeName = ChangeName()
                changeName.conversationId = conversationId
                changeName.name = nameGroup.trim()
                changeNameGroup(groupChat.conversationId, nameGroup.trim())
            }
        })
        dialogChangeNameGroup!!.show()
    }

    /**
     * api đổi tên nhóm
     */

    private fun changeNameGroup(idGroup: String, name: String) {
        val formatName = AppUtils.fromHtml(name)
        binding.tvNameGroup.text = formatName
        EasyHttp.post(this)
            .api(ChangeNameGroupApi.params(idGroup, binding.tvNameGroup.text.toString()))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        groupChat.name = binding.tvNameGroup.text.toString()
                        toast("Đổi tên nhóm thành công")
                    } else {
                        toast(data.getData())
                    }
                }

            })
    }

    /**
     * api đổi avatar nhóm
     */

    private fun changeAvatarGroup(idGroup: String, avatar: String) {
        EasyHttp.post(this).api(ChangeAvatarGroupApi.params(idGroup, avatar))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        toast("Đổi avatar nhóm thành công")
                    } else {
                        toast(data.getData())
                    }
                }
            })
    }

    /**
     * api đổi background nhóm
     */

    private fun changeBackgroundGroup(idGroup: String, avatar: String) {
        EasyHttp.post(this).api(ChangeBackgroundGroupApi.params(idGroup, avatar))
            .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                override fun onHttpSuccess(data: HttpData<Any>) {
                    if (data.isRequestSucceed()) {
                        toast("Đổi background nhóm thành công")
                        finish()
                    } else {
                        toast(data.getData())
                    }
                }
            })
    }


    /**
     * api ghim cuộc trò chuyện
     */
//    private fun setPinned(idGroup: String) {
//        EasyHttp.post(this).api(PinnedGroupApi.params(idGroup))
//                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
//                    override fun onHttpSuccess(data: HttpData<Any>) {
//                        if (!data.isRequestSucceed()) {
//                            if (data.isRequestError()) {
//                                toast(data.getMessage())
//                            }
//                            Timber.e(
//                                    "${
//                                        AppApplication.applicationContext()
//                                                .getString(R.string.error_message)
//                                    } ${data.getMessage()}"
//                            )
//                            postDelayed({
//                                binding.itemGroup.swSettingPersonal.isChecked = false
//                            }, 500)
//                        } else {
//                            if (binding.itemPersonal.swSettingPersonal.isChecked) {
//                                groupChat.isPinned = 1
//                            } else {
//                                groupChat.isPinned = 0
//                            }
//                        }
//                    }
//                })
//    }

    private fun setOutConversation(idGroup: String) {
        EasyHttp.post(this).api(OutGroupApi.params(idGroup))
                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                    override fun onHttpSuccess(data: HttpData<Any>) {
                        if (data.isRequestSucceed()) {
                            EventBus.getDefault().post(CurrentFragmentEventBus(3))
                            EventBus.getDefault().post(OutGroupEventBus(idGroup))
                            finish()
                            startActivity(HomeActivity::class.java)
                        } else {
                            toast(data.getData())
                        }
                    }

                })
    }

    /**
     * api ẩn cuộc trò chuyện
     */
//    private fun setHide(idGroup: String) {
//        EasyHttp.post(this).api(HideGroupApi.params(idGroup))
//                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
//                    override fun onHttpSuccess(data: HttpData<Any>) {
//                        if (data.isRequestSucceed()) {
//                            EventBus.getDefault().post(CurrentFragmentEventBus(3))
//                            if (groupChat.isHidden == 1) {
//                                groupChat.isHidden = 0
//                            } else {
//                                groupChat.isHidden = 1
//                                EventBus.getDefault().post(EventBusHideConversation(idGroup))
//                            }
//                            val intent = Intent(this@SettingConversationDetailActivity, HomeActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        } else {
//                            Timber.e(
//                                    "${
//                                        AppApplication.applicationContext()
//                                                .getString(R.string.error_message)
//                                    } ${data.getMessage()}"
//                            )
//                            hideDialog()
//                            binding.itemPersonal.swHideChat.isChecked = !swHide
//                            binding.itemGroup.swHideChat.isChecked = !swHide
//                        }
//                    }
//
//                    override fun onHttpFail(e: java.lang.Exception?) {
//                        Timber.e(
//                                "${
//                                    AppApplication.applicationContext()
//                                            .getString(R.string.error_message)
//                                } ${e?.message}"
//                        )
//                        hideDialog()
//                        binding.itemPersonal.swHideChat.isChecked = !swHide
//                        binding.itemGroup.swHideChat.isChecked = !swHide
//                    }
//                })
//    }


    /**
     * api lấy chi tiết cuộc trò chuyện
     */
    private fun getDetailGroup(idGroup: String) {
        EasyHttp.get(this).api(DetailGroupApi.params(idGroup))
                .request(object : HttpCallbackProxy<HttpData<GroupChat>>(this) {
                    override fun onHttpStart(call: Call?) {
                        //empty
                    }

                    override fun onHttpEnd(call: Call?) {
                        //empty
                    }

                    override fun onHttpFail(throwable: Throwable?) {
                        Timber.e(
                                "${
                                    AppApplication.applicationContext()
                                            .getString(R.string.error_message)
                                } ${throwable}"
                        )
                    }

                    override fun onHttpSuccess(data: HttpData<GroupChat>) {
                        if (data.isRequestSucceed()) {
                            groupChat = data.getData()!!
                            setDataDetail()
                        } else {
                            Timber.e(
                                    "${
                                        AppApplication.applicationContext()
                                                .getString(R.string.error_message)
                                    } ${data.getMessage()}"
                            )
                        }
                    }
                })
    }

    private fun setDataDetail() {
        when (groupChat.type) {
            AppConstants.TYPE_SYSTEM -> {
                //chưa phat triển
                binding.llDetail.hide()
            }

            AppConstants.TYPE_GROUP -> {
                binding.tvNameGroup.text = groupChat.name
                binding.itemPersonal.llPersonalChat.hide()
                binding.itemGroup.llGroupChat.show()
                binding.llProfileUser.hide()
                binding.llAddMember.show()

                PhotoLoadUtils.loadImageAvatarGroupByGlide(
                        binding.ivAvatarGroup,
                        groupChat.avatar
                )

                binding.ivAvatarGroup.setOnClickListener {
                    AppUtils.showMediaAvatar(getContext(), groupChat.avatar, 0)
                }

                binding.itemGroup.llLinkChat.setOnClickListener {
                    try {
                        val bundle = Bundle()
                        val intent = Intent(
                            this@SettingConversationDetailActivity,
                            QrGroupActivity::class.java
                        )
                        bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
                        intent.putExtras(bundle)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Timber.e(e.message)
                    }
                }


                when (groupChat.myPermission) {
                    // trưởng nhóm
                    ConversationTypeConstants.OWNER -> {
                        //chon avat
                        binding.ivUpdateAvatar.setOnClickListener {
                            imageType = 1
                            PhotoPickerUtils.showImagePickerChooseAvatar(
                                    this, pickerImageIntent
                            )
                            PhotoLoadUtils.loadImageAvatarGroupByGlide(
                                    binding.ivAvatarGroup,
                                    linkAvatar
                            )
                        }
                        //dialog name group
                        binding.tvNameGroup.setOnClickListener {
                            dialogChangeGroupChat(
                                    groupChat.conversationId,
                                    groupChat.name.trim()
                            )
                        }

                        binding.itemPersonal.llPersonalChat.hide()
                        binding.itemGroup.llGroupChat.show()
                        binding.itemGroup.llTransferGroupLeader.show()
                    }
                    //thành viên
                    ConversationTypeConstants.MEMBER -> {
                        //dialog name group
                        binding.tvNameGroup.setOnClickListener {
                            toast(R.string.no_permission_update)
                        }
                        //chon avat
                        binding.ivUpdateAvatar.setOnClickListener {
                            toast(R.string.no_permission_update)
                        }
                        binding.itemGroup.llGroupChat.show()
                        binding.itemPersonal.llPersonalChat.hide()
                        binding.itemGroup.llTransferGroupLeader.hide()
                    }
                    //phó nhóm
                    ConversationTypeConstants.DEPUTY -> {
                        //chon avat
                        binding.ivUpdateAvatar.setOnClickListener {
                            PhotoPickerUtils.showImagePickerChooseAvatar(
                                    this, pickerImageIntent
                            )

                            PhotoLoadUtils.loadImageAvatarGroupByGlide(
                                    binding.ivAvatarGroup,
                                    linkAvatar
                            )
                        }

//                        dialog name group
                        binding.tvNameGroup.setOnClickListener {
                            dialogChangeGroupChat(
                                    groupChat.conversationId,
                                    groupChat.name.trim()
                            )
                        }
                        binding.itemPersonal.llPersonalChat.hide()
                        binding.itemGroup.llGroupChat.show()
                    }
                }

                binding.ivUpdateAvatar.setBackgroundResource(R.drawable.ic_update_avatar)

//                binding.itemGroup.llSettingGroup.setOnClickListener {
//                    val bundle = Bundle()
//                    val intent = Intent(
//                            this, SettingChatActivity::class.java
//                    )
//                    bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
//                    intent.putExtras(bundle)
//                    startActivity(intent)
//                }

                //them member
                binding.llAddMember.setOnClickListener {
                    val bundle = Bundle()
                    val intent = Intent(
                            this, AddMemberGroupActivity::class.java
                    )
                    bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
                    intent.putExtras(bundle)
                    startActivity(intent)
                }

                binding.itemGroup.llOutGroup.setOnClickListener {
                    dialogOutGroup = DialogOutGroup.Builder(
                            this,
                            getString(R.string.title_out_group),
                            getString(R.string.policy_out_group),
                            getString(R.string.leave_the_group)
                    ).setListener(object : DialogOutGroup.OnListener {
                        override fun onPolicyConfirm(next: Int) {
                            if (groupChat.myPermission == ConversationTypeConstants.OWNER) {
                                DialogChooseLeaderBeforeOutGroup.Builder(
                                        this@SettingConversationDetailActivity,
                                        this@SettingConversationDetailActivity,
                                        groupChat.conversationId
                                ).setOnChangeLeaderListener(object : DialogChooseLeaderBeforeOutGroup.OnChangeLeaderListener {
                                    override fun onChangeLeader() {
                                        setOutConversation(groupChat.conversationId)
                                    }
                                }).show()
                            } else {
                                setOutConversation(groupChat.conversationId)
                            }
                            dialogOutGroup!!.dismiss()
                        }
                    })
                    dialogOutGroup!!.show()
                }

                binding.itemGroup.llDeleteHistoryChat.setOnClickListener {
                    dialogOutGroup = DialogOutGroup.Builder(
                            this,
                            getString(R.string.common_confirm),
                            getString(R.string.policy_delete_group),
                            getString(R.string.delete_the_group)
                    ).setListener(object : DialogOutGroup.OnListener {
                        override fun onPolicyConfirm(next: Int) {
                            deleteGroup(groupChat.conversationId)
                        }
                    })
                    dialogOutGroup!!.show()
                }

                binding.itemGroup.llListMember.setOnClickListener {
//                    if (groupChat.myPermission == 2 || groupChat.myPermission == 1) {
                        val intent = Intent(
                                this, ManagerMemberActivity::class.java
                        )
                        val bundle = Bundle()
                        bundle.putString(ChatConstants.GROUP_DATA, Gson().toJson(groupChat))
                        intent.putExtras(bundle)
                        startActivity(intent)
//                    } else {
//                        val bundle = Bundle()
//                        val intent = Intent(
//                                this, ListMemberActivity::class.java
//                        )
//                        bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(groupChat))
//                        intent.putExtras(bundle)
//                        startActivity(intent)
//                    }
                }
            }

            AppConstants.TYPE_PRIVATE -> {
                binding.itemPersonal.llPersonalChat.show()
                binding.itemGroup.llGroupChat.hide()

                binding.itemPersonal.tvCreateUserPersonal.text =
                        String.format(
                                "%s %s",
                                getString(R.string.create_group_with),
                                groupChat.name.trim()
                        )
                binding.itemPersonal.tvAddUserPersonal.text = String.format(
                        "%s %s %s",
                        getString(R.string.add),
                        groupChat.name.trim(),
                        getString(R.string.join_group)
                )

                binding.ivAvatarGroup.setOnClickListener {
                    AppUtils.showMediaAvatar(getContext(), groupChat.avatar, 0)
                }

                binding.ivUpdateAvatar.setBackgroundResource(R.drawable.ic_avatar)
                //trang cá nhân user
                binding.llProfileUser.setOnClickListener {
                    var idMember = ""
                    for (i in groupChat.members.indices) {
                        if (member[i].userId != UserCache.getUser().id) {
                            idMember = member[i].userId
                            break
                        }
                    }
                    try {
//                        val intent = Intent(
//                                this, Class.forName(ModuleClassConstants.INFO_CUSTOMER)
//                        )
//                        intent.putExtra(
//                                AppConstants.ID_USER,
//                                idMember
//                        )
//                        startActivity(intent)
                    } catch (e: Exception) {
                        Timber.e(e.message)
                    }

                }

                binding.itemPersonal.llDeleteHistoryChat.setOnClickListener {
                    dialogOutGroup = DialogOutGroup.Builder(
                            this,
                            getString(R.string.common_confirm),
                            getString(R.string.policy_delete_group),
                            getString(R.string.delete_the_group)
                    ).setListener(object : DialogOutGroup.OnListener {
                        override fun onPolicyConfirm(next: Int) {
                            deleteGroup(groupChat.conversationId)
                        }
                    })
                    dialogOutGroup!!.show()
                }

                binding.tvNameGroup.setCompoundDrawables(null, null, null, null)

                binding.itemPersonal.llPersonalChat.show()

                handleShowActionByUserStatusAndUserBlock()
            }
        }

        //set notification chat
        if (groupChat.isNotify == 0) {
            btnNotify = false
            binding.ibNotify.setImageResource(R.drawable.ic_turn_off_notification)
            binding.tvNotify.text = getString(R.string.turn_on_notify)
        } else {
            btnNotify = true
            binding.ibNotify.setImageResource(R.drawable.ic_notification_news_header)
            binding.tvNotify.text = getString(R.string.turn_off_notify)
        }

        when (groupChat.myPermission) {
            ConversationTypeConstants.OWNER -> {
                binding.itemGroup.llSettingGroup.hide()
            }

            ConversationTypeConstants.DEPUTY -> {
                binding.itemGroup.llSettingGroup.hide()
            }

            ConversationTypeConstants.MEMBER -> {
                binding.itemGroup.llSettingGroup.hide()
            }

            else -> {
                binding.itemGroup.llSettingGroup.hide()
            }
        }
        member.addAll(groupChat.members)
        when (groupChat.type) {
            AppConstants.TYPE_GROUP -> {
                if (groupChat.isPinned == 1) {
                    binding.itemGroup.swSettingPersonal.isChecked = true
                    swPinned = false
                } else {
                    binding.itemGroup.swSettingPersonal.isChecked = false
                    swPinned = true
                }

                if (groupChat.isHidden == 1) {
                    binding.itemGroup.swHideChat.isChecked = true
                    binding.itemGroup.llPinChat.hide()
                    swHide = false
                } else {
                    binding.itemGroup.swHideChat.isChecked = false
                    binding.itemGroup.llPinChat.hide()
                    swHide = true
                }
            }

            AppConstants.TYPE_PRIVATE -> {
                if (groupChat.isPinned == 1) {
                    binding.itemPersonal.swSettingPersonal.isChecked = true
                    binding.itemPersonal.lnPinned.show()
                    swPinned = false
                } else {
                    binding.itemPersonal.swSettingPersonal.isChecked = false
                    swPinned = true
                }

                if (groupChat.isHidden == 1) {
                    binding.itemPersonal.swHideChat.isChecked = true
                    binding.itemPersonal.lnPinned.hide()
                    swHide = false
                } else {
                    binding.itemPersonal.swHideChat.isChecked = false
                    swHide = true
                }
            }
        }
    }

    private fun handleShowActionByUserStatusAndUserBlock() {
        if (groupChat.userStatus == AppConstants.STATUS_ACTIVE) {
            binding.tvNameGroup.text = groupChat.name
            PhotoLoadUtils.loadImageAvatarByGlide(
                    binding.ivAvatarGroup,
                    groupChat.avatar
            )
            binding.llMuteNotify.show()
            binding.itemPersonal.lnPinned.show()
            binding.itemPersonal.lnHidden.show()
        } else {
            binding.tvNameGroup.text = getString(R.string.undefine_account)
            binding.ivAvatarGroup.setImageResource(R.drawable.ic_user_default)
            binding.llProfileUser.hide()
            binding.llChangeBackground.hide()
            binding.llMuteNotify.hide()
            binding.itemPersonal.lnPinned.hide()
            binding.itemPersonal.lnHidden.hide()
        }
    }


    /**
     * api xoá lịch sử trò chuyện
     */
    private fun deleteGroup(idGroup: String) {
        EasyHttp.post(this).api(DeleteGroupApi.params(idGroup))
                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
                    override fun onHttpSuccess(data: HttpData<Any>) {
                        if (data.isRequestSucceed()) {
                            EventBus.getDefault().post(DeleteEventBus(groupChat.conversationId))
                            startActivity(HomeActivity::class.java)
                            finish()
                            dialogOutGroup!!.dismiss()
                        }
                    }
                })
    }

//    private fun changeNameGroup(idGroup: String, name: String) {
//        val formatName = AppUtils.fromHtml(name)
//        binding.tvNameGroup.text = formatName
//        EasyHttp.post(this)
//                .api(ChangeNameGroupApi.params(idGroup, binding.tvNameGroup.text.toString()))
//                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
//                    override fun onHttpSuccess(data: HttpData<Any>) {
//                        if (data.isRequestSucceed()) {
//                            groupChat.name = binding.tvNameGroup.text.toString()
//                            toast("Đổi tên nhóm thành công")
//                        } else {
//                            toast(data.getData())
//                        }
//                    }
//                })
//    }
//
//    private fun changeAvatarGroup(idGroup: String, avatar: String) {
//        EasyHttp.post(this).api(ChangeAvatarGroupApi.params(idGroup, avatar))
//                .request(object : HttpCallbackProxy<HttpData<Any>>(this) {
//                    override fun onHttpSuccess(data: HttpData<Any>) {
//                        if (data.isRequestSucceed()) {
//                            toast("Đổi avatar nhóm thành công")
//                        } else {
//                            toast(data.getData())
//                        }
//                    }
//
//                })
//    }

//    override fun onActionDone(isConfirm: Boolean) {
//        if (isConfirm) {
//            setHide(groupChat.conversationId)
//        } else {
//            binding.itemPersonal.swHideChat.isChecked = !swHide
//            binding.itemGroup.swHideChat.isChecked = !swHide
//        }
//    }
//
//    @Subscribe
//    fun onCloseScreen(event: CloseScreenEventBus) {
//        finish()
//    }
}