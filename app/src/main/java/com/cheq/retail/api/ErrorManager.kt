package com.cheq.retail.api

import android.content.Intent
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.splash.SplashActivity

class ErrorManager {

    companion object {
        fun handleServerError(errorCode: Int) {
            when (errorCode) {
                401 -> {
                    SharePrefs.getInstance(MainApplication.getApplicationContext())
                        ?.clearSharePrefs()
                    SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                        ?.clearSharePrefs()
                    SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                        ?.clearSharePrefs()
                    SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                        .putBoolean(SharePrefsKeys.IS_USER_AUTHENTICATE,false)
                    SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                        .putBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE,true)
                    MainApplication.getApplicationContext().startActivity(
                        Intent(MainApplication.getApplicationContext(), SplashActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    MainApplication.finartLogout()
                }
            }
        }
    }

}