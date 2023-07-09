package com.cheq.profile.domain.entities


/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
data class CheqSafeDetails(
    var status: Boolean,
    val message: String,
    val apiMessage: String,
    val emailLinkingStatus: EmailLinkingStatus
)