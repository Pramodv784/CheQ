package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName


/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
data class ReferralStaticDto(
    @SerializedName("OfferValid") val offerValid: Boolean? = null,
    @SerializedName("youGetValue") val youGetValue: Int? = null,
    @SerializedName("youGetMessage") val youGetMessage: String? = null,
    @SerializedName("httpStatus") val httpStatus: Int? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("defaultMessage") val defaultMessage: String? = null,
    @SerializedName("friendsGetValue") val friendsGetValue: Int? = null,
    @SerializedName("chips") val chips: Int? = null,
    @SerializedName("friendsGetMessage") val friendsGetMessage: String? = null,
    @SerializedName("steps") val steps: List<String?>? = null,
    @SerializedName("whatsappMessage") val whatsappMessage: String? = null,
    @SerializedName("daysLeft") val daysLeft: String? = null
)