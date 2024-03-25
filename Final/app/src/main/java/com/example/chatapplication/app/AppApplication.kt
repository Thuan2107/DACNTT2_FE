package com.example.chatapplication.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.cloudinary.android.MediaManager
import com.example.chatapplication.R
import com.example.chatapplication.cache.HttpLoggerCache
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.manager.ActivityManager
import com.example.chatapplication.model.RequestHandler
import com.example.chatapplication.model.RequestServer
import com.example.chatapplication.other.ToastStyle
import com.example.chatapplication.utils.AppUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestInterceptor
import com.hjq.http.model.HttpHeaders
import com.hjq.http.model.HttpParams
import com.hjq.http.request.HttpRequest
import com.hjq.toast.ToastLogInterceptor
import com.hjq.toast.ToastUtils
import com.tencent.mmkv.MMKV
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.PollingXHR
import io.socket.engineio.client.transports.WebSocket
import okhttp3.OkHttpClient
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory



class AppApplication : Application() {
    private var config: MutableMap<String, String> = HashMap()

    override fun onCreate() {
        super.onCreate()
        config["cloud_name"] = "ds9clp4oy"
        config["api_key"] = "246469816168789"
        config["api_secret"] = "tGRwvKzjCbs1jRdgLM4s1rCtuKI"
            MediaManager.init(this, config)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initSdk(this)


        instance = this

        widthDevice = Resources.getSystem().displayMetrics.widthPixels
        heightDevice = Resources.getSystem().displayMetrics.heightPixels
    }



