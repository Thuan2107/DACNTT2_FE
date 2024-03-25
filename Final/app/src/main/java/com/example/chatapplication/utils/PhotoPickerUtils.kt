package com.example.chatapplication.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import com.example.chatapplication.R
import com.example.chatapplication.other.GlideEngine
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnGridItemSelectAnimListener
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnQueryFilterListener
import com.luck.picture.lib.interfaces.OnSelectAnimListener
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.utils.DateUtils
import top.zibin.luban.CompressionPredicate
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File

object PhotoPickerUtils {
    fun showImagePickerCreatePost(
        activity: Activity, localMedia: List<LocalMedia>, intent: ActivityResultLauncher<Intent>
    ) {

        val selectionModel: PictureSelectionModel = PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofAll())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCompressEngine(ImageFileCompressEngine())
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setLanguage(7)
            .isOnlyObtainSandboxDir(false) //đọc bộ nhớ ảnh, không thêm là cút
            .isPageStrategy(true)
            .isFastSlidingSelect(true) //.setOutputCameraImageFileName("luck.jpeg")
            //.setOutputCameraVideoFileName("luck.mp4")
            .isWithSelectVideoImage(true)
            .isPreviewFullScreenMode(true)
            .isPreviewImage(true)
            .isPreviewVideo(false)
            .isPreviewAudio(false)
            .isOpenClickSound(true)
            .isGif(true) // sử dụng ảnh gif
            .setImageSpanCount(3)
            .setFilterMaxFileSize(200000)
            .setMaxSelectNum(30)
            .setMinSelectNum(0)
            .setMaxVideoSelectNum(4)
            .setRecyclerAnimationMode(1)
            .setSelectedData(localMedia)
        selectionModel.forResult(intent)
    }

    fun showImagePickerCreateComment(
        activity: Activity, intent: ActivityResultLauncher<Intent>, localMedia: List<LocalMedia>
    ) {

        val selectionModel: PictureSelectionModel = PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofAll())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCompressEngine(ImageFileCompressEngine())
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setLanguage(7)
            .isOnlyObtainSandboxDir(false) //đọc bộ nhớ ảnh, không thêm là cút
            .isPageStrategy(true)
            .isFastSlidingSelect(true) //.setOutputCameraImageFileName("luck.jpeg")
            //.setOutputCameraVideoFileName("luck.mp4")
            .isWithSelectVideoImage(true)
            .isPreviewFullScreenMode(true)
            .isPreviewImage(true)
            .isPreviewVideo(false)
            .isPreviewAudio(false)
            .isOpenClickSound(true)
            .isGif(true) // Không sử dụng ảnh gif
            .setImageSpanCount(3) // chia cột danh sách ảnh
            .setFilterMaxFileSize(200000)
            .setMaxSelectNum(1)
            .setMinSelectNum(0)
            .setMaxVideoSelectNum(1)
            .setRecyclerAnimationMode(1)
            .isEmptyResultReturn(false)
            .isWithSelectVideoImage(false)
            .setSelectedData(localMedia)
        selectionModel.forResult(intent)

    }

    fun showImagePickerChooseAvatar(
        activity: Activity, intent: ActivityResultLauncher<Intent>
    ) {
        PictureSelector.create(activity)
            .openSystemGallery(SelectMimeType.ofImage())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .forSystemResultActivity(intent)
    }

    fun showImagePickerChooseAvatar(
        activity: Activity,
    ) {
        // 进入相册
        val selectionModel: PictureSelectionModel = PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofImage())
            .setSelectorUIStyle(PictureSelectorStyle())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCompressEngine(ImageFileCompressEngine())
            .isAutoVideoPlay(true)
            .isLoopAutoVideoPlay(true)
            .isUseSystemVideoPlayer(true)
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setLanguage(7)
            .isDisplayTimeAxis(true)
            .isOnlyObtainSandboxDir(false) //đọc bộ nhớ ảnh, không thêm là cút
            .isPageStrategy(true)
            .isOriginalControl(true)
            .isDisplayCamera(true)
            .isOpenClickSound(true)
            .isFastSlidingSelect(true) //.setOutputCameraImageFileName("luck.jpeg")
            //.setOutputCameraVideoFileName("luck.mp4")
            .isWithSelectVideoImage(true) //Có hỗ trợ lựa chọn hình ảnh video hay không
            .isPreviewFullScreenMode(true) //xem trước
            .isVideoPauseResumePlay(true)
            .isPreviewZoomEffect(true)
            .isPreviewImage(true)
            .isPreviewVideo(true)
            .isPreviewAudio(true)
            .setGridItemSelectAnimListener(OnGridItemSelectAnimListener { view, isSelected ->
                val set = AnimatorSet()
                set.playTogether(
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleX",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    ),
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleY",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    )
                )
                set.duration = 350
                set.start()
            })
            .setSelectAnimListener(OnSelectAnimListener { view ->
                val animation = AnimationUtils.loadAnimation(activity, R.anim.ps_anim_modal_in)
                view.startAnimation(animation)
                animation.duration
            }) //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
            .isMaxSelectEnabledMask(true)
            .isDirectReturnSingle(true)
            .setMinSelectNum(0)
