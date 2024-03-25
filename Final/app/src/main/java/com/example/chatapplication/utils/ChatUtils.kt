package com.example.chatapplication.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.activity.HomeActivity
import com.example.chatapplication.app.AppApplication.Companion.socketChat
import com.example.chatapplication.cache.ConfigCache
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.model.entity.Sender
import com.example.chatapplication.model.entity.UserTag
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import kotlin.math.ln

/**
 * @Author: Phạm Văn Nhân
 * @Date: 05/10/2022
 */
object ChatUtils {

    fun emitSocket(key: String?, ojb: Any?) {
        try {
            if (socketChat != null) {
                Timber.e("%s: %s", "Tình trạng kết nối socket", socketChat?.connected())
                if (!socketChat!!.connected()) {
                    socketChat!!.connect()
                } else {
                    val gson = Gson()
                    Timber.d("%s : %s", key, gson.toJson(ojb))
                    socketChat!!.emit(key, JSONObject(gson.toJson(ojb)))
                }
            } else {
                Timber.e("%s: %s", "Tình trạng kết nối socket", false)
            }
        } catch (e: JSONException) {
            Timber.e("%s: %s", "Tình trạng kết nối socket", false)
        }
    }

    fun setTagNameFromKeyTagName(message: String, user: Sender): SpannableStringBuilder {
        val colorSpan = ForegroundColorSpan(Color.parseColor("#15a4fa"))
        val newBuilder = SpannableStringBuilder(message)
        val spannableString = SpannableString("@${user.fullName}")
        spannableString.setSpan(
                colorSpan,
                0,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val startIndex = message.indexOf(message)
        if (startIndex != -1) {
            newBuilder.replace(
                    startIndex,
                    startIndex + message.length,
                    spannableString
            )
        }
        return newBuilder
    }

    fun getViewHeightOrWidth(view: View, heightOrWidth: Int): Int {
        val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val deviceWidth: Int
        val size = Point()
        display.getSize(size)
        deviceWidth = size.x
        val widthMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthMeasureSpec, heightMeasureSpec)
        return if (heightOrWidth == 0) {
            view.measuredHeight
        } else view.measuredWidth
    }


    fun formatTagNameNotHighLight(
            message: String,
            list: ArrayList<UserTag>
    ): String {
        var finalMessage = message
        for (tag in list) {
            val tagName = if (tag.user.userId == AppConstants.USER_TAG_ALL_ID)
                "@All"
            else
                "@${tag.user.fullName}"
            finalMessage = finalMessage.replace(tag.key, tagName)
        }

        return finalMessage
    }


    fun setTagNameFromKeyTagNameInViewText(
            message: String,
            list: ArrayList<UserTag>
    ): SpannableStringBuilder {
        val newBuilder = SpannableStringBuilder(message)

        for (tag in list) {
            val textString = if (tag.user.userId == AppConstants.USER_TAG_ALL_ID) "@All" else "@${tag.user.fullName}"
            val startIndex = newBuilder.indexOf(tag.key)
            if (startIndex != -1) {
                newBuilder.replace(
                        startIndex,
                        startIndex + tag.key.length,
                        textString
                )
            }
        }

        return newBuilder
    }

