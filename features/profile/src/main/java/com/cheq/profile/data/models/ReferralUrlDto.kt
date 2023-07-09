package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
data class ReferralUrlDto(
    @SerializedName("referralUrl") var referralUrl: String? = null,
    @SerializedName("upsertedId") val upsertedId: Int? = null,
    @SerializedName("upsertedCount") val upsertedCount: Int? = null,
    @SerializedName("acknowledged") val acknowledged: Boolean? = null,
    @SerializedName("modifiedCount") val modifiedCount: Int? = null,
    @SerializedName("matchedCount") val matchedCount: Int? = null
)