//            .setMaxVideoSelectNum(maxSelectVideoNum) //số lượng video tối đa
            .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
            .isGif(false) //ảnh gift
        selectionModel.forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun showImagePickerChooseAvatarChat(
        activity: Activity, intent: ActivityResultLauncher<Intent>
    ) {
        // 进入相册
        val selectionModel: PictureSelectionModel = PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofImage())
            .setSelectorUIStyle(PictureSelectorStyle())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCompressEngine(ImageFileCompressEngine())
            .isAutoVideoPlay(true)
            .isLoopAutoVideoPlay(true)
            .isUseSystemVideoPlayer(true)
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setLanguage(7)
            .isDisplayTimeAxis(true)
            .isOnlyObtainSandboxDir(false) //đọc bộ nhớ ảnh, không thêm là cút
            .isPageStrategy(true)
            .isOriginalControl(true)
            .isDisplayCamera(true)
            .isOpenClickSound(true)
            .isFastSlidingSelect(true) //.setOutputCameraImageFileName("luck.jpeg")
            //.setOutputCameraVideoFileName("luck.mp4")
            .isWithSelectVideoImage(true) //Có hỗ trợ lựa chọn hình ảnh video hay không
            .isPreviewFullScreenMode(true) //xem trước
            .isVideoPauseResumePlay(true)
            .isPreviewZoomEffect(true)
            .isPreviewImage(true)
            .isPreviewVideo(true)
            .isPreviewAudio(true)
            .setGridItemSelectAnimListener(OnGridItemSelectAnimListener { view, isSelected ->
                val set = AnimatorSet()
                set.playTogether(
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleX",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    ),
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleY",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    )
                )
                set.duration = 350
                set.start()
            })
            .setSelectAnimListener(OnSelectAnimListener { view ->
                val animation = AnimationUtils.loadAnimation(activity, R.anim.ps_anim_modal_in)
                view.startAnimation(animation)
                animation.duration
            }) //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
            .isMaxSelectEnabledMask(true)
            .isDirectReturnSingle(true)
            .setMinSelectNum(0)
//            .setMaxVideoSelectNum(maxSelectVideoNum) //số lượng video tối đa
            .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
            .isGif(false) //ảnh gift
        selectionModel.forResult(intent)
    }


    fun showImagePickerChat(
        activity: Activity,
    ) {
        val selectionModel: PictureSelectionModel = PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofAll())
            .setSelectorUIStyle(PictureSelectorStyle())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCompressEngine(ImageFileCompressEngine())
            .isAutoVideoPlay(true)
            .isLoopAutoVideoPlay(true)
            .isUseSystemVideoPlayer(true)
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setLanguage(7)
            .isDisplayTimeAxis(true)
            .isOnlyObtainSandboxDir(false) //đọc bộ nhớ ảnh, không thêm là cút
            .isPageStrategy(true)
            .isOriginalControl(true)
            .isDisplayCamera(true)
            .isOpenClickSound(true)
            .isFastSlidingSelect(true) //.setOutputCameraImageFileName("luck.jpeg")
            //.setOutputCameraVideoFileName("luck.mp4")
            .isWithSelectVideoImage(true) //Có hỗ trợ lựa chọn hình ảnh video hay không
            .isPreviewFullScreenMode(true) //xem trước
            .isVideoPauseResumePlay(true)
            .isPreviewZoomEffect(true)
            .isPreviewImage(true)
            .isPreviewVideo(true)
            .isPreviewAudio(true)
            .setGridItemSelectAnimListener(OnGridItemSelectAnimListener { view, isSelected ->
                val set = AnimatorSet()
                set.playTogether(
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleX",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    ),
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleY",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    )
                )
                set.duration = 350
                set.start()
            })
            .setSelectAnimListener(OnSelectAnimListener { view ->
                val animation = AnimationUtils.loadAnimation(activity, R.anim.ps_anim_modal_in)
                view.startAnimation(animation)
                animation.duration
            }) //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
            .isMaxSelectEnabledMask(true)
            .isDirectReturnSingle(true)
            .setMinSelectNum(0)
            .setMaxSelectNum(30) //tối đa 30 ảnh
            .setMaxVideoSelectNum(1)
