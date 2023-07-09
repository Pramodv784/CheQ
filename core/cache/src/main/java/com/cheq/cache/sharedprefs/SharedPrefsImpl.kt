package com.cheq.cache.sharedprefs

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
@RequiresApi(Build.VERSION_CODES.GINGERBREAD)
class SharedPrefsImpl constructor(
    private val context: Context,
    private val name: SharedPrefsType
) : SharedPrefs {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(name.value, Activity.MODE_PRIVATE)
    }

    override fun getString(key: String, defaultValue: String): String =
        sharedPreferences.getString(key, defaultValue).orEmpty()


    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    override fun clearPrefs() {
        sharedPreferences.edit().clear().apply()
    }

}