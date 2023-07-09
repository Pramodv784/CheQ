package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep

@Keep
data class VerifyOtpResponse(var user: UserEntity, val token: String, var apiMessage: String) {
    data class UserEntity(
        val mobile: String,
        var __v: Int,
        val email: String,
        val firstName: String,
        val lastName: String
    )
}



