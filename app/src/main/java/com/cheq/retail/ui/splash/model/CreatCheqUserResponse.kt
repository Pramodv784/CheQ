package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep

@Keep
data class CreatCheqUserResponse(
    val status: Boolean,
    val httpStatus: Int,
    val message: String,
    val `data`: DataCheq,
    val traceId: String,

)