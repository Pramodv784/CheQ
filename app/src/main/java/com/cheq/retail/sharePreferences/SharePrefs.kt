package com.cheq.retail.sharePreferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants.BANK_BASE
import com.cheq.retail.constants.AFConstants.BANNER_BASE
import com.cheq.retail.constants.AFConstants.FAQS_BASE
import com.cheq.retail.constants.AFConstants.FILENAME_CHEQ
import com.cheq.retail.constants.AFConstants.FILENAME_CHEQ_PERMISSION
import com.cheq.retail.constants.AFConstants.HTML_BASE
import com.cheq.retail.constants.AFConstants.LOAN_BASE
import com.cheq.retail.constants.AFConstants.POLICIES_BASE
import com.cheq.retail.constants.AFConstants.VOUCHER_BASE
import com.cheq.retail.constants.AFConstants.WAITLIST_BASE
import com.cheq.retail.ui.login.model.GetUserDetailsResponseModel
import com.cheq.retail.ui.splash.model.GetDeviceIdResponse
import com.cheq.retail.ui.splash.model.ProfileDetailsResponse
import com.cheq.retail.ui.verifyOtp.model.DataVerifyOtp
import com.cheq.retail.ui.verifyOtp.model.VerifyOtpResponse


class SharePrefs private constructor(ctx: Context) {

    var sharedPreferences: SharedPreferences? =
        MainApplication.getApplicationContext().getSharedPreferences(FILENAME_CHEQ, Activity.MODE_PRIVATE)

    var sharedPreferencesPermission: SharedPreferences? =
        MainApplication.getApplicationContext().getSharedPreferences(FILENAME_CHEQ_PERMISSION, Activity.MODE_PRIVATE)


    companion object {
        var instance: SharePrefs? = null
        fun getInstance(ctx: Context): SharePrefs? {
            if (instance == null) instance = SharePrefs(MainApplication.getApplicationContext())
            return instance
        }
    }

    fun putString(key: String?, value: String?) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    fun getString(key: String?): String? {
        return sharedPreferences?.getString(key, "")
    }

    fun getBankString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return BANK_BASE
        return sharedPreferences?.getString(key, "")
    }

    fun getLoanString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return LOAN_BASE
        return sharedPreferences?.getString(key, "")
    }

    fun getBannerString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return BANNER_BASE
        return sharedPreferences?.getString(key, "")
    }

    fun getHtmlString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return HTML_BASE
        return sharedPreferences?.getString(key, "")
    }

    fun getPoliciesString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return POLICIES_BASE
        return sharedPreferences?.getString(key, "")
    }

    fun getFaqString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return FAQS_BASE
        return sharedPreferences?.getString(key, "")
    }

    fun getVoucherString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return VOUCHER_BASE
        return sharedPreferences?.getString(key, "")
    }
    fun getCVVString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return FAQS_BASE
        return sharedPreferences?.getString(key, "")
    }
    fun getWaitListString(key: String?): String? {
        val value = sharedPreferences?.getString(key, "")
        if (value.isNullOrEmpty() || value == "null")
            return WAITLIST_BASE
        return sharedPreferences?.getString(key, "")
    }

    fun putInt(key: String?, value: Int?) {
        sharedPreferences?.edit()?.putInt(key, value?:0)?.apply()
    }

    fun putBoolean(key: String?, value: Boolean?) {
        sharedPreferences?.edit()?.putBoolean(key, value?:false)?.apply()
    }

    fun getBoolean(key: String?): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }


    fun getInt(key: String?): Int? {
        return sharedPreferences?.getInt(key, 0)
    }

    fun clearSharePrefs() {
        sharedPreferences?.edit()?.clear()?.apply()
    }

    fun setSharedPreferences(response: VerifyOtpResponse?) {
        putBoolean(SharePrefsKeys.IS_LOGGED_IN, true)

    }

    fun setSharedPreferences(response: ProfileDetailsResponse) {
        putString(SharePrefsKeys.MOBILE_NUMBER, response.mobile)
        putString(SharePrefsKeys.FIRST_NAME, response.firstname)
        putString(SharePrefsKeys.LAST_NAME, response.lastname)
        putString(SharePrefsKeys.USER_EMAIL, response.email)
        putString(SharePrefsKeys.DOB, response.dateofbirth)
        putString(SharePrefsKeys.PAN_CARD, response.pancard)
        putBoolean(SharePrefsKeys.IS_EMANDATE_COMPLETE, response.isEmandateDone)
    }

    fun setSharedPreferences(deviceIdResponse: GetDeviceIdResponse) {
        putString(SharePrefsKeys.MOBILE_NUMBER, deviceIdResponse.mobile)
        putString(SharePrefsKeys.FIRST_NAME, deviceIdResponse.firstName)
        putString(SharePrefsKeys.LAST_NAME, deviceIdResponse.lastName)
        putBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE, true)
    }

    fun setSharedPreferences(response: DataVerifyOtp?) {
        putString(SharePrefsKeys.MOBILE_NUMBER, response?.user?.mobile)
        putBoolean(SharePrefsKeys.IS_LOGGED_IN, true)
        //  putString(SharePrefsKeys.ACCESS_TOKEN, response.token.accessToken)
        // putString(SharePrefsKeys.REFRESH_TOKEN, response.token.refreshToken)
        putBoolean(SharePrefsKeys.IS_EMANDATE_COMPLETE, response?.user?.isEmandateDone)
    }

    fun setUserData(response: GetUserDetailsResponseModel) {
        putString(SharePrefsKeys.MOBILE_NUMBER, response.user.mobile)
        putString(SharePrefsKeys.FIRST_NAME, response.user.firstName)
        putString(SharePrefsKeys.LAST_NAME, response.user.lastName)
        putString(SharePrefsKeys.USER_EMAIL, response.user.email)
        putString(SharePrefsKeys.DOB, response.user.dateOfBirth)
        putString(SharePrefsKeys.PAN_CARD, response.user.panCard)
        // putString(SharePrefsKeys.ACCESS_TOKEN, response.token)

        putBoolean(SharePrefsKeys.IS_LOGGED_IN, true)
        putBoolean(SharePrefsKeys.IS_EMANDATE_COMPLETE, response.user.isEmandateDone)
    }

    fun isNeverAskAgainPermission(): Boolean? {
        return sharedPreferencesPermission?.getBoolean(SharePrefsKeys.IS_PERMISSION_DENIED, false)
    }

    fun markNeverAskAgainPermission(status: Boolean) {
        sharedPreferencesPermission?.edit()?.putBoolean(SharePrefsKeys.IS_PERMISSION_DENIED, status)
            ?.apply()
    }
}