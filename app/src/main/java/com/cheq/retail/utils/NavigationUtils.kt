package com.cheq.retail.utils

import android.app.Activity
import android.content.Intent
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.login.GetPanCardActivity
import com.cheq.retail.ui.login.ProfileActivity
import com.google.firebase.perf.metrics.Trace

class NavigationUtils {
    companion object {
        fun getNavigation(activity: Activity, appStartupTrace: Trace? = null): Intent {
            return when {
                SharePrefs.getInstance(activity)?.getString(SharePrefsKeys.FIRST_NAME)
                    .isNullOrEmpty() && SharePrefs.getInstance(activity)!!
                    .getString(SharePrefsKeys.LAST_NAME).isNullOrEmpty()
                        && SharePrefs.getInstance(activity)!!.getString(SharePrefsKeys.USER_EMAIL)
                    .isNullOrEmpty() -> {
                    Intent(activity, ProfileActivity::class.java)
                }
                SharePrefs.getInstance(activity)!!.getString(SharePrefsKeys.DOB).isNullOrEmpty() &&
                        SharePrefs.getInstance(activity)!!.getString(SharePrefsKeys.PAN_CARD)
                            .isNullOrEmpty() -> {
                    Intent(activity, GetPanCardActivity::class.java)
                }

                else -> {
                    appStartupTrace?.stop()
                    Intent(activity, MainActivity::class.java)
                }
            }
        }

        fun isProfileIncomplete(): Boolean {
            return when {
                SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                    .getString(SharePrefsKeys.FIRST_NAME)
                    .isNullOrEmpty() && SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                    .getString(SharePrefsKeys.LAST_NAME).isNullOrEmpty()
                        && SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                    .getString(SharePrefsKeys.USER_EMAIL)
                    .isNullOrEmpty() -> {
                    true
                }

                SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                    .getString(SharePrefsKeys.DOB)
                    .isNullOrEmpty() -> {
                    true
                }

                SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                    .getString(SharePrefsKeys.PAN_CARD)
                    .isNullOrEmpty() -> {
                    true
                }

                else -> {
                    false
                }
            }
        }
    }
}