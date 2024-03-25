package com.example.chatapplication.dialog

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.databinding.ConfirmDialogBinding
import com.example.chatapplication.utils.AppUtils


class ConfirmDialog {
    class Builder(
        context: Context,
        header: String,
        content: String,
        confirmTextBtn: String,
        cancelTextBtn: String
    ) : BaseDialog.Builder<Builder>(context) {
        private val binding: ConfirmDialogBinding =
            ConfirmDialogBinding.inflate(LayoutInflater.from(context))

        private lateinit var onActionDone: OnActionDone

        fun setOnActionDone(onActionDone: OnActionDone): Builder = apply {
            this.onActionDone = onActionDone
        }

        init {
            setAnimStyle(AnimAction.ANIM_TOAST)
            setGravity(Gravity.CENTER)
            setWidth(Resources.getSystem().displayMetrics.widthPixels * 15 / 20)
            setContentView(binding.root)
            binding.tvHeader.isVisible = header.isNotEmpty()
            binding.tvHeader.text = AppUtils.fromHtml(header)
            binding.tvContent.text = AppUtils.fromHtml(content)
            binding.tvUiConfirm.text = confirmTextBtn
            binding.tvUiCancel.text = cancelTextBtn

            binding.tvUiCancel.setOnClickListener {
                onActionDone.onActionDone(false)
                dismiss()
            }
            binding.tvUiConfirm.setOnClickListener {
                onActionDone.onActionDone(true)
                dismiss()
            }
        }
    }

    interface OnActionDone {
        fun onActionDone(isConfirm: Boolean)
    }
}