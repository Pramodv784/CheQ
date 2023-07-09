package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TokenUpdateResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val apiMessage: String? = null,


    val status: Boolean? = null
)