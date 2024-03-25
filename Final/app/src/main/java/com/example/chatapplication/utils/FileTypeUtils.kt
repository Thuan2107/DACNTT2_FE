package com.example.chatapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.example.chatapplication.R
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.constant.UploadTypeConstants
import com.luck.picture.lib.entity.LocalMedia
import java.text.DecimalFormat
import java.util.ArrayList

object FileTypeUtils {
    fun getStringSizeLengthFile(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (size < sizeMb) return df.format((size / sizeKb).toDouble()) + " KB" else if (size < sizeGb) return df.format(
            (size / sizeMb).toDouble()
        ) + " MB" else if (size < sizeTerra) return df.format((size / sizeGb).toDouble()) + " GB"
        return ""
    }

    fun getMimeType(url: String): String {
        try {
            return url.substring(url.lastIndexOf(".") + 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setLogoImageToFile(context: Context, imageView: ImageView, typeMedia: String?) {
        when (typeMedia) {
            UploadTypeConstants.TYPE_JPG, UploadTypeConstants.TYPE_JPEG, UploadTypeConstants.TYPE_PNG, UploadTypeConstants.TYPE_SVG -> {
                imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_photo))
            }

            UploadTypeConstants.TYPE_MP4 -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_video))

            else -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_attach))
        }
    }

    fun checkFileSizeToUpload(listMedia: ArrayList<LocalMedia>, activity: AppActivity): Boolean {
        for (mediaItem in listMedia) {
            if (mediaItem.size > (100 * 1000000).toLong()) {//Max size: 100MB, 1MB = 10^6 long
                activity.toast(activity.getString(R.string.max_size_image_video))
                return false
            }
        }

        return true
    }
}