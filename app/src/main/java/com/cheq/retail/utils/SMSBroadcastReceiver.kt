package com.cheq.retail.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSBroadcastReceiver : BroadcastReceiver() {
     lateinit var smsBroadcastListener: SmsBroadcastListener
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras: Bundle? = intent.extras
            val smsRetriverStatus: Status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (smsRetriverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent: Intent? =
                        extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                    smsBroadcastListener.onSuccess(messageIntent)
                }
                CommonStatusCodes.TIMEOUT -> {
                    smsBroadcastListener.onFailure()
                }
            }
        }
    }


    interface SmsBroadcastListener {
        fun onSuccess(intent: Intent?)

        fun onFailure()
    }
}