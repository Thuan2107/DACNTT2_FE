package com.example.chatapplication.cache

import com.example.chatapplication.constant.AppConstants
import com.example.chatapplication.helper.PrefUtils.Companion.getString
import com.example.chatapplication.helper.PrefUtils.Companion.putString
import com.example.chatapplication.model.Config
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

object ConfigCache {
    private var mConfig: Config = Config()

    private val mmkv: MMKV = MMKV.mmkvWithID("cache_config")

    fun saveConfig(config: Config?) {
        try {
            mmkv.remove(AppConstants.CACHE_CONFIG)
            mmkv.putString(AppConstants.CACHE_CONFIG, Gson().toJson(config)).commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getConfig(): Config {
        try {
            mConfig = Gson().fromJson(mmkv.getString(AppConstants.CACHE_CONFIG, ""), Config::class.java)
        } catch (e: Exception) {
            e.stackTrace
        }
        return mConfig
    }

}