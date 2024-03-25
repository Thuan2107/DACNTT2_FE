package com.example.chatapplication.activity


import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.ChatConstants
import com.example.chatapplication.databinding.ActivityQrGroupBinding
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.utils.AppUtils
import com.google.gson.Gson
import timber.log.Timber



class QrGroupActivity : AppActivity() {
    private lateinit var binding: ActivityQrGroupBinding
    var group = GroupChat()

    override fun getLayoutView(): View {
        binding = ActivityQrGroupBinding.inflate(layoutInflater)
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

    override fun initView() {

    }

    @SuppressLint("SetTextI18n")
    override fun initData() {

        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.GROUP_DATA)) {
                group = Gson().fromJson(
                    bundleIntent.getString(AppConstants.GROUP_DATA),
                    GroupChat::class.java
                )
            }
        }

        binding.tvLink.text = group.linkJoin

        binding.btnCopy.setOnClickListener {
            binding.btnCopy.isEnabled = false
            postDelayed({ binding.btnCopy.isEnabled = true }, 1000)
//            val clipboard: ClipboardManager =
//                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            clipboard.text = binding.tvLink.text
            AppUtils.copyText(this, group.linkJoin)
            toast("Đã copy văn bản")
        }

    }
}