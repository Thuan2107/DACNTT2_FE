package com.example.chatapplication.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapplication.R
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.databinding.DialogRemoveMemberBinding
import com.example.chatapplication.model.entity.MemberList
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils

class DialogRemoveMember {
    class Builder(context: Context, permission: Int, myPermission: Int, data: MemberList) :
        BaseDialog.Builder<Builder>(context), Runnable {
        private var listener: ClickButton? = null

        fun setListener(listener: ClickButton): Builder = apply {
            this.listener = listener
        }

        private val binding: DialogRemoveMemberBinding = DialogRemoveMemberBinding.inflate(
            LayoutInflater.from(context)
        )

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            binding.tvName.text = data.fullName
            when (data.permission) {
                0 -> {
                    binding.tvPosition.text = getString(R.string.member)
                }

                1 -> {
                    binding.tvPosition.text = getString(R.string.deputy)
                }

                else -> {
                    binding.tvPosition.text = getString(R.string.owner)
                }
            }
            binding.ivMessage.setOnClickListener {
                listener!!.onReviewConfirm(
                    4
                )
            }
            PhotoLoadUtils.loadImageAvatarByGlide(binding.imgAvatar, data.avatar)
            if (data.contactType == AppConstants.FRIEND || data.contactType == AppConstants.ITS_ME) binding.ivOnline.show()
            else binding.ivOnline.hide()


            binding.ivOnline.isSelected = data.isOnline == 1

            if (myPermission == 0) {
                binding.llRemoveMember.visibility = View.GONE
                binding.llDeputy.visibility = View.GONE
                binding.llRemoveDeputy.visibility = View.GONE
                binding.llPageUser.visibility = View.VISIBLE
                binding.llBlockUser.visibility = View.GONE
            } else if (myPermission == 1 && permission == 1 || permission == 2) {
                binding.llRemoveMember.visibility = View.GONE
                binding.llDeputy.visibility = View.GONE
                binding.llRemoveDeputy.visibility = View.GONE
                binding.llBlockUser.visibility = View.GONE
                binding.llPageUser.visibility = View.VISIBLE
            } else if (myPermission == 2 && permission == 0) {
                binding.llPageUser.visibility = View.VISIBLE
                binding.llRemoveMember.visibility = View.VISIBLE
                binding.llDeputy.visibility = View.VISIBLE
                binding.llBlockUser.visibility = View.GONE
                binding.llRemoveDeputy.visibility = View.GONE
            } else if (myPermission == 0 || permission == 0) {
                binding.llDeputy.visibility = View.GONE
                binding.llRemoveDeputy.visibility = View.GONE
            } else {
                binding.llPageUser.visibility = View.VISIBLE
                binding.llRemoveMember.visibility = View.VISIBLE
                binding.llDeputy.visibility = View.GONE
                binding.llRemoveDeputy.visibility = View.VISIBLE
                binding.llBlockUser.visibility = View.GONE
            }
            binding.llPageUser.setOnClickListener {
                listener!!.onReviewConfirm(
                    1
                )
            }

            binding.llDeputy.setOnClickListener {
                listener!!.onReviewConfirm(
                    2
                )
            }

            binding.llRemoveDeputy.setOnClickListener {
                listener!!.onReviewConfirm(
                    3
                )
            }
            binding.llBlockUser.setOnClickListener {
                listener!!.onReviewConfirm(
                    4
                )
            }


            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            initView()
            setContentView(binding.root)
            binding.llRemoveMember.setOnClickListener {
                listener!!.onReviewConfirm(
                    0
                )
            }


        }

        override fun run() {

        }

        /**
         * khởi tạo view
         */
        private fun initView() {

        }

    }

    interface ClickButton {
        fun onReviewConfirm(num: Int)
    }


}
