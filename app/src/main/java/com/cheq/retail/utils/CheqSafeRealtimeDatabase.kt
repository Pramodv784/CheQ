package com.cheq.retail.utils

import android.util.Log
import androidx.annotation.Keep
import com.cheq.retail.constants.Constant.Companion.CHEQ_SAFE_BANNER
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.Constant.Companion.CHEQ_SAFE
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.main.*
import com.google.firebase.database.*
import com.cheq.retail.ui.main.model.CheqSafeData
import com.cheq.retail.ui.socialLogin.model.CheqSafeScreens
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
@Keep
object CheqSafeRealtimeDatabase {
    private lateinit var mFirebasedatabase: FirebaseDatabase
    fun checkSafeFromFb(from: CheqSafeScreens, onValueFetched: (CheqSafeData?) -> Unit) {
        //mFirebasedatabase = FirebaseDatabase.getInstance("https://cheqmobileappdev-default-rtdb.firebaseio.com/")
        mFirebasedatabase = FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
         var cheqSafeDb = mFirebasedatabase.getReference(CHEQ_SAFE)
        cheqSafeDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cheqSafeBannerData = snapshot.getValue(CheqSafeBannerResponse::class.java)

                if (cheqSafeBannerData?.cheqsafe_visibility == true) {
                    when (from) {
                        CheqSafeScreens.HOME -> {
                            onValueFetched(
                                CheqSafeData(
                                    isVisible = cheqSafeBannerData.feature_config_home?.rule?.platform?.android == true && checkWhiteListUser(cheqSafeBannerData.feature_config_home.rule.whitelist),
                                    successBannerUrl = cheqSafeBannerData.feature_config_home?.banners?.initial,
                                    failedBannerUrl = cheqSafeBannerData.feature_config_home?.banners?.failed,
                                )
                            )
                        }
                        CheqSafeScreens.SETTING -> {
                            onValueFetched(
                                CheqSafeData(
                                    isVisible = cheqSafeBannerData.feature_config_setting?.rule?.platform?.android == true && checkWhiteListUser(cheqSafeBannerData.feature_config_setting.rule.whitelist),
                                    successBannerUrl = "",
                                    failedBannerUrl = "",
                                )
                            )
                        }
                        CheqSafeScreens.PAYMENT -> {
                            onValueFetched(
                                CheqSafeData(
                                    isVisible = cheqSafeBannerData.feature_config_payment?.rule?.platform?.android == true && checkWhiteListUser(cheqSafeBannerData.feature_config_payment.rule.whitelist),
                                    successBannerUrl = cheqSafeBannerData.feature_config_payment?.banners?.initial,
                                    failedBannerUrl = cheqSafeBannerData.feature_config_payment?.banners?.failed,
                                )
                            )
                        }
                        CheqSafeScreens.CONVERT_TO_CASE -> {
                            onValueFetched(
                                CheqSafeData(
                                    isVisible = cheqSafeBannerData.feature_config_ctc?.rule?.platform?.android == true && checkWhiteListUser(cheqSafeBannerData.feature_config_ctc.rule.whitelist),
                                    successBannerUrl = cheqSafeBannerData.feature_config_ctc?.banners?.initial,
                                    failedBannerUrl = cheqSafeBannerData.feature_config_ctc?.banners?.failed,
                                )
                            )
                        }
                    }
                    /*onValueFetched(
                        CheqSafeData(
                        isGlobalVisible = true,
                            successBannerUrl = cheqSafeBannerData.featureConfigHome.banner.initial,
                            failedBannerUrl = cheqSafeBannerData.featureConfigHome.banner.failed,
                            isAndroid = cheqSafeBannerData.featureConfigHome.rule.android,
                            isWhitelistUser = checkWhiteListUser(userId,cheqSafeBannerData.featureConfigHome.rule.whiteListUser)
                    )*/

                } else {
                    onValueFetched(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onValueFetched(null)
            }

        })
    }

    private fun checkWhiteListUser(whiteListUser: WhitelistUser?): Boolean {
        val cheqUserId =
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID)

        return if (whiteListUser?.enabled == true) {
            whiteListUser.whitelist_users?.contains(cheqUserId) == true
        }else {
            true
        }

    }
}