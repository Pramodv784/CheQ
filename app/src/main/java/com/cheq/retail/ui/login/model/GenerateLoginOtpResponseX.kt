package com.cheq.retail.ui.login.model

import androidx.annotation.Keep
import com.cheq.retail.ui.splash.model.DataX
import com.google.gson.annotations.SerializedName

@Keep
data class GenerateLoginOtpResponseX(
    val OTP: String,
    val cheqUserId: String,
    val deviceId: String,
    val messageId: String,@field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
)