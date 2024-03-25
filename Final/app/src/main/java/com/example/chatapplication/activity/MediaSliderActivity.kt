package com.example.chatapplication.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.chatapplication.R
import com.example.chatapplication.adapter.MediaAdapter
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.constant.UploadTypeConstants
import com.example.chatapplication.databinding.MediaSliderActivityBinding
import com.example.chatapplication.eventbus.EventBusBackFromDetailTimeLine
import com.example.chatapplication.model.entity.MediaShow
import com.example.chatapplication.model.entity.PlayerVideoItem
import com.example.chatapplication.utils.AppUtils
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import com.example.chatapplication.utils.PhotoLoadUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Runnable
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Type


class MediaSliderActivity : AppActivity(), MediaAdapter.OnVideoPrepareListener {
    private lateinit var binding: MediaSliderActivityBinding

    private var data: ArrayList<MediaShow> = ArrayList()
    private var isMediaCountVisible: Boolean = false
    private var startPosition: Int = 0
    private var player: ExoPlayer? = null
    private var sliderAdapter: MediaAdapter? = null
    private var playerList: ArrayList<PlayerVideoItem> = ArrayList()

    private var mmkv: MMKV = MMKV.mmkvWithID("cache_volume")

    override fun getLayoutView(): View {
        binding = MediaSliderActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(
            R.anim.window_ios_in,
            R.anim.window_ios_out
        )
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
            R.anim.window_ios_in,
            R.anim.window_ios_out
        )
        updateSeekAndPlayingStatus()
        if (data.size == 1) {
            player?.pause()
        } else {
            val previousIndex = playerList.indexOfFirst { it.player.isPlaying }
            if (previousIndex != -1) {
                player = playerList[previousIndex].player
                player?.pause()
                player?.playWhenReady = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (data.size == 1) {
            player?.release()
        } else {
            val previousIndex = playerList.indexOfFirst { it.player.isPlaying }
            if (previousIndex != -1) {
                player = playerList[previousIndex].player
                player?.release()
            }
        }
    }

    private fun updateSeekAndPlayingStatus() {
        if (data.size == 1 && data.first().type == UploadTypeConstants.UPLOAD_VIDEO) {
            try {
                EventBus.getDefault().post(
                    EventBusBackFromDetailTimeLine(
                        data.first().url,
                        player!!.currentPosition,
                        player!!.isPlaying
                    )
                )
            } catch (e: Exception) {
                EventBus.getDefault().post(
                    EventBusBackFromDetailTimeLine(data.first().url, 0, true)
                )
            }
        }
    }

    override fun initView() {
        ImmersionBar.setTitleBar(this, binding.toolbar)
        val slideConfig = SlidrConfig.Builder()
            .position(SlidrPosition.TOP)//Hướng có thể trượt
            .sensitivity(1f)//Độ nhảy cảm của việc trượt
            .scrimColor(getColor(R.color.transparent))//Màu của lớp phủ khi slide
            .scrimStartAlpha(0.8f)//Độ trong suốt ban đầu cảu lớp phủ trước khi trượt
            .scrimEndAlpha(0f)//Độ trong suốt ban đầu cảu lớp phủ sau khi trượt
            .velocityThreshold(2500f)//Ngưỡng tốc độ tối thiểu để trượt thành công. VD: cho là 2.5s(2500) nếu tốc độ trượt ngón tay hơn 2.5s thì xem như trượt thành công
            .distanceThreshold(0.25f)//Ngưỡng khoảng cách tối thiểu để trượt thành công. VD: cho là 200f nếu ngón tay trượt 1 khoảng là 200f thì xem như trượt thành công
            .edge(false)
            .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
            .build()
        Slidr.attach(this, slideConfig)
    }

    override fun initData() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(
            this,
            android.R.color.black
        )
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(AppConstants.DATA_MEDIA)) {
                val gson = Gson()
                val mediaListType: Type = object : TypeToken<ArrayList<MediaShow>?>() {}.type
                data = gson.fromJson(bundleIntent.getString(AppConstants.DATA_MEDIA), mediaListType)
            }
            if (bundleIntent.containsKey(AppConstants.POSITION_MEDIA)) {
                startPosition = bundleIntent.getInt(AppConstants.POSITION_MEDIA)
            }

