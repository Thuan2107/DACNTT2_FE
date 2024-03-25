package com.example.chatapplication.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.text.Html
import android.text.InputFilter
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chatapplication.R
import com.example.chatapplication.activity.MediaSliderActivity
import com.example.chatapplication.app.AppApplication
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.model.entity.MediaList
import com.example.chatapplication.model.entity.MediaShow
import com.example.chatapplication.other.CenterLayoutManager
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import com.luck.picture.lib.entity.LocalMedia
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.SecureRandom
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow


object AppUtils {
    /** Trả về tên thiết bị  */
    fun getDeviceName(): String {
        return Build.MANUFACTURER + " - " + Build.MODEL
    }

    @SuppressLint("HardwareIds")
    fun getDeviceUid(activity: Activity): String {
        return Settings.Secure.getString(
            activity.application.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    /**
     * Check file name extension
     * */
    fun getMimeType(url: String): String? {
        try {
            return url.substring(url.lastIndexOf(".") + 1)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  địa chỉ ip
     */
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr: String = addr.hostAddress ?: ""
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                    0, delim
                                ).uppercase(Locale.getDefault())
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            //ex
        } // for now eat exceptions
        return ""
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            AppApplication.instance!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Format tiền
    fun formatCurrencyDecimal(number: Float?): String {
        return String.format("%,.0f", number)
    }

    fun formatCurrencyDecimal(number: Long?): String {
        return String.format("%,.0f", number)
    }

    fun downloadFile(activity: Context, fileUrl: String?, fileName: String?) {
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(PhotoLoadUtils.getLinkPhoto(fileUrl))
        val request = DownloadManager.Request(uri)
        request.setTitle(fileName)
        request.setDescription("Tải về")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS.toString(),
            AppConstants.FOLDER_APP + File.separator + fileName!!.replace("%20", "")
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
        ToastUtils.show(activity.getString(R.string.downloading))
    }

    fun getDecimalFormattedString(input: String): String {
        var value = input
        if (value.contains("-")) {
            value = value.substring(1)
            val lst = StringTokenizer(value, ".")
            var str1 = value
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = StringBuilder()
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = StringBuilder(".")
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.isNotEmpty()) {
                        str3.append(".").append(str2)
                    }
                    return String.format("-%s", str3)
                }
                if (i == 3) {
                    str3.insert(0, ",")
                    i = 0
                }
                str3.insert(0, str1[k])
                i++
                k--
            }
        } else {
            val lst = StringTokenizer(value, ".")
            var str1 = value
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = StringBuilder()
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = StringBuilder(".")
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.isNotEmpty()) {
                        str3.append(".").append(str2)
                    }
                    return str3.toString()
                }
                if (i == 3) {
                    str3.insert(0, ",")
                    i = 0
                }
                str3.insert(0, str1[k])
                i++
                k--
            }
        }
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun getWidthHeightViewVideo(height: Int, width: Int, maxLength: Int): ArrayList<Int> {
        return if (height > width) {
            val aspectRatio = width.toDouble() / height.toDouble()
            val targetWidth = (maxLength * aspectRatio).toInt()
            arrayListOf(maxLength, targetWidth)
        } else if (height < width) {
            val aspectRatio = height.toDouble() / width.toDouble()
            val targetHeight = (maxLength * aspectRatio).toInt()
            arrayListOf(targetHeight, maxLength)
        } else {
            arrayListOf(maxLength, maxLength)
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

    fun digitSpecialCharactersVN(data: String): String {
        return data.replace("[^\\w\\s.,]".toRegex(), "").replace("  ", " ")
            .replace("_".toRegex(), "")
    }


    private fun configRecyclerView(
        recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager?
    ) {
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(50)
        recyclerView.isDrawingCacheEnabled = true
        recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.itemAnimator?.let {
            it.changeDuration = 0
            (it as SimpleItemAnimator).supportsChangeAnimations = false
        }
        recyclerView.isNestedScrollingEnabled = false
    }

    fun initRecyclerViewVertical(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        configRecyclerView(
            view, LinearLayoutManager(
                view.context, RecyclerView.VERTICAL, false
            )
        )
        view.adapter = adapter
    }


    fun initRecyclerViewVertical(
        view: RecyclerView,
        adapter: RecyclerView.Adapter<*>?,
        count: Int
    ) {
        configRecyclerView(view, GridLayoutManager(view.context, count))
        view.adapter = adapter
    }

    fun initRecyclerViewHorizontal(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        configRecyclerView(
            view, LinearLayoutManager(
                view.context, RecyclerView.HORIZONTAL, false
            )
        )
        view.adapter = adapter
    }

    fun initRecyclerViewHorizontalCenter(
        view: RecyclerView,
        adapter: RecyclerView.Adapter<*>?
    ) {
        configRecyclerView(
            view, CenterLayoutManager(
                view.context, RecyclerView.HORIZONTAL, false
            )
        )
        view.adapter = adapter
    }
    fun initRecyclerViewHorizontalCenter(
        view: RecyclerView,
        adapter: RecyclerView.Adapter<*>?,
        autoScrollInterval: Long = 3000 // Thời gian mặc định giữa các lần cuộn, tính theo mili giây
    ) {
        val layoutManager = CenterLayoutManager(
            view.context, RecyclerView.HORIZONTAL, false
        )
        configRecyclerView(view, layoutManager)
        view.adapter = adapter

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                // Lấy vị trí item cuối cùng hiển thị trên màn hình
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Lấy tổng số item trong adapter
                val totalItemCount = adapter?.itemCount ?: 0

                // Nếu vị trí cuối cùng hiển thị là item cuối cùng trong danh sách
                if (lastVisibleItemPosition == totalItemCount-1 ) {

                    // Cuộn về đầu danh sách
                    layoutManager.smoothScrollToPosition(view, RecyclerView.State(), 0)
                } else {
                    // Nếu không, cuộn đến vị trí tiếp theo
                    layoutManager.smoothScrollToPosition(view, RecyclerView.State(), lastVisibleItemPosition)

                }

                // Lập lịch cho cuộn tiếp theo
                handler.postDelayed(this, autoScrollInterval)
            }
        }

        // Bắt đầu cuộn tự động
        handler.postDelayed(runnable, autoScrollInterval)
    }



    fun initRecyclerViewHorizontal(
        view: RecyclerView,
        adapter: RecyclerView.Adapter<*>?,
        count: Int
    ) {
        configRecyclerView(view, GridLayoutManager(view.context, count))
        view.adapter = adapter
    }

    fun initRecyclerViewVerticalReverse(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        val preCachingLayoutManager = LinearLayoutManager(
            view.context,
            RecyclerView.VERTICAL,
            true
        )
        configRecyclerView(view, preCachingLayoutManager)
        view.adapter = adapter
    }

    fun initGridLayoutFromRecyclerViewHorizontalWithTimeHeaderConfig(
        view: RecyclerView,
        adapter: RecyclerView.Adapter<*>?,
        count: Int
    ) {
        val layoutManager = GridLayoutManager(view.context,count)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if (adapter!!.getItemViewType(position) == 0)
                    count
                else
                    1
            }
        }
        configRecyclerView(view, layoutManager)
        view.adapter = adapter
    }

    //format điểm đánh giá nhà hàng
    //ví dụ: 5.0 ---> 5, 5.1, 5.2 ---> 5.1/5.2
    fun formatDoubleToString(value: Double): String {
        val s: String = if (value.toInt().toDouble().compareTo(value) == 0) {
            java.lang.String.format(Locale.getDefault(), "%s", value.toInt())
        } else {
            java.lang.String.format(Locale.getDefault(), "%s", value)
        }
        return s
    }

    fun fromHtml(string: String): Spanned {
        return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    }

    //Round double to 2 decimal
    fun roundOffDecimal(numInDouble: Double): Double {
        return BigDecimal(numInDouble.toString()).setScale(1, RoundingMode.HALF_UP).toDouble()
    }

    fun getNameFileToPath(path: String): String {
        return path.substring(path.lastIndexOf("/") + 1)
    }

    @Throws(IOException::class)
    fun copyFile(source: File?, destination: File?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(FileInputStream(source), FileOutputStream(destination))
        }
    }

    fun checkMimeTypeVideo(url: String?): Boolean {
        return getMimeType(url!!) == "mp4" || getMimeType(
            url
        ) == "MP4" || getMimeType(url) == "MOV" || getMimeType(url) == "mov"
    }

    fun checkMimeTypeImage(url: String?): Boolean {
        return getMimeType(url!!) == "png" || getMimeType(
            url
        ) == "jpg" || getMimeType(url) == "jpeg"
    }

    fun checkMimeTypeGif(url: String?): Boolean {
        return getMimeType(url!!) == "gif"
    }

    @SuppressLint("IntentWithNullActionLaunch")
