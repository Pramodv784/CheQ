package com.cheq.profile.domain.entities



/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
data class ReferralEarned(
    var show: Boolean,
    var totalRewardsEarned: Int,
    var httpStatus: Int,
    var totalFriendsInvited: Int,
    var totalReferralLimit: Int
)