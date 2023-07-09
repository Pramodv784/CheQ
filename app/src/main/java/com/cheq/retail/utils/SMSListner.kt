package com.cheq.retail.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.cheq.retail.ui.verifyOtp.OTPInterface.OTPListener
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern


class SMSListner : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // this function is trigged when each time a new SMS is received on device.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val data = intent.extras
            val format: String? = data?.getString("format")
            var pdus: Array<Any?>? = arrayOfNulls(0)
            if (data != null) {
                pdus =
                    data["pdus"] as Array<Any?>? // the pdus key will contain the newly received SMS
            }
            try {
                if (pdus != null) {
                    for (pdu in pdus) { // loop through and pick up the SMS of interest
                        val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray?, format)
                        if (smsMessage.messageBody?.lowercase()?.contains("cheq") == true) {
                            val matcher = Pattern.compile("\\d+").matcher(smsMessage.messageBody)
                            CoroutineScope(Dispatchers.Main).launch {
                                invalidate(matcher)
                            }
                        }
                        break
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun invalidate(matcher: Matcher) {
        while (matcher.find()) {
            val otpReceived: String? = matcher.group()
            otpReceived?.let {
                if (it.length == 4) mListener?.onOTPReceived(it)
            }

        }
    }

    companion object {
        private var mListener // this listener will do the magic of throwing the extracted OTP to all the bound views.
                : OTPListener? = null

        fun bindListener(listener: OTPListener?) {
            mListener = listener
        }

        fun unbindListener() {
            mListener = null
        }
    }
}
