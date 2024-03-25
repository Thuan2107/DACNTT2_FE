package com.example.chatapplication.action

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/08
 *    desc   : mục đích thanh tiêu đề
 */
interface TitleBarAction : OnTitleBarListener {

    /**
     * Lấy đối tượng thanh tiêu đề
     */
    fun getTitleBar(): TitleBar?

    /**
     * Đặt tiêu đề của thanh tiêu đề
     */
    fun setTitle(@StringRes id: Int) {
        getTitleBar()?.setTitle(id)
    }

    /**
     * Đặt tiêu đề của thanh tiêu đề
     */
    fun setTitle(title: CharSequence?) {
        getTitleBar()?.title = title
    }

    /**
     * Đặt tiêu đề bên trái của thanh tiêu đề
     */
    fun setLeftTitle(id: Int) {
        getTitleBar()?.setLeftTitle(id)
    }

    fun setLeftTitle(text: CharSequence?) {
        getTitleBar()?.leftTitle = text
    }

    fun getLeftTitle(): CharSequence? {
        return getTitleBar()?.leftTitle
    }

    /**
     * Đặt tiêu đề bên phải của thanh tiêu đề
     */
    fun setRightTitle(id: Int) {
        getTitleBar()?.setRightTitle(id)
    }

    fun setRightTitle(text: CharSequence?) {
        getTitleBar()?.rightTitle = text
    }




    fun getRightTitle(): CharSequence? {
        return getTitleBar()?.rightTitle
    }

    /**
     * Đặt biểu tượng bên trái của thanh tiêu đề
     */
    fun setLeftIcon(id: Int) {
        getTitleBar()?.setLeftIcon(id)
    }

    fun setLeftIcon(drawable: Drawable?) {
        getTitleBar()?.leftIcon = drawable
    }

    fun getLeftIcon(): Drawable? {
        return getTitleBar()?.leftIcon
    }

    /**
     * Đặt biểu tượng bên phải của thanh tiêu đề
     */
    fun setRightIcon(id: Int) {
        getTitleBar()?.setRightIcon(id)
    }

    fun setRightIcon(drawable: Drawable?) {
        getTitleBar()?.rightIcon = drawable
    }

    fun getRightIcon(): Drawable? {
        return getTitleBar()?.rightIcon
    }

    /**
     * Lấy đệ quy đối tượng TitleBar trong ViewGroup
     */
    fun obtainTitleBar(group: ViewGroup?): TitleBar? {
        if (group == null) {
            return null
        }
        for (i in 0 until group.childCount) {
            val view = group.getChildAt(i)
            if (view is TitleBar) {
                return view
            }
            if (view is ViewGroup) {
                val titleBar = obtainTitleBar(view)
                if (titleBar != null) {
                    return titleBar
                }
            }
        }
        return null
    }



}