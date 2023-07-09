package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ReferralStaticData(

    @field:SerializedName("OfferValid")
    val offerValid: Boolean? = null,

    @field:SerializedName("youGetValue")
    val youGetValue: Int? = null,

    @field:SerializedName("youGetMessage")
    val youGetMessage: String? = null,

    @field:SerializedName("httpStatus")
    val httpStatus: Int? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("defaultMessage")
    val defaultMessage: String? = null,

    @field:SerializedName("friendsGetValue")
    val friendsGetValue: Int? = null,

    @field:SerializedName("chips")
    val chips: Int? = null,

    @field:SerializedName("friendsGetMessage")
    val friendsGetMessage: String? = null,

    @field:SerializedName("steps")
    val steps: List<String?>? = null,

    @field:SerializedName("whatsappMessage")
    val whatsappMessage: String? = null,

    @field:SerializedName("daysLeft")
    val daysLeft: String? = null
)