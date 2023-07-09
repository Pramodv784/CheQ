package com.cheq.retail.ui.verifyOtp

public interface OTPInterface {
    interface OTPListener {
        fun onOTPReceived( otp:String)
    }
}