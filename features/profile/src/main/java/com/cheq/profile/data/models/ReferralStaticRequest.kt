package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
data class ReferralStaticRequest(
    @SerializedName("cheqUserId")
    val cheqUserId: String? = null,
)