            if (bundleIntent.containsKey(AppConstants.MEDIA_COUNT_VISIBLE)) {
                isMediaCountVisible = bundleIntent.getBoolean(AppConstants.MEDIA_COUNT_VISIBLE)
            }
        }

        if (data.size > 1) {
            binding.mPager.show()
            binding.llOneMedia.hide()
            initViewsAndSetAdapter()
        } else {
            binding.mPager.hide()
            binding.llOneMedia.show()
            initViewOneMedia()
        }

        autoHideToolbar.run()
        binding.imageView.mBigImage.setOnClickListener(showToolbar)
        binding.videoView.playerView.setOnClickListener(showToolbar)
    }

    private val showToolbar = OnClickListener {
        if (binding.toolbar.isVisible) {
            binding.toolbar.hide()
        } else {
            binding.toolbar.show()
            autoHideToolbar.run()
        }
    }

    private val autoHideToolbar = Runnable {
        Handler(Looper.getMainLooper()).postDelayed({
            if (binding.toolbar.isVisible) {
                binding.toolbar.hide()
            }
        }, 5000)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initViewOneMedia() {
        binding.tvNumber.hide()
        binding.ivClose.setOnClickListener {
            finish()
        }

        if (data.first().url.contains("/"))
            binding.ivDownload.hide()
        else
            binding.ivDownload.hide()

        binding.ivDownload.setOnClickListener {
            AppUtils.downloadFile(
                AppApplication.applicationContext(),
                data.first().url,
                data.first().name
            )
            MMKV.mmkvWithID(AppConstants.DOWNLOAD_CACHE)
                .putString(AppConstants.FILE_DOWNLOAD, data.first().url)
            MMKV.mmkvWithID(AppConstants.DOWNLOAD_CACHE).putInt(AppConstants.TYPE_DOWNLOAD, 1)
        }

        val finalUrl = if (!data.first().url.contains("/")) {
            if (data.first().urlLocal == "")
                PhotoLoadUtils.getLinkPhoto(data.first().url)
            else
                data.first().urlLocal
        } else {
            data.first().url
        }
        if (data.first().type == UploadTypeConstants.UPLOAD_IMAGE) {
            binding.llImage.show()
            binding.llVideo.hide()
            PhotoLoadUtils.loadMediaImage(finalUrl, binding.imageView.mBigImage)
        } else {
            binding.llImage.hide()
            binding.llVideo.show()
            player = ExoPlayer.Builder(this).build()
            // Bind the player to the view.
            binding.videoView.playerView.player = player
            // Build the media item.
            val mediaItem = MediaItem.fromUri(finalUrl)
            // Set the media item to be played.
            player?.setMediaItem(mediaItem)
            // Change seek to the seek from previous screen
            player?.seekTo(data.first().currentSeek)
            // Prepare the player.
            player?.prepare()
            //Set up volume
            if (mmkv.getBoolean(AppConstants.VOLUME_INFO, false)) {
                player?.volume = 1f
            } else {
                player?.volume = 0f
            }
            //Repeat mode
            player?.repeatMode = Player.REPEAT_MODE_ONE
            binding.videoView.playerView.setShowNextButton(false)
            binding.videoView.playerView.setShowPreviousButton(false)
            binding.videoView.ivVolume.isSelected = player?.volume == 1f
            player?.addListener(object : Player.Listener {
                override fun onVolumeChanged(volume: Float) {
                    binding.videoView.ivVolume.isSelected = volume == 1f
                }
            })
            binding.videoView.playerView.setControllerVisibilityListener { visibility ->
                binding.videoView.ivVolume.isVisible = visibility == View.VISIBLE
            }
            binding.videoView.ivVolume.setOnClickListener {
                if (mmkv.getBoolean(AppConstants.VOLUME_INFO, false)) {
                    mmkv.putBoolean(AppConstants.VOLUME_INFO, false)
                    player?.volume = 0f
                } else {
                    mmkv.putBoolean(AppConstants.VOLUME_INFO, true)
                    player?.volume = 1f
                }
            }
            // Start the playback.
            player?.play()
        }
    }

    private fun setStartPosition() {
        if (startPosition >= 0) {
            if (startPosition > data.size) {
                binding.mPager.currentItem = data.size - 1
                binding.mPager.setCurrentItem(data.size - 1, false)
                return
            }
            binding.mPager.setCurrentItem(startPosition, false)
        } else {
            binding.mPager.setCurrentItem(0, false)
        }
        binding.mPager.offscreenPageLimit = data.size
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun initViewsAndSetAdapter() {
        sliderAdapter = MediaAdapter(this)
        sliderAdapter?.setData(data)
        sliderAdapter?.setVideoListener(this)
        binding.mPager.adapter = sliderAdapter
        setStartPosition()
        binding.mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.tvNumber.text = (position + 1).toString() + "/" + data.size
                pausePreviousVideoPlayer()
            }
        })

        if (isMediaCountVisible) {
            if (data.size == 1) {
                binding.tvNumber.hide()
            } else binding.tvNumber.show()
            binding.tvNumber.text = (binding.mPager.currentItem + 1).toString() + "/" + data.size
        } else {
            binding.tvNumber.hide()
        }

        binding.ivClose.setOnClickListener {
            finish()
        }

        if (data[binding.mPager.currentItem].url.contains("/")) {
            binding.ivDownload.hide()
        } else
            binding.ivDownload.show()

        binding.ivDownload.setOnClickListener {
            AppUtils.downloadFile(
                AppApplication.applicationContext(),
                data[binding.mPager.currentItem].url,
                data[binding.mPager.currentItem].name
            )
            MMKV.mmkvWithID(AppConstants.DOWNLOAD_CACHE)
                .putString(AppConstants.FILE_DOWNLOAD, data[binding.mPager.currentItem].url)
            MMKV.mmkvWithID(AppConstants.DOWNLOAD_CACHE).putInt(AppConstants.TYPE_DOWNLOAD, 1)
        }
    }

    private fun pausePreviousVideoPlayer() {
        val previousIndex = playerList.indexOfFirst { it.player.isPlaying }
        if (previousIndex != -1) {
            player = playerList[previousIndex].player
            player?.pause()
            player?.playWhenReady = false
        }
    }

    override fun onVideoPrepare(playerItem: PlayerVideoItem) {
        playerList.add(playerItem)
    }

    override fun onChangeVideoVolume() {
        for (item in playerList) {
            //Set up volume
            if (mmkv.getBoolean(AppConstants.VOLUME_INFO, false)) {
                item.player.volume = 1f
            } else {
                item.player.volume = 0f
            }
        }

        //Set up volume
        if (mmkv.getBoolean(AppConstants.VOLUME_INFO, false)) {
            player?.volume = 1f
        } else {
            player?.volume = 0f
        }
    }

    override fun onTouchView() {
        if (binding.toolbar.isVisible) {
            binding.toolbar.hide()
        } else {
            binding.toolbar.show()
            autoHideToolbar.run()
        }
    }
}