    @SuppressLint("IntentWithNullActionLaunch")
//    fun getLinkTagNameAndPhoneAction(
//            list: ArrayList<UserTag>,
//            context: Context,
//            message: String
//    ): ArrayList<Pair<String, View.OnClickListener>> {
//        val links = ArrayList<Pair<String, View.OnClickListener>>()
//        for (tag in list) {
//            if (tag.user.userId != AppConstants.USER_TAG_ALL_ID) {
//                val pair =
//                        Pair(
//                                "@${tag.user.fullName}", View.OnClickListener {
//                            if (tag.user.userId == UserCache.getUser().id) {
//                                context.startActivity(Intent(context, HomeActivity::class.java))
//                            } else {
////                                val intent =
////                                        Intent(
////                                                context,
////                                                Class.forName(ModuleClassConstants.INFO_CUSTOMER)
////                                        )
////                                intent.putExtra(AppConstants.ID_USER, tag.user.userId)
////                                context.startActivity(intent)
//                                Log.d("info customer", "info customer")
//                            }
//                        }
//                        )
//                links.add(pair)
//            } else {
//                val pair = Pair("@All", View.OnClickListener {
//                    //Do nothing
//                })
//                links.add(pair)
//            }
//        }
//
//        val positionOfNumberLink = AppUtils.getAllPositionOfNumberLinkOfChat(message)
//        for (item in positionOfNumberLink) {
//            if (message.substring(item.first, item.second).replace(" ", "").length > 6) {
//                if (item.first != 0) {
//                    if (!message.substring(item.first - 1, item.second).contains("@")) {
//                        val clickListener = Pair(
//                                message.substring(item.first, item.second), View.OnClickListener {
//                            DialogPhoneLinkAction.Builder(
//                                    context,
//                                    message.substring(item.first, item.second)
//                            ).show()
//                        }
//                        )
//                        links.add(clickListener)
//                    }
//                } else {
//                    if (!message.substring(0, item.second).contains("@")) {
//                        val clickListener = Pair(
//                                message.substring(0, item.second), View.OnClickListener {
//                            DialogPhoneLinkAction.Builder(
//                                    context,
//                                    message.substring(0, item.second)
//                            ).show()
//                        }
//                        )
//                        links.add(clickListener)
//                    }
//                }
//            }
//        }
//        return links
//    }

//    fun getLinkPhoneLongClick(
//            context: Context,
//            message: String
//    ): ArrayList<Pair<String, View.OnLongClickListener>> {
//        val links = ArrayList<Pair<String, View.OnLongClickListener>>()
//        val positionOfNumberLink = AppUtils.getAllPositionOfNumberLinkOfChat(message)
//        for (item in positionOfNumberLink) {
//            if (message.substring(item.first, item.second).replace(" ", "").length > 6) {
//                if (item.first != 0) {
//                    if (!message.substring(item.first - 1, item.second).contains("@")) {
//                        val clickListener = Pair(
//                                message.substring(item.first, item.second), View.OnLongClickListener {
//                            DialogPhoneLinkAction.Builder(
//                                    context,
//                                    message.substring(item.first, item.second)
//                            ).show()
//                            true
//                        }
//                        )
//                        links.add(clickListener)
//                    }
//                } else {
//                    if (!message.substring(0, item.second).contains("@")) {
//                        val clickListener = Pair(
//                                message.substring(0, item.second), View.OnLongClickListener {
//                            DialogPhoneLinkAction.Builder(
//                                    context,
//                                    message.substring(0, item.second)
//                            ).show()
//                            true
//                        }
//                        )
//                        links.add(clickListener)
//                    }
//                }
//            }
//        }
//        return links
//    }

    fun TextView.makeLinksClick(links: ArrayList<Pair<String, View.OnClickListener>>) {
        val spannableString = SpannableString(this.text)

        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
                    textPaint.color = textPaint.linkColor
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    textPaint.isUnderlineText = false
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }

            }
            val startIndexOfLink = this.text.indexOf(link.first)
            if (startIndexOfLink != -1) {
                spannableString.setSpan(
                        clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        this.movementMethod =
                LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    fun TextView.makeLinksLongClick(links: ArrayList<Pair<String, View.OnLongClickListener>>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
                    textPaint.color = textPaint.linkColor
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onLongClick(view)
                }

            }
            val startIndexOfLink = this.text.indexOf(link.first)
            if (startIndexOfLink != -1) {
                spannableString.setSpan(
                        clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        this.movementMethod =
                LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    //re
    fun translateReboundingView(
            itemView: View, viewHolder: RecyclerView.ViewHolder, dX: Float
    ) {
        val swipeDismissDistanceHorizontal = (itemView.width * 0.8f).toDouble()
        val dragFraction = ln(1 + dX / swipeDismissDistanceHorizontal / ln(3.0))
        val dragTo = dragFraction * swipeDismissDistanceHorizontal * 0.8f
        viewHolder.itemView.translationX = dragTo.toFloat()
    }
}