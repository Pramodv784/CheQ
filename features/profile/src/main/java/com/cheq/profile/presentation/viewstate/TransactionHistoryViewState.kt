package com.cheq.profile.presentation.viewstate


/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */
data class TransactionHistoryViewState<T>(
    val isLoading: Boolean = true,
    val data: T? = null,
    val error: Throwable? = null
)
