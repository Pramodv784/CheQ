package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName


/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
data class ReferralEarnedDto(
    @SerializedName("show") var show: Boolean? = false,
    @SerializedName("totalRewardsEarned") var totalRewardsEarned: Int? = 0,
    @SerializedName("httpStatus") var httpStatus: Int? = 0,
    @SerializedName("totalFriendsInvited") var totalFriendsInvited: Int? = 0,
    @SerializedName("totalReferralLimit") var totalReferralLimit: Int? = 0
)