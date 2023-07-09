package com.cheq.retail.receiver

import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.utils.MparticleUtils
import com.smsapi.ReceiveSMS


/**
 * Created by Sanket Mendon on 29,May,2023.
 * sanket@cheq.one
 */
class CheqFinartReceiver : ReceiveSMS() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SMS_RECEIVED_ACTION) {
            super.onReceive(context, intent)
            MparticleUtils.logFinartEvent(
                AFConstants.FINART_BACKGROUND_SMS_PROCESSING,
                AFConstants.SCREEN_BACKGROUND
            )
        }
    }
}