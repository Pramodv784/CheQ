package com.cheq.retail.utils

import com.cheq.retail.BuildConfig
import com.cheq.retail.constants.Constant
import com.cheq.retail.ui.main.CheqSafeBannerResponse
import com.cheq.retail.ui.main.OfferWidgetResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object CommonFirebaseData {
    private lateinit var mFirebasedatabase: FirebaseDatabase

    fun getDataBaseRefrence(refrence: String, onValueFetched: (OfferWidgetResponse) -> Unit) {
        mFirebasedatabase = FirebaseDatabase.getInstance(BuildConfig.firebaseDataBaseURL)

        var cheqSafeDb = mFirebasedatabase.getReference(refrence)
        cheqSafeDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val offerData = snapshot.getValue(OfferWidgetResponse::class.java)
                if (offerData != null) {
                    onValueFetched(offerData)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}