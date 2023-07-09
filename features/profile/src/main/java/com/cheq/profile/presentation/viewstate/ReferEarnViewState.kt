package com.cheq.profile.presentation.viewstate

import com.cheq.profile.domain.entities.ReferralStatic

/**
 * Created by Akash Khatkale on 1st June, 2023.
 * akash.k@cheq.one
 */
data class ReferEarnViewState<T>(
    val isLoading: Boolean = true,
    val data: ReferralStatic? = null,
    val error: Throwable? = null
)