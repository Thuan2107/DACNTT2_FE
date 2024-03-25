package com.example.chatapplication.cache

import com.example.chatapplication.constant.AppConstants
import com.tencent.mmkv.MMKV

object HttpLoggerCache {

    private var logEnable = false

    private val mmkv: MMKV = MMKV.mmkvWithID("caches_http_log")

    fun getHttpLogEnable(): Boolean {
        try {
            logEnable = mmkv.getBoolean(AppConstants.CACHE_HTTP_LOG, false)
        } catch (e: Exception) {
            e.stackTrace
        }
        return logEnable
    }

    fun saveHttpLogEnable(enable: Boolean) {
        try {
            mmkv.remove(AppConstants.CACHE_HTTP_LOG)
            mmkv.putBoolean(AppConstants.CACHE_HTTP_LOG, enable).commit()
        } catch (e: Exception) {
            e.stackTrace
        }
    }
}