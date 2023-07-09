package com.cheq.retail.ui.login.model

import androidx.annotation.Keep

@Keep
data class GenerateLoginOtpResponse(
    var apiMessage: String,
    var OTP: String,
    val messageId: String,
    var login: Boolean
)


