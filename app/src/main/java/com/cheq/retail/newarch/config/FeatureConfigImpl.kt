package com.cheq.retail.newarch.config

import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsConstants
import com.cheq.common.config.remoteconfig.RemoteConfig
import com.cheq.config.AppConfig
import com.cheq.config.FeatureConfig
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
class FeatureConfigImpl @Inject constructor(
    private val appConfig: AppConfig,
    private val remoteConfig: RemoteConfig,
    @SharedPrefsCheQ private val sharedPrefs: SharedPrefs
): FeatureConfig {
    override fun isNewProfileEnabled(): Boolean {
        return if (appConfig.isDebugApp()) {
            sharedPrefs.getBoolean(SharedPrefsConstants.NEW_PROFILE_ENABLED)
        } else {
            remoteConfig.getBoolean(RemoteConfig.NEW_ARCH_ENABLED)
        }
    }
}