package com.cheq.retail.constants

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseLog {

    fun FireBaseLogEvent(context: Context,_Requirement: String, Event: String, CustomEventNames: String){
        var bundle =Bundle()
        bundle.putString("Requirement",_Requirement)
        bundle.putString("Event",Event)
        bundle.putString("CustomEventNames",CustomEventNames)
        FirebaseAnalytics.getInstance(context).logEvent(Event,bundle)
    }

    fun setCurrentUserForFirebase(context: Context,userId:String){
        FirebaseAnalytics.getInstance(context).setUserId(userId)
    }
}