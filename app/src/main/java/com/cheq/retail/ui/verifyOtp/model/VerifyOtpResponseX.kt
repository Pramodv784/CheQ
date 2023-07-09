package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VerifyOtpResponseX(
    val `data`: DataVerifyOtp,
    val fromInterceptor: Boolean,
    val httpStatus: Int,
    val message: String,
    @field:SerializedName("user_err_msg")
    val userErrorMessage: String? = null,
    val requestId: String,
    val status: Boolean
)