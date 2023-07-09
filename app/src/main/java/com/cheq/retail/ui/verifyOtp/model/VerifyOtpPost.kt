package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep

@Keep
data class VerifyOtpPost(
    var OTP: String,
    var mobile: String,
    var deviceId: String,
    var fcmToken: String,
    var cheqUserId: String,
    var appsflyer_id: String
)