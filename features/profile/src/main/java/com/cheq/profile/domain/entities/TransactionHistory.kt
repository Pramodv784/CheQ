package com.cheq.profile.domain.entities

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
data class TransactionHistory(
    val transactionList: List<TransactionHistoryItem>
) {
    data class TransactionHistoryItem(
        val cashAmount: Int,
        val payingStatus: String,
        val payingFailureReason: Any?,
        val createdAt: String,
        val rewardTransactionId: Any?,
        val updatedAt: String,
        val productId: String,
        val id: String,
        val bankMasterId: Any?,
        val payingTransactionId: Any?,
        val payoutTransactionId: Any?,
        val paymentMethod: Any?,
        val cheqUniTransactionId: String,
        val billId: String,
        val tender: Any?,
        val billAmount: Int,
        val billTotalAmount: Int,
        val paidTogether: Boolean,
        val createdBy: String,
        val chipAmount: Int,
        val rewardRefId: Any?,
        val systemGenerated: Boolean,
        val payoutFailureReason: Any?,
        val payoutStatus: Any?,
        val cheqUserId: String,
        val narration: String,
        val updatedBy: String,
        val comment: Any?,
        val status: String,
        val chipUsed: Int,
    )
}