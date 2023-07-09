package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BinCheckBodyRequest(
    @field:SerializedName("cardNumber") val cardNumber: String? = null,

    @field:SerializedName("bankmasterId") val bankmasterId: String? = null,

    @field:SerializedName("productId") val productId: String? = null,
)
