package com.cheq.retail.utils

import android.app.Activity
import android.util.Log
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager


object InAppReview {
    fun requestReview(activity: Activity) {
        try {
            val manager = ReviewManagerFactory.create(activity)

            val request = manager.requestReviewFlow()
            request
                .addOnSuccessListener {
                    val reviewInfo = it
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnSuccessListener { it1 ->
                        MparticleUtils.logEvent(
                            "AppRating_Invoked",
                            "In App Rating invoked ",
                            "Unique",
                            "Continue",
                            null,
                            activity
                        )
                    }
                }
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
        }
    }

}
