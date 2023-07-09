package com.cheq.retail.newarch.config

import com.cheq.config.CheqCrashlytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class CheqCrashlyticsImpl @Inject constructor(): CheqCrashlytics {
    override fun log(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }
}