package com.cheq.retail.utils

import android.app.Activity
import android.util.Log
import com.cheq.retail.constants.Constant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object RealTimeDatabase {
    private var db = Firebase.database.reference
    private const val TABLE_NAME = "config"
    const val IS_IN_APP_REVIEW_ENABLE = "isInAppReviewEnable"
    const val SHOW_APP_RATING_REWARDS = "show_app_rating_rewards"
    const val SHOW_APP_RATING_C2C = "show_app_rating_c2c"

    fun checkIsInAppReviewEnable(key: String, onValueFetched: (Boolean) -> Unit) {

        db
            .child(TABLE_NAME)
            .child(key)
            .get()
            .addOnCompleteListener {
                val isEnabled = if(it.isSuccessful) {
                    (it.result.value as? Boolean) == true
                }else {
                    false
                }
                onValueFetched(isEnabled)
            }

        /*
        db.child(TABLE_NAME).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val isInAppReviewEnable = snapshot.child(IS_IN_APP_REVIEW_ENABLE).value as Boolean

                if (isInAppReviewEnable) {
                    InAppReview.requestReview(activity)
                } else {
                    // Nothing
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("CHECK", "loadPost:onCancelled", databaseError.toException())
            }

        })

         */
    }
}