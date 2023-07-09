package com.cheq.retail.sharePreferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants

class SharePrefCheqUserId private constructor(ctx: Context) {

    var sharePrefCheqUserId: SharedPreferences? =
        MainApplication.getApplicationContext().getSharedPreferences(AFConstants.FILENAME_CHEQUSERID, Activity.MODE_PRIVATE)

    companion object {
        var instance: SharePrefCheqUserId? = null
        @JvmStatic fun getInstance(ctx: Context): SharePrefCheqUserId? {
            if (instance == null) instance = SharePrefCheqUserId(MainApplication.getApplicationContext())
            return instance
        }
    }

    fun putString(key: String?, value: String?) {
        sharePrefCheqUserId?.edit()?.putString(key, value)?.apply()
    }

    fun getString(key: String?): String? {
        return sharePrefCheqUserId?.getString(key, "")
    }

    fun clearSharePrefs() {
        sharePrefCheqUserId?.edit()?.clear()?.apply()
    }
}