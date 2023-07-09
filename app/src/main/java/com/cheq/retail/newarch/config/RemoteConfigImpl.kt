package com.cheq.retail.newarch.config

import com.cheq.common.config.remoteconfig.RemoteConfig
import com.cheq.common.extension.orFalse
import com.cheq.retail.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
class RemoteConfigImpl @Inject constructor(): RemoteConfig {

    private var remoteConfig: FirebaseRemoteConfig? = null

    init {
        getFirebaseRemoteConfig()
    }

    private fun getFirebaseRemoteConfig() {
        try {
            remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0

            }
            remoteConfig?.setConfigSettingsAsync(configSettings)
            remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
            remoteConfig?.fetchAndActivate()
        } catch (e: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun getString(key: String): String {
        return remoteConfig?.getString(key).orEmpty()
    }

    override fun getBoolean(key: String): Boolean {
        return remoteConfig?.getBoolean(key).orFalse
    }

}