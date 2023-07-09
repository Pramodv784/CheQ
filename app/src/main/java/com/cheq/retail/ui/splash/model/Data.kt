package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataCheq(
    val user: User,
    @field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
)