    companion object {
        var instance: AppApplication? = null

        fun applicationContext(): AppApplication {
            return instance as AppApplication
        }

        @SuppressLint("HardwareIds")
        fun initSdk(application: Application) {

            // Khởi tạo toast
            ToastUtils.init(application, ToastStyle())

            // cài đặt Toast
            ToastUtils.setInterceptor(ToastLogInterceptor())

            // cài đặt Crash
//            CrashHandler.register(application)

            //Lưu cache
            MMKV.initialize(application)

            // Activity
            ActivityManager.getInstance().init(application)


            // Create the Collector
            val chuckerCollector = ChuckerCollector(
                context = application,
                // Toggles visibility of the notification
                showNotification = true,
                // Allows to customize the retention period of collected data
                retentionPeriod = RetentionManager.Period.ONE_HOUR
            )
            // Create the Interceptor
            val chuckerInterceptor = ChuckerInterceptor.Builder(application)
                // The previously created Collector
                .collector(chuckerCollector)
                // The max body content length in bytes, after this responses will be truncated.
                .maxContentLength(250_000L)
                // List of headers to replace with ** in the Chucker UI
                .redactHeaders("Auth-Token", "Bearer")
                // Read the whole response body even when the client does not consume the response completely.
                // This is useful in case of parsing errors or when the response body
                // is closed before being read like in Retrofit with Void and Unit types.
                .alwaysReadResponseBody(true)
                // Use decoder when processing request and response bodies. When multiple decoders are installed they
                // are applied in an order they were added.
                // Controls Android shortcut creation.
                .build()



            // Khởi tạo khung yêu cầu mạng
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .apply { if (HttpLoggerCache.getHttpLogEnable()) {
                    addInterceptor(chuckerInterceptor)
                }}
                .build()

            //Khởi tạo máy chủ java
            EasyConfig.with(okHttpClient)
                // có in nhật ký không
//                .setLogEnabled(AppConfig.isLogEnable())
                //Thiết lập cấu hình máy chủ
                .setServer(RequestServer())
                //Đặt chính sách xử lý yêu cầu
                .setHandler(RequestHandler(application))
                // Đặt bộ chặn tham số yêu cầu
                .setInterceptor(object : IRequestInterceptor {
                    override fun interceptArguments(
                        httpRequest: HttpRequest<*>, params: HttpParams, headers: HttpHeaders
                    ) {
                        headers.put("time", System.currentTimeMillis().toString())
                        headers.put("Content-Type", "application/json")
                        headers.put("Cache-Control", "no-store")
                        headers.put("Cache-Control", "no-cache")
                        headers.put(
                            "udid", Settings.Secure.getString(
                                application.contentResolver, Settings.Secure.ANDROID_ID
                            )
                        )
                        headers.put("os_name", "android")
                    }
                })
                // Đặt số lần yêu cầu thử lại
                .setRetryCount(1)
                // Đặt thời gian thử lại yêu cầu
                .setRetryTime(2000).into()


            // Đăng ký theo dõi thay đổi trạng thái mạng
            val connectivityManager: ConnectivityManager? =
                ContextCompat.getSystemService(application, ConnectivityManager::class.java)
            connectivityManager?.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                @SuppressLint("MissingPermission")
                override fun onLost(network: Network) {
                    val topActivity: Activity? = ActivityManager.getInstance().getTopActivity()
                    if (topActivity !is LifecycleOwner) {
                        return
                    }
                    val lifecycleOwner: LifecycleOwner = topActivity
                    if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) {
                        return
                    }
                    ToastUtils.show(R.string.common_network_error)
                }
            })

            if (UserCache.isLogin()) {
                socketChat = getSocketChatInstance()!!
//                socketLogin = getSocketLoginInstance()
//                getSocketOrderInstance()
            }


            //Bỏ qua bảo mật khi domain không có ssl
            sslHostnameSocket()

            //Setup socket chat

        }

        // Biến mặc định để lưu cache
        lateinit var kv: MMKV

        var socketChat: Socket? = null

        var socketOrder: Socket? = null

        var socketLogin: Socket? = null


        var widthDevice: Int = 0
        var heightDevice: Int = 0


        private fun getSocketChatInstance(): Socket? {
            val url = AppConstants.IP_ADDRESS
            Timber.d("%s", String.format("device=%s", AppUtils.getDeviceName()))
            if (UserCache.isLogin()) {
                if (url != "") {
                    val options = IO.Options.builder()
                        .setExtraHeaders(
                            Collections.singletonMap(
                                "Authorization",
                                listOf(UserCache.accessToken())
                            )
                        )
                        .setQuery(String.format("device=%s", AppUtils.getDeviceName()))
                        .setReconnection(true)
                        .setTransports(arrayOf(Polling.NAME, WebSocket.NAME, PollingXHR.NAME))
                        .build()
                    socketChat = IO.socket(url, options).connect()
                    return socketChat
                }
            }
            return null
        }

//        private fun getSocketLoginInstance(): Socket? {
//            //Kiểm tra nếu đường dẫn rỗng hoặc không có http
//            if (!ConfigCache.getConfig().apiOauthNode.equals("") || !ConfigCache.getConfig().apiOauthNode!!.contains(
//                    "http"
//                )
//            ) {
//                if (ConfigCache.getConfig().apiOauthNode != "") {
//                    val options = IO.Options.builder()
//                        .setExtraHeaders(
//                            Collections.singletonMap(
//                                "authorization",
//                                listOf("bearer 1234")
//                            )
//                        )
//                        .setReconnection(true)
//                        .setTransports(arrayOf(Polling.NAME, WebSocket.NAME, PollingXHR.NAME))
//                        .build()
//                    socketLogin = IO.socket(ConfigCache.getConfig().apiOauthNode, options).connect()
//                    return socketLogin
//                }
//
//            }
//            return null
//        }


        private fun sslHostnameSocket() {
            val trustAllCerts: Array<TrustManager> =
                arrayOf(@SuppressLint("CustomX509TrustManager") object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?, authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?, authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder.connectTimeout(20, TimeUnit.SECONDS)
            builder.writeTimeout(1, TimeUnit.MINUTES)
            builder.readTimeout(1, TimeUnit.MINUTES)
            IO.setDefaultOkHttpCallFactory(builder.build())
            IO.setDefaultOkHttpWebSocketFactory(builder.build())
        }

    }
}
