package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep

@Keep
data class Token(
    val accessToken: String,
    val error: Any,
    val message: String,
    val refreshToken: String,
    val response: Any,
    val status: Boolean
)