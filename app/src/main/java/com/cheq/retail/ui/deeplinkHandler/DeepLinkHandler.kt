package com.cheq.retail.ui.deeplinkHandler

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.Constant
import com.cheq.retail.core.repository.DeeplinkRepository
import com.cheq.retail.ui.accountSettings.PersonalDetailsActivity
import com.cheq.retail.ui.accountSettings.TransactionHistoryActivity
import com.cheq.retail.ui.login.LoginActivity
import com.cheq.retail.ui.login.TermsAndConditionActivity
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.fragment.AppMenuActivity
import com.cheq.retail.ui.onboarding.OnBoardingActivity
import com.cheq.retail.ui.permission.PermissionActivity
import com.cheq.retail.ui.referral.ReferralActivity
import com.cheq.retail.ui.referral.ReferralHistoryActivity
import com.cheq.retail.ui.splash.SplashActivity
import com.freshchat.consumer.sdk.Freshchat
import java.util.*


class DeepLinkHandler : BaseActivity() {

    companion object {

        fun getDeeplinkIntent(applicationContext: Context, deeplinkValue: String): Intent? {

            val supportedDeeplinkPage = deeplinkValue.let {
                try {
                    Pages.valueOf(it.uppercase(Locale.ENGLISH))
                } catch (e: Exception) {
                    null
                }
            }
            return when (supportedDeeplinkPage) {
                Pages.CARD_PAYMENT -> MainActivity.startActivityForDeepLink(
                    applicationContext,
                    R.id.nav_products_fragment
                )
                Pages.SPLASH -> null
                Pages.CVP -> Intent(applicationContext, OnBoardingActivity::class.java)
                Pages.PERMISSIONS -> Intent(applicationContext, PermissionActivity::class.java)
                Pages.NOTIFICATION_PERMISSION -> Intent(
                    applicationContext,
                    SplashActivity::class.java
                )
                Pages.ENTER_PHONE_NUMBER -> Intent(applicationContext, LoginActivity::class.java)
                Pages.PERSONAL_DETAILS -> Intent(
                    applicationContext,
                    PersonalDetailsActivity::class.java
                )
                Pages.MASTER_CONSENT -> Intent(
                    applicationContext,
                    TermsAndConditionActivity::class.java
                ).putExtra("link", "Terms")
                Pages.ACCOUNT_AND_SETTINGS -> Intent(
                    applicationContext,
                    AppMenuActivity::class.java
                )
                Pages.LOADING -> Intent(applicationContext, SplashActivity::class.java)
                Pages.ADDITIONAL_DETAILS_REQUIRED -> Intent(
                    applicationContext,
                    SplashActivity::class.java
                )
                Pages.HOME -> Intent(applicationContext, MainActivity::class.java)
                Pages.DASHBOARD -> Intent(applicationContext, MainActivity::class.java)
                Pages.CHEQ_SAFE -> AppMenuActivity.getStartIntentForCheqSafe(applicationContext)
                Pages.REWARDS -> MainActivity.startActivityForDeepLink(
                    applicationContext,
                    R.id.nav_rewards_fragment
                )
                Pages.TRANSACTION_HISTORY -> Intent(
                    applicationContext,
                    TransactionHistoryActivity::class.java
                )
                Pages.REFERRAL_HISTORY -> Intent(
                    applicationContext,
                    ReferralHistoryActivity::class.java
                )
                Pages.REFER_AND_EARN -> {
                    Intent(applicationContext, ReferralActivity::class.java)
                }

                else -> null
            }
        }

        fun getDeeplinkIntentValue(applicationContext: Context): String? {

            val deeplinkValue = DeeplinkRepository.getInstance(applicationContext).popDeeplink()
                ?: return null
            return deeplinkValue
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppsFlyer()
    }

    private fun initAppsFlyer() {
        val afDevKey: String = Constant.APPSFLYER_KEY
        val appsflyer = AppsFlyerLib.getInstance()
        appsflyer.subscribeForDeepLink { deepLinkResult ->
            val activityName = deepLinkResult.deepLink.deepLinkValue

            if (activityName != null) {
                DeeplinkRepository
                    .getInstance(this@DeepLinkHandler)
                    .storeDeeplink(activityName)

            }

            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }

        appsflyer.init(afDevKey, null, this)
        appsflyer.start(this)
    }

}