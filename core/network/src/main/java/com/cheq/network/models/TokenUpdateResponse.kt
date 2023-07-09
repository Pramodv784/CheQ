package com.cheq.network.models

data class TokenUpdateResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val apiMessage: String? = null,
    val status: Boolean? = null
)