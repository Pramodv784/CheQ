package com.cheq.retail.base

import android.util.Log
import com.cheq.retail.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.net.UnknownHostException

object FirebaseRemoteConfigUtils {

    //We can define the firebase keys used here
    private const val USER_NET_EARNING_TEXT = "user_net_earning_text"
    private const val NET_EARNING_TITLE = "net_earnings_title"
    private const val NET_EARNING_SUBTITLE = "net_earnings_subtitle"
    private const val NON_NET_EARNING_TITLE = "non_net_earnings_title"
    private const val NON_NET_EARNING_SUBTITLE = "non_net_earnings_subtitle"
    private const val NET_EARNING_PRIMARY_CTA = "net_earnings_primary_cta"
    private const val NET_EARNING_SECONDARY_CTA = "net_earnings_secondary_cta"
    private const val NON_NET_EARNING_PRIMARY_CTA = "non_net_earnings_primary_cta"
    private const val NON_NET_EARNING_SECONDARY_CTA = "non_net_earnings_secondary_cta"
    private const val LIMIT_BREACH_FIRST_TEXT = "limit_breach_first_text"
    private const val LIMIT_BREACH_LAST_TEXT = "limit_breach_last_text"
    private const val PAY_TOGETHER_EARNINGS_TEXT = "pay_together_earnings_text"
    private const val ADDITIONAL_FEES_TEXT = "additional_fees_text"
    private const val EARNINGS_TEXT = "earnings_text"
    private const val UPI_LIMIT_EXCEED_NUDGE = "upi_limit_exceed_nudge"
    private const val UPI_AMOUNT_LIMIT = "upi_amount_limit"
    private const val FIREBASE_MINIMUM_FETCH_INTERVAL: Long = 3600
    private const val CHEQ_SAFE_PRIMARY_CTA = "cheq_safe_primary_cta"
    private const val CHEQ_SAFE_SECONDARY_CTA = "cheq_safe_secondary_cta"
    private const val CHEQ_SAFE_TITLE = "cheq_safe_title"
    private const val CHEQ_SAFE_SUB_TITLE = "cheq_safe_subtitle"
    private const val SPLASH_RETRY_COUNT = "splash_retry_count"
    private const val SPLASH_EXHAUST_MESSAGE = "splash_exhaust_message"
    private const val NEW_ARCH_FLAG = "enable_new_arch"
    private lateinit var remoteConfig: FirebaseRemoteConfig

    fun init() {
        remoteConfig = getFirebaseRemoteConfig()
    }

    private fun getFirebaseRemoteConfig(): FirebaseRemoteConfig {
        try {
         remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = FIREBASE_MINIMUM_FETCH_INTERVAL
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener {}
        } catch (e: UnknownHostException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return remoteConfig
    }

    fun getNetEarningText(): String = remoteConfig.getString(USER_NET_EARNING_TEXT)

    fun getNetEarningsTitleText(): String = remoteConfig.getString(NET_EARNING_TITLE)
    fun getNetEarningsSubTitleText(): String = remoteConfig.getString(NET_EARNING_SUBTITLE)

    fun getNonNetEarningsTitleText(): String = remoteConfig.getString(NON_NET_EARNING_TITLE)
    fun getNonNetEarningsSubTitleText(): String = remoteConfig.getString(NON_NET_EARNING_SUBTITLE)

    fun getNetEarningsPrimaryCtaText(): String = remoteConfig.getString(NET_EARNING_PRIMARY_CTA)
    fun getNetEarningsSecondaryCtaText(): String = remoteConfig.getString(NET_EARNING_SECONDARY_CTA)

    fun getNonNetEarningsPrimaryCtaText(): String = remoteConfig.getString(
        NON_NET_EARNING_PRIMARY_CTA
    )

    fun getNonNetEarningsSecondaryCtaText(): String = remoteConfig.getString(
        NON_NET_EARNING_SECONDARY_CTA
    )

    fun getLimitBreachFirstText(): String = remoteConfig.getString(
        LIMIT_BREACH_FIRST_TEXT
    )

    fun getLimitBreachLastText(): String = remoteConfig.getString(
        LIMIT_BREACH_LAST_TEXT
    )

    fun getPayTogetherEarningsText(): String = remoteConfig.getString(
        PAY_TOGETHER_EARNINGS_TEXT
    )

    fun getEarningsText(): String = remoteConfig.getString(
        EARNINGS_TEXT
    )

    fun getAdditionalFeesText(): String = remoteConfig.getString(
        ADDITIONAL_FEES_TEXT
    )

    fun getUpiLimitExceedText(): String = remoteConfig.getString(
        UPI_LIMIT_EXCEED_NUDGE
    )
    fun getUpiLimit(): String = remoteConfig.getString(
        UPI_AMOUNT_LIMIT
)
    fun getCheqSafePrimaryCtaText(): String = remoteConfig.getString(
        CHEQ_SAFE_PRIMARY_CTA
    )

    fun getCheqSafeSecondaryCtaText(): String = remoteConfig.getString(
        CHEQ_SAFE_SECONDARY_CTA
    )

    fun getCheqSafeTitleText(): String = remoteConfig.getString(
        CHEQ_SAFE_TITLE
    )

    fun getCheqSafeSubTitleText(): String = remoteConfig.getString(
        CHEQ_SAFE_SUB_TITLE
    )

    fun getSplashRetryCount() : Int = remoteConfig.getLong(
        SPLASH_RETRY_COUNT).toInt()

    fun getSplashExhaustText(): String = remoteConfig.getString(
        SPLASH_EXHAUST_MESSAGE
    )


    fun isNewArchEnabled(): Boolean = remoteConfig.getBoolean(NEW_ARCH_FLAG)
}