//    fun showMediaAvatar(context: Context, avatar: MediaList, position: Int) {
//        val medias: ArrayList<MediaShow> = ArrayList()
//        medias.add(MediaShow(avatar.original.url, avatar.original.name, avatar.type))
//        val intent = Intent(
//            context,
//            Class.forName(ModuleClassConstants.MEDIA_SLIDER_ACTIVITY)
//        )
//        val bundle = Bundle()
//        bundle.putString(AppConstants.DATA_MEDIA, Gson().toJson(medias))
//        bundle.putInt(AppConstants.POSITION_MEDIA, position)
//
//        if (position == 0) {
//            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, false)
//        } else
//            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, true)
//
//        intent.putExtras(bundle)
//        context.startActivity(intent)
//    }

    fun showMediaNewsFeed(context: Context, data: ArrayList<String>, position: Int) {
        val medias: ArrayList<MediaShow> = ArrayList()
        for (i in data.indices) {
            medias.add(
                MediaShow(
                    data[i])
            )
        }

        val intent = Intent(context, MediaSliderActivity::class.java)
        val bundle = Bundle()
        bundle.putString(AppConstants.DATA_MEDIA, Gson().toJson(medias))
        bundle.putInt(AppConstants.POSITION_MEDIA, position)

        if (data.size == 1 && position == 0) {
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, false)
        } else
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, true)

        intent.putExtras(bundle)
        context.startActivity(intent)
    }





    fun calculateTotalPage(totalRecord: Int, limit: Int): Int {
        return if (totalRecord % limit == 0) {
            (totalRecord / limit)
        } else {
            (totalRecord / limit) + 1
        }
    }

    fun random(): String {
        val generator = Random()
        val randomStringBuilder = java.lang.StringBuilder()
        val randomLength = generator.nextInt(15)
        var tempChar: Char
        for (i in 0 until randomLength) {
            tempChar = (generator.nextInt(96) + 32).toChar()
            randomStringBuilder.append(tempChar)
        }
        return randomStringBuilder.toString()
    }

    fun randomVideoNameFile(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val randomName = StringBuilder(10)

        for (i in 0 until 10) {
            val randomIndex = random.nextInt(characters.length)
            randomName.append(characters[randomIndex])
        }

        return "video$randomName.mp4"
    }

    fun resetExternalStorageMedia(context: Context): Boolean {
        if (Environment.isExternalStorageEmulated()) return false
        val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory())
        val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, uri)
        context.sendBroadcast(intent)
        return true
    }


    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun formatValueNumberQuantity(number: Number): String {
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.0").format(
                numValue / 10.0.pow((base * 3).toDouble())
            ) + suffix[base].toString().replace(".0", "").replace(",0", "")
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

    fun getRandomString(len: Int): String {
        val characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val rnd = SecureRandom()
        val sb = java.lang.StringBuilder(len)
        for (i in 0 until len) sb.append(characters[rnd.nextInt(characters.length)])
        return sb.toString()
    }

    fun getFirstLetterEachWord(string: String): String {
        var resutl = ""
        val stringArray = string.split("(?<=[\\S])[\\S]*\\s*").toTypedArray()
        for (s in stringArray) {
            resutl = String.format("%s%s", resutl, s)
        }
        return resutl
    }

    fun removeVietnameseFromString(str: String?): String {
        val slug: String = try {
            val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
            val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
            pattern.matcher(temp).replaceAll("")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
        return slug
    }

    @SuppressLint("IntentWithNullActionLaunch")
    fun showMediaAvatar(context: Context, avatar: String, position: Int) {
        val medias: ArrayList<MediaShow> = ArrayList()
        medias.add(MediaShow(avatar))
        val intent = Intent(
            context,
            MediaSliderActivity::class.java
        )
        val bundle = Bundle()
        bundle.putString(AppConstants.DATA_MEDIA, Gson().toJson(medias))
        bundle.putInt(AppConstants.POSITION_MEDIA, position)

        if (position == 0) {
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, false)
        } else
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, true)

        intent.putExtras(bundle)
        context.startActivity(intent)
    }





    fun formatTimeWithUTC(str_date: String): String {
        val OLD_FORMAT = "MM-dd-yyyy HH:mm:ss"
        val NEW_FORMAT = "HH:mm dd-MM-yyyy"

        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat(OLD_FORMAT)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = try {
            df.parse(str_date) as Date
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
        df.timeZone = TimeZone.getDefault()
        val formattedDate = df.format(Objects.requireNonNull(date))

        var newDateString = ""
        try {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(OLD_FORMAT)
            val d = sdf.parse(formattedDate)
            sdf.applyPattern(NEW_FORMAT)
            newDateString = sdf.format(Objects.requireNonNull(d))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newDateString
    }

    fun copyText(activity: Activity, text: String?) {
        val clipboard =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("\"text\"", text)
        clipboard.setPrimaryClip(clip)
    }


    fun getNameNoType(path: String): String {
        val filename = path.substring(path.lastIndexOf("/") + 1)
        val parts = filename.split("\\.").toTypedArray()
        return "[File] ${parts[0]}"
    }

    fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    fun getVideoYoutubeId(url: String): String {
        val regex =
            "(?<=watch\\?v=|/videos/|/shorts/|embed/|youtu.be/|/v/|/e/|/user/|/live/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200C2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(url)
        return if (matcher.find()) {
            matcher.group()
        } else {
            ""
        }
    }

    private fun isNullOrEmpty(str: String?): Boolean {
        return str.isNullOrEmpty()
    }

//    fun getBitmap(urlImage: String?, context: Context): Bitmap {
//        val bitmapAvatar = if (isNullOrEmpty(urlImage)) {
//            Glide.with(context)
//                .asBitmap()
//                .load(R.drawable.ic_user_default)
//                .circleCrop()
//                .placeholder(R.drawable.ic_user_default)
//                .error(R.drawable.ic_user_default)
//                .submit(100, 100)
//                .get()
//        } else {
//            try {
//                Glide.with(context)
//                    .asBitmap()
//                    .load(PhotoLoadUtils.getLinkPhoto(urlImage))
//                    .circleCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .apply(
//                        RequestOptions().placeholder(R.drawable.ic_user_default)
//                            .error(R.drawable.ic_user_default)
//                    )
//                    .submit(100, 100)
//                    .get()
//            } catch (e: Exception) {
//                Glide.with(context)
//                    .asBitmap()
//                    .load(
//                        R.drawable.ic_user_default
//                    )
//                    .circleCrop()
//                    .placeholder(R.drawable.ic_user_default)
//                    .error(R.drawable.ic_user_default)
//                    .submit(100, 100)//480 320
//                    .get()
//            }
//        }
//        return bitmapAvatar
//    }




    fun removeAccent(s: String?): String? {
        val temp: String = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
    }


    fun disableClickAction(view: View, timeDelay: Long) {
        view.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({ view.isEnabled = true }, timeDelay)
    }

    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(
                videoPath,
                HashMap()
            )
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.frameAtTime
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    @SuppressLint("Recycle")
    fun getRealPathFileFromUri(context: Context, uri: Uri): String {
        var realPath = ""
        try {
            val parcelFileDescriptor =
                context.contentResolver.openFileDescriptor(uri, "r", null)
            if (parcelFileDescriptor != null) {
                val fileDescriptor = parcelFileDescriptor.fileDescriptor
                val inputStream = FileInputStream(fileDescriptor)
                val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME),
                        null,
                        null
                    )
                } else {
                    context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )
                }

                val nameFile = cursor?.use {
                    it.moveToFirst()
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                }
                cursor?.close()
                val file = File(context.cacheDir, nameFile!!)
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.close()
                inputStream.close()
                realPath = file.absolutePath
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return realPath
    }

    var specialCharacters =
        InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (source[i].toString() == "!" || source[i].toString() == "@" || source[i].toString() == "#" || source[i].toString() == "$" || source[i].toString() == "%" || source[i].toString() == "&" || source[i].toString() == "*" || source[i].toString() == " ") {
                    //Empty
                } else if (!Character.isLetterOrDigit(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

    fun getLastNameFirstName(fullName: String): Array<String?> {
        var name = fullName
        val names = arrayOfNulls<String>(2)
        names[0] = ""
        names[1] = ""
        if (!isNullOrEmpty(name)) {
            name = name.trim { it <= ' ' }
            if (name.isNotEmpty()) {
                val lastIndex = name.lastIndexOf(" ")
                if (lastIndex > -1) {
                    names[0] = name.substring(lastIndex)
                    names[1] = name.substring(0, lastIndex)
                    if (names[0] == null) {
                        names[0] = ""
                    }
                    if (names[1] == null) {
                        names[1] = ""
                    }
                } else {
                    names[0] = name
                }
            }
        }
        return names
    }

    fun containsOnlyWholeNumbers(str: String): Boolean {
        val regexPattern = Regex("[0-9]+")
        return regexPattern.matches(str)
    }

    fun getAllPositionOfNumberLinkOfChat(str: String): MutableList<Pair<Int, Int>> {
        val regexPattern = Regex("[0-9 ]+")
        val matchPositions = mutableListOf<Pair<Int, Int>>()
        regexPattern.findAll(str).forEach {
            val startIndex = it.range.first
            val endIndex = it.range.last + 1
            matchPositions.add(Pair(startIndex, endIndex))
        }
        return matchPositions
    }






    private fun openUrl(context: Context, url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }


    // Hàm kiểm tra ký tự đặc biệt và emoji
    fun containsSpecialCharacters(text: String): Boolean {
        val regex = Regex("[^\\p{L}\\p{N}\\s]") // Chỉ cho phép chữ cái, chữ số và khoảng trắng
        return regex.containsMatchIn(text)
    }

    // Hàm xóa các ký tự đặc biệt và emoji từ chuỗi
    fun removeSpecialCharacters(text: String): String {
        val regex = Regex("[^\\p{L}\\p{N}\\s]") // Chỉ cho phép chữ cái, chữ số và khoảng trắng
        return regex.replace(text, "")
    }


//    fun emitSocket(key: String?, ojb: Any?) {
//        try {
//            if (AppApplication.socketChat != null) {
//                Timber.e(
//                    "%s: %s",
//                    "Tình trạng kết nối socket",
//                    AppApplication.socketChat?.connected()
//                )
//                Timber.e("%s: %s", "Domain chat", ConfigCache.getConfig().apiConnection)
//                if (!AppApplication.socketChat!!.connected()) {
//                    AppApplication.socketChat!!.connect()
//                } else {
//                    val gson = Gson()
//                    Timber.d("%s : %s", key, gson.toJson(ojb))
//                    AppApplication.socketChat!!.emit(key, JSONObject(gson.toJson(ojb)))
//                }
//            } else {
//                Timber.e("%s: %s", "Tình trạng kết nối socket", false)
//                Timber.e("%s: %s", "Domain chat", ConfigCache.getConfig().apiConnection)
//            }
//        } catch (e: JSONException) {
//            Timber.e("%s: %s", "Tình trạng kết nối socket", false)
//            Timber.e("%s: %s", "Domain chat", ConfigCache.getConfig().apiConnection)
//        }
//    }

    fun checkDeviceSupported(dialIntent: Intent, context: Context): Boolean {
        val manager = context.packageManager
        val info = manager.queryIntentActivities(dialIntent, 0)
        return info.isNotEmpty()
    }


    fun View.clickWithDebounce(debounceTime: Long = 1000L, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action()
                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }

    fun setChildListener(parent: View, listener: View.OnClickListener) {
        parent.setOnClickListener(listener)
        if (parent !is ViewGroup) {
            return
        }
        for (i in 0 until parent.childCount) {
            setChildListener(parent.getChildAt(i), listener)
        }
    }

    fun getMoneyFormatted(value: BigDecimal): String {
        val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
        return formatter.format(value)
    }

    fun roundAndFormat(number: Double): String {
        val rounded = BigDecimal(number).setScale(1, RoundingMode.HALF_UP).toDouble()
        var formatted = if (rounded % 1 == 0.0) {
            rounded.toInt().toString()
        } else {
            rounded.toString()
        }

        return formatted
    }

    fun <T> List<T>.toArrayList(): ArrayList<T> {
        return ArrayList(this)
    }
}