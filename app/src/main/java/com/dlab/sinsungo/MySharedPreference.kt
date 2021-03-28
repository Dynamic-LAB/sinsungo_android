package com.dlab.sinsungo

import android.content.Context
import android.content.SharedPreferences

class MySharedPreference(context: Context) {
    private val PREFS_FILE_NAME = "prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)

    private val DEFAULT_STRING_VALUE = ""
    private val DEFAULT_INT_VALUE = 0
    private val DEFAULT_BOOLEAN_VALUE = false
    private val DEFAULT_FLOAT_VALUE = 0.0F
    private val DEFAULT_LONG_VALUE = 0L

    fun getString(key: String): String? {
        return prefs.getString(key, DEFAULT_STRING_VALUE)
    }

    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getInt(key: String): Int {
        return prefs.getInt(key, DEFAULT_INT_VALUE)
    }

    fun setInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, DEFAULT_BOOLEAN_VALUE)
    }

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getFloat(key: String): Float {
        return prefs.getFloat(key, DEFAULT_FLOAT_VALUE)
    }

    fun setFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun getLong(key: String): Long {
        return prefs.getLong(key, DEFAULT_LONG_VALUE)
    }

    fun setLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }
}
