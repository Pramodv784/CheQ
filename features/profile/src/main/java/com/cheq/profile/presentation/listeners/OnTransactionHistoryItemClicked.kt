package com.cheq.profile.presentation.listeners

import com.cheq.profile.domain.entities.TransactionHistory

interface OnTransactionHistoryItemClicked {
    fun onItemClicked(item: TransactionHistory.TransactionHistoryItem)
}