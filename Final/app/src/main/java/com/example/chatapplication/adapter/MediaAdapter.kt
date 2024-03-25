package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.browse.MediaBrowser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.UploadTypeConstants
import com.example.chatapplication.databinding.ItemImageSliderBinding
import com.example.chatapplication.databinding.ItemVideoBinding
import com.example.chatapplication.model.entity.MediaShow
import com.example.chatapplication.model.entity.PlayerVideoItem
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.PhotoLoadUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player


import com.tencent.mmkv.MMKV
import vn.techres.line.app.AppAdapter


class MediaAdapter(context: Context) :
    AppAdapter<MediaShow>(context) {
    private lateinit var player: ExoPlayer
    private lateinit var mediaItem: MediaItem
    private lateinit var playerPrepareListener: OnVideoPrepareListener
    private var mmkv: MMKV = MMKV.mmkvWithID("cache_volume")

    fun setVideoListener(playerPrepareListener: OnVideoPrepareListener) {
        this.playerPrepareListener = playerPrepareListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (getData()[position].type == UploadTypeConstants.UPLOAD_IMAGE) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return if (viewType == 1) {
            val binding =
                ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VideoViewHolder(binding)
        } else {
            val binding =
                ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolder(binding)
        }
    }

    inner class ImageViewHolder(private val binding: ItemImageSliderBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            val finalUrl = if (!item.url.contains("/")) {
                if (item.urlLocal == "")
                    PhotoLoadUtils.getLinkPhoto(item.url)
                else
                    item.urlLocal
            } else {
                item.url
            }

            PhotoLoadUtils.loadMediaImage(finalUrl, binding.mBigImage)

            binding.mBigImage.setOnClickListener{
                playerPrepareListener.onTouchView()
            }
        }
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("UnsafeOptInUsageError", "ClickableViewAccessibility")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            val finalUrl = if (!item.url.contains("/")) {
                if (item.urlLocal == "")
                    PhotoLoadUtils.getLinkPhoto(item.url)
                else
                    item.urlLocal
            } else {
                item.url
            }
            player = ExoPlayer.Builder(getContext()).build()
            // Bind the player to the view.
            binding.playerView.player = player
            // Build the media item.
            mediaItem = MediaItem.fromUri(finalUrl)
            // Set the media item to be played.
            player.setMediaItem(mediaItem)
            // Prepare the player.
            player.prepare()
            //Set up volume
            if (mmkv.getBoolean(AppConstants.VOLUME_INFO, false)) {
                player.volume = 1f
            } else {
                player.volume = 0f
            }
            //Repeat mode
            player.repeatMode = Player.REPEAT_MODE_ONE
            binding.playerView.setShowNextButton(false)
            binding.playerView.setShowPreviousButton(false)
            binding.ivVolume.isSelected = player.volume == 1f
            player.addListener(object : Player.Listener {
                override fun onVolumeChanged(volume: Float) {
                    binding.ivVolume.isSelected = volume == 1f
                }
            })
            binding.playerView.setControllerVisibilityListener { visibility ->
                binding.ivVolume.isVisible = visibility == View.VISIBLE
            }
            binding.ivVolume.setOnClickListener {
                if (mmkv.getBoolean(AppConstants.VOLUME_INFO, false)) {
                    mmkv.putBoolean(AppConstants.VOLUME_INFO, false)
                    player.volume = 0f
                } else {
                    mmkv.putBoolean(AppConstants.VOLUME_INFO, true)
                    player.volume = 1f
                }
                playerPrepareListener.onChangeVideoVolume()
            }

            playerPrepareListener.onVideoPrepare(PlayerVideoItem(player, position))

            binding.playerView.setOnClickListener{
                playerPrepareListener.onTouchView()
            }
        }
    }

    interface OnVideoPrepareListener {
        fun onVideoPrepare(playerItem: PlayerVideoItem)

        fun onChangeVideoVolume()

        fun onTouchView()
    }
}