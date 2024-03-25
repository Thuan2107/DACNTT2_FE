package com.example.chatapplication.dialog

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.databinding.DialogNameChangeGroupBinding
import com.example.chatapplication.utils.AppUtils


class DialogChangeNameGroup {
    class Builder (
        context: Context, nameGroup: String
    ) : CommonDialog.Builder<Builder>(context), BaseAdapter.OnItemClickListener {
        private var listener: OnListener? = null
        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        private var binding: DialogNameChangeGroupBinding =
            DialogNameChangeGroupBinding.inflate(
                LayoutInflater.from(context)
            )

        private var isShowing = false

        init {
            setCustomView(binding.root)
            setActionView(false)
            setTitleView(false)
            setCancelable(true)
            setGravity(Gravity.CENTER)
            setAnimStyle(AnimAction.ANIM_IOS)
            setWidth(Resources.getSystem().displayMetrics.widthPixels * 9 / 10)
            binding.edtChangeName.setText(nameGroup)
            binding.tvUiCancel.setOnClickListener {
                autoDismiss()
            }

            binding.tvUiConfirm.setOnClickListener {
                AppUtils.disableClickAction(binding.tvUiConfirm, 1000)
                if (binding.edtChangeName.text.toString() == "") {
                    listener!!.onChangeNameGroupChat(nameGroup)
                    autoDismiss()
                } else {
                    listener!!.onChangeNameGroupChat(
                        binding.edtChangeName.text.toString()
                    )
                    autoDismiss()
                }
            }


            binding.edtChangeName.setOnClickListener {
                isShowing = false
            }
        }

        override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
            //empty
        }

    }

    interface OnListener {
        fun onChangeNameGroupChat(
            nameGroup: String
        )
    }
}
