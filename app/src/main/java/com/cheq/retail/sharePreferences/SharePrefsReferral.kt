package com.cheq.retail.sharePreferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SharePrefsReferral private constructor(ctx: Context) {

    var referralSharedPref: SharedPreferences? =
        MainApplication.getApplicationContext().getSharedPreferences(AFConstants.FILENAME_REFERRAL, Activity.MODE_PRIVATE)

    companion object {
        var instance: SharePrefsReferral? = null
        fun getInstance(ctx: Context): SharePrefsReferral? {
            if (instance == null) instance = SharePrefsReferral(MainApplication.getApplicationContext())
            return instance
        }
    }

    fun setReferralId(key: String, value: String?) {
        referralSharedPref?.edit()?.putString(key, value)?.apply()
    }

    fun getReferralId(key: String): String? {
        return referralSharedPref?.getString(key, "")
    }

    fun setCardShown(key: String, value: Boolean) {
        referralSharedPref?.edit()?.putBoolean(key, value)?.apply()
    }

    fun isCardShown(key: String): Boolean? {
        return referralSharedPref?.getBoolean(key,false)
    }

    fun putString(key: String?, value: String?) {
        referralSharedPref?.edit()?.putString(key, value)?.apply()

    }

    fun putLong(key: String?, value: Long) {
        referralSharedPref?.edit()?.putLong(key, value)?.apply()

    }

    fun getString(key: String?): String? {
        return referralSharedPref?.getString(key, "")

    }

    fun putList(key: String?, list: ArrayList<String>?) {
        val value  = list?.map { AFConstants.SHA_256 +it}
        val gson = Gson()
        val json: String = gson.toJson(value)
        referralSharedPref?.edit()?.putString(key, json)?.apply()

    }

    fun getList(key: String?): ArrayList<String>? {
        val gson = Gson()
        val json: String? = referralSharedPref?.getString(key, "")

        val type: Type = object : TypeToken<java.util.ArrayList<String?>?>() {}.getType()
        return gson.fromJson(json, type)
    }

    fun putBoolean(key: String?, value: Boolean?) {
        referralSharedPref?.edit()?.putBoolean(key, value?:false)?.apply()

    }

    fun getBoolean(key: String?): Boolean? {
        return referralSharedPref?.getBoolean(key, false)

    }

    fun getSslDelay(key: String): Long? {
        return referralSharedPref?.getLong(key, 3000)

    }

    fun clearSharePrefs() {
        referralSharedPref?.edit()?.clear()?.apply()
    }
}