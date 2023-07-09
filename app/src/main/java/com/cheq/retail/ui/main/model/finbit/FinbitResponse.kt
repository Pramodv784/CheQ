package com.cheq.retail.ui.main.model.finbit

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class FinbitResponse(
    val access_token: String,
    val expires_in: Int,
    val message: String,
    val refersh_token_expires_in: String,
    val refresh_token: String,
    val status: String,
    val token_type: String
): Serializable