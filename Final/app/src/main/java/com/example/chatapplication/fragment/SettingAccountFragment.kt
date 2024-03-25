package com.example.chatapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.example.chatapplication.R
import com.example.chatapplication.activity.ChangePasswordActivity
import com.example.chatapplication.activity.HomeActivity
import com.example.chatapplication.activity.InfoCustomerActivity
import com.example.chatapplication.activity.LoginActivity
import com.example.chatapplication.adapter.GroupAdapter
import com.example.chatapplication.app.AppFragment
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.FragmentPhoneBookBinding
import com.example.chatapplication.databinding.FragmentSettingAccountBinding
import com.example.chatapplication.dialog.ConfirmDialog
import com.example.chatapplication.dialog.DialogSelectionChat
import com.example.chatapplication.manager.ActivityManager
import com.example.chatapplication.model.entity.GroupChat
import com.example.chatapplication.utils.AppUtils
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.paginate.Paginate
import okhttp3.Call
import timber.log.Timber

class SettingAccountFragment : AppFragment<HomeActivity>(){
    private lateinit var binding: FragmentSettingAccountBinding

    private var user = UserCache.getUser()

    companion object {

    }

    override fun getLayoutView(): View {
        binding = FragmentSettingAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {

        binding.lnChangePassword.setOnClickListener {
            val intent = Intent(
                requireContext(),
                ChangePasswordActivity::class.java
            )
            startActivity(intent)
        }

        binding.lnViewProfile.setOnClickListener {
            val intent = Intent(
                requireContext(),
                InfoCustomerActivity::class.java
            )
            val bundle = Bundle()
            bundle.putString(
                AppConstants.ID_USER, UserCache.getUser().id)
            intent.putExtras(bundle)
            startActivity(intent)
        }



        //Logout
        binding.lnLogout.setOnClickListener {
            AppUtils.disableClickAction(binding.lnLogout, 1000)
            ConfirmDialog.Builder(
                requireContext(),
                getString(R.string.logout_header),
                getString(R.string.logout_message),
                getString(R.string.confirm),
                getString(R.string.common_cancel)
            ).setOnActionDone(object : ConfirmDialog.OnActionDone {
                override fun onActionDone(isConfirm: Boolean) {
                    if (isConfirm) {
                        try {
                            user = UserCache.getUser()
                            user.id = ""
                            UserCache.saveUser(user)
                            val intent = Intent(
                                requireContext(),
                                LoginActivity::class.java
                            )
                            val bundle = Bundle()
                            intent.putExtras(bundle)
                            startActivity(intent)
                            ActivityManager.getInstance().finishAllActivities()
                        } catch (e: Exception) {
                            Timber.e(e.message)
                        }
                    }
                }
            }).show()
        }
        }


}


