package com.example.chatapplication.helper

import android.content.SharedPreferences

class PrefUtils {
    companion object {
        const val NAME_OLD_TABLE = "NAME_OLD_TABLE"
        const val FIRST_INSTALL = "FIRST_INSTALL"
        const val BRANCH_ID = "BRANCH_ID"
        const val NAME_BRANCH = "Name_Branch"
        const val ADDRESS_BRANCH = "Address_Branch"

        private var preferences: SharedPreferences? = null

        private fun checkForNullKey(key: String?) {
            if (key == null) {
                throw NullPointerException()
            }
        }

        private fun checkForNullValue(value: String?) {
            if (value == null) {
                throw NullPointerException()
            }
        }


        fun putString(key: String?, value: String?) {
            checkForNullKey(key)
            checkForNullValue(value)
            preferences!!.edit().putString(key, value).apply()
        }

        fun putInt(key: String?, value: Int) {
            checkForNullKey(key)
            preferences!!.edit().putInt(key, value).apply()
        }

        fun putLong(key: String?, value: Long) {
            checkForNullKey(key)
            preferences!!.edit().putLong(key, value).apply()
        }

        fun putBoolean(key: String?, value: Boolean?) {
            checkForNullKey(key)
            preferences!!.edit().putBoolean(key, value!!).apply()
        }

        fun getString(key: String?): String? {
            return if (preferences == null) "" else preferences!!.getString(key, "")
        }

        fun getString(key: String?, defaultValue: String?): String? {
            return if (preferences == null) "" else preferences!!.getString(key, defaultValue)
        }

        fun getInt(key: String?, defaultValue: Int): Int {
            return if (preferences == null) 0 else preferences!!.getInt(key, defaultValue)
        }

        fun getLong(key: String?, defaultValue: Long): Long {
            return if (preferences == null) 0 else preferences!!.getLong(key, 0)
        }

        fun getBoolean(key: String?): Boolean {
            return if (preferences == null) false else preferences!!.getBoolean(key, false)
        }
    }
}