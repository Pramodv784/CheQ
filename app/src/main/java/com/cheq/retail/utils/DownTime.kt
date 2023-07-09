package com.cheq.retail.utils

import android.util.Log
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.Constant
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.google.firebase.database.*
import com.google.gson.Gson

object DownTime {
    fun getDownTime(onvalueFetched:(ConfigDataResponse) -> Unit){
        val mFirebaseDatabase: DatabaseReference?
        val mFirebaseInstance: FirebaseDatabase?
        mFirebaseInstance =
            FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance.getReference(Constant.FIREBASE_DOWN_TIME_REF)
        mFirebaseDatabase.keepSynced(true)
        val rateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var  _data = snapshot.getValue(ConfigDataResponse::class.java)
                _data?.let { onvalueFetched.invoke(it) }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        mFirebaseDatabase.addValueEventListener(rateListener)

    }
}