package com.cheq.profile.presentation.viewstate

import com.cheq.profile.domain.entities.ReferralHistory

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
data class ReferralHistoryViewState(
    val isLoading: Boolean = true,
    val data: ReferralHistory? = null,
    val error: Throwable? = null
)
