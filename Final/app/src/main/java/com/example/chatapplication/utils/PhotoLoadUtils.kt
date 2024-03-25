package com.example.chatapplication.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.toolbox.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chatapplication.R
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.cache.ConfigCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


object PhotoLoadUtils {
    //Custom link url để load hình
    fun getLinkPhoto(photo: String?): String {
//        return String.format("%s%s", ConfigCache.getConfig().apiUploadShort, photo)
        return ""
    }
    fun loadImageByGlide(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            // Start a new coroutine to load the image asynchronously
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val job = coroutineScope.launch {
                Glide.with(AppApplication.applicationContext())
                    .load(if (!url.contains("/")) getLinkPhoto(url) else url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .into(view)
            }
            // Store the coroutine job as a tag to the view
            view.tag = job
        } else {
            view.setImageResource(R.drawable.ic_default)
        }
    }



    fun loadImageBackgroundByGlide(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            // Start a new coroutine to load the image asynchronously
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val job = coroutineScope.launch {
                Glide.with(AppApplication.applicationContext())
                    .load(if (!url.contains("/")) getLinkPhoto(url) else url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.bg_white_default)
                    .error(R.drawable.bg_white_default)
                    .into(view)
            }
            // Store the coroutine job as a tag to the view
            view.tag = job
        } else {
            view.setImageResource(R.drawable.bg_white_default)
        }
    }



    fun loadImageBackgroundBrandByGlide(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            // Start a new coroutine to load the image asynchronously
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val job = coroutineScope.launch {
                Glide.with(AppApplication.applicationContext())
                    .load(if (!url.contains("/")) getLinkPhoto(url) else url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .transform(
                        MultiTransformation(
                            RoundedCorners(
                                AppApplication.applicationContext().resources.getDimension(R.dimen.dp_8).toInt()
                            )
                        )
                    )
                    .into(view)
            }
            // Store the coroutine job as a tag to the view
            view.tag = job
        } else {
            view.setImageResource(R.drawable.ic_default)
        }
    }




    fun loadImageAvatarByGlide(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            // Start a new coroutine to load the image asynchronously
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val job = coroutineScope.launch {
                Glide.with(AppApplication.applicationContext())
                    .load(if (!url.contains("/")) getLinkPhoto(url) else url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .transform(MultiTransformation(CircleCrop()))
                    .into(view)
            }
            // Store the coroutine job as a tag to the view
            view.tag = job
        } else {
            view.setImageResource(R.drawable.ic_user_default)
        }
    }

    fun loadImageAvatarGroupByGlide(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            // Start a new coroutine to load the image asynchronously
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val job = coroutineScope.launch {
                Glide.with(AppApplication.applicationContext())
                    .load(if (!url.contains("/")) getLinkPhoto(url) else url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_empty_group)
                    .error(R.drawable.ic_empty_group)
                    .transform(MultiTransformation(CircleCrop()))
                    .into(view)
            }
            // Store the coroutine job as a tag to the view
            view.tag = job
        } else {
            view.setImageResource(R.drawable.ic_empty_group)
        }
    }

    fun loadImageLinkJoinGroupByGlide(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            // Start a new coroutine to load the image asynchronously
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val job = coroutineScope.launch {
                Glide.with(AppApplication.applicationContext())
                    .load(if (!url.contains("/")) getLinkPhoto(url) else url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .transform(
                        MultiTransformation(
                            RoundedCorners(
                                AppApplication.applicationContext().resources.getDimension(
                                    R.dimen.dp_8
                                ).toInt()
                            )
                        )
                    )
                    .into(view)
            }
            // Store the coroutine job as a tag to the view
            view.tag = job
        } else {
            view.setImageResource(R.mipmap.ic_launcher)
        }
    }



    //--------------------------------------------Chat--------------------------------------------//

    @SuppressLint("ResourceType")
    fun loadPhoto(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            // Start a new coroutine to load the image asynchronously
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val job = coroutineScope.launch {
                Glide.with(view)
                    .load(if (!url.contains("/")) getLinkPhoto(url) else url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_empty_media)
                    .error(R.drawable.ic_empty_media)
                    .into(view)
            }
            // Store the coroutine job as a tag to the view
            view.tag = job
        } else {
            view.setImageDrawable(
                AppCompatResources.getDrawable(
                    AppApplication.applicationContext(),
                    R.drawable.ic_empty_media
                )
            )
        }
    }

    fun resizeImageClip(url: String?, view: ImageView) {
        try {
            Glide.with(view)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        val bitmap = Bitmap.createScaledBitmap(
                            resource,
                            AppApplication.widthDevice / 4,
                            AppApplication.heightDevice / 4,
                            false
                        )
                        view.layoutParams.height = bitmap.height
                        view.layoutParams.width = bitmap.width
                        view.setImageBitmap(bitmap)
                    }
                })
        } catch (ignored: java.lang.Exception) {
        }
    }


    private fun resizeBitmapChat(source: Bitmap, maxLength: Int): Bitmap? {
        return try {
            if (source.height > source.width) {
                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                var targetWidth = (maxLength * aspectRatio).toInt()
                if (targetWidth > maxLength) {
                    targetWidth = maxLength
                }
                Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            } else if (source.height == source.width) {
                Bitmap.createScaledBitmap(source, maxLength, maxLength, false)
            } else {
                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: java.lang.Exception) {
            source
        }
    }

    fun loadMediaImage(url: String?, imageView: ImageView) {
        if (!url.isNullOrEmpty()) {
            Glide.with(AppApplication.applicationContext())
                .load(url)
                .error(R.drawable.ic_default_post)
                .placeholder(R.drawable.ic_default_post)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        val bitmap: Bitmap =
                            resizeBitmapMediaSlider(resource.toBitmap(), imageView)
                        imageView.setImageBitmap(bitmap)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //Empty
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        Timber.e("Lỗi hình ${if (!url.contains("/")) getLinkPhoto(url) else url}")
                        imageView.setImageResource(R.drawable.ic_default_post)
                    }
                })
        } else {
            imageView.setImageResource(R.drawable.ic_default_post)
        }
    }

    fun resizeBitmapMediaSlider(source: Bitmap, imageView: ImageView): Bitmap {
        return try {
            val targetHeight: Int
            val targetWidth: Int
            if (source.height == source.width) {
                targetHeight = imageView.width
                targetWidth = imageView.width
            } else if (source.height > source.width) {
                if (source.height >= imageView.height) {
                    val aspectRatio =
                        source.width.toDouble() / source.height.toDouble()
                    targetHeight = imageView.height
                    targetWidth = (imageView.height * aspectRatio).toInt()
                } else {
                    val aspectRatio =
                        source.height.toDouble() / source.width.toDouble()
                    targetWidth = imageView.width
                    targetHeight = (imageView.width * aspectRatio).toInt()
                }
            } else {
                val aspectRatio =
                    source.height.toDouble() / source.width.toDouble()
                targetWidth = imageView.width
                targetHeight =
                    (imageView.width * aspectRatio).toInt()
            }
            Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
        } catch (e: Exception) {
            source
        }
    }



//--------------------------------------------------------------------------------------------//



}