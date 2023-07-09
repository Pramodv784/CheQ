package com.cheq.profile.domain.entities


/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
data class ReferralUrl(
    var referralUrl: String,
    val upsertedId: Int,
    val upsertedCount: Int,
    val acknowledged: Boolean,
    val modifiedCount: Int,
    val matchedCount: Int
)