//            .setMaxVideoSelectNum(maxSelectVideoNum) //số lượng video tối đa
            .setRecyclerAnimationMode(AnimationType.SLIDE_IN_BOTTOM_ANIMATION)
            .isGif(false) //ảnh gift
        selectionModel.forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun showImagePickerChat(
        activity: Activity, intent: ActivityResultLauncher<Intent>
    ) {
        val selectionModel: PictureSelectionModel = PictureSelector.create(activity)
            .openGallery(SelectMimeType.ofAll())
            .setSelectorUIStyle(PictureSelectorStyle())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setCompressEngine(ImageFileCompressEngine())
            .isAutoVideoPlay(true)
            .isLoopAutoVideoPlay(true)
            .isUseSystemVideoPlayer(true)
            .isPageSyncAlbumCount(true)
            .setQueryFilterListener(OnQueryFilterListener { false }) //.setExtendLoaderEngine(getExtendLoaderEngine())
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setLanguage(7)
            .isDisplayTimeAxis(true)
            .isOnlyObtainSandboxDir(false) //đọc bộ nhớ ảnh, không thêm là cút
            .isPageStrategy(true)
            .isOriginalControl(true)
            .isDisplayCamera(true)
            .isOpenClickSound(true)
            .isFastSlidingSelect(true) //.setOutputCameraImageFileName("luck.jpeg")
            //.setOutputCameraVideoFileName("luck.mp4")
            .isWithSelectVideoImage(true) //Có hỗ trợ lựa chọn hình ảnh video hay không
            .isPreviewFullScreenMode(true) //xem trước
            .isVideoPauseResumePlay(true)
            .isPreviewZoomEffect(true)
            .isPreviewImage(true)
            .isPreviewVideo(true)
            .isPreviewAudio(true)
            .setGridItemSelectAnimListener(OnGridItemSelectAnimListener { view, isSelected ->
                val set = AnimatorSet()
                set.playTogether(
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleX",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    ),
                    ObjectAnimator.ofFloat(
                        view,
                        "scaleY",
                        if (isSelected) 1f else 1.12f,
                        if (isSelected) 1.12f else 1.0f
                    )
                )
                set.duration = 350
                set.start()
            })
            .setSelectAnimListener(OnSelectAnimListener { view ->
                val animation = AnimationUtils.loadAnimation(activity, R.anim.ps_anim_modal_in)
                view.startAnimation(animation)
                animation.duration
            }) //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
            .isMaxSelectEnabledMask(true)
            .isDirectReturnSingle(true)
            .setMinSelectNum(0)
            .setMaxSelectNum(30) //tối đa 30 ảnh
            .setMaxVideoSelectNum(1)
//            .setMaxVideoSelectNum(maxSelectVideoNum) //số lượng video tối đa
            .setRecyclerAnimationMode(AnimationType.SLIDE_IN_BOTTOM_ANIMATION)
            .isGif(false) //ảnh gift
        selectionModel.forResult(intent)
    }

    fun showImageVideoPickerShortVideo(
        activity: Activity, intent: ActivityResultLauncher<Intent>
    ) {
        PictureSelector.create(activity)
            .openSystemGallery(SelectMimeType.ofAll())
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setCompressEngine(ImageFileCompressEngine())
            .forSystemResultActivity(intent)
    }

    private class ImageFileCompressEngine : CompressFileEngine {

        override fun onStartCompress(
            context: Context,
            source: ArrayList<Uri>,
            call: OnKeyValueResultCallbackListener
        ) {
            Luban.with(context)
                .load(source)
                .ignoreBy(100)
                .setRenameListener { filePath ->
                    val indexOf = filePath.lastIndexOf(".")
                    val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                    DateUtils.getCreateFileName("CMP_") + postfix
                }
                .filter(object : CompressionPredicate {
                    override fun apply(path: String): Boolean {
                        if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                            return true
                        }
                        return !PictureMimeType.isUrlHasGif(path)
                    }
                })
                .setCompressListener(object : OnNewCompressListener {
                    override fun onStart() {

                    }

                    override fun onSuccess(source: String, compressFile: File) {
                        call.onCallback(source, compressFile.absolutePath)
                    }

                    override fun onError(source: String, e: Throwable) {
                        call.onCallback(source, null)
                    }
                })
                .launch()
        }
    }
}