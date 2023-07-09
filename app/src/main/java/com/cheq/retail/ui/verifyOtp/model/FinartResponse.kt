package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep

@Keep
data class FinartResponse(
    val httpStatus: Int,
    val message: String,
    val status: Boolean
)