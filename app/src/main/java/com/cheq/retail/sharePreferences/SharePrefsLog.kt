package com.cheq.retail.sharePreferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants

class SharePrefsLog private constructor(ctx: Context) {

    var sharedPreferencesLogScreen: SharedPreferences? =
        MainApplication.getApplicationContext().getSharedPreferences(AFConstants.FILENAME_LOG_SCREEN, Activity.MODE_PRIVATE)

    companion object {
        var instance: SharePrefsLog? = null
        fun getInstance(ctx: Context): SharePrefsLog? {
            if (instance == null) instance = SharePrefsLog(MainApplication.getApplicationContext())
            return instance
        }
    }

    fun putLogBoolean(key: String?, value: Boolean?) {
        sharedPreferencesLogScreen?.edit()?.putBoolean(key, value?:false)?.apply()
    }

    fun getLogBoolean(key: String?): Boolean? {
        return sharedPreferencesLogScreen?.getBoolean(key, false)
    }

    fun clearSharePrefs() {
        sharedPreferencesLogScreen?.edit()?.clear()?.apply()
    }
}