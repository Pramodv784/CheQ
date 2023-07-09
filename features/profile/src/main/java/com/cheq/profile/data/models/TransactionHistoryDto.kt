package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
data class TransactionHistoryDto(
    @SerializedName("txnLists")
    val transactionList: List<TransactionHistoryItemDto?>?,
) {

    data class TransactionHistoryItemDto(
        @SerializedName("cash_amount") val cashAmount: Int?,
        @SerializedName("payin_status") val payingStatus: String?,
        @SerializedName("payin_failure_reason") val payingFailureReason: Any?,
        @SerializedName("created_at") val createdAt: String?,
        @SerializedName("reward_txn_id") val rewardTransactionId: Any?,
        @SerializedName("updated_at") val updatedAt: String?,
        @SerializedName("product_id") val productId: String?,
        @SerializedName("id") val id: String?,
        @SerializedName("bankmaster_id") val bankMasterId: Any?,
        @SerializedName("payin_txn_id") val payingTransactionId: Any?,
        @SerializedName("payout_txn_id") val payoutTransactionId: Any?,
        @SerializedName("payment_method") val paymentMethod: Any?,
        @SerializedName("cheq_uni_txn_id") val cheqUniTransactionId: String?,
        @SerializedName("bill_id") val billId: String?,
        @SerializedName("tender") val tender: Any?,
        @SerializedName("bill_amount") val billAmount: Int?,
        @SerializedName("bill_total_amount") val billTotalAmount: Int?,
        @SerializedName("paid_together") val paidTogether: Boolean?,
        @SerializedName("created_by") val createdBy: String?,
        @SerializedName("chip_amount") val chipAmount: Int?,
        @SerializedName("reward_ref_id") val rewardRefId: Any?,
        @SerializedName("system_generated") val systemGenerated: Boolean?,
        @SerializedName("payout_failure_reason") val payoutFailureReason: Any?,
        @SerializedName("payout_status") val payoutStatus: Any?,
        @SerializedName("cheq_user_id") val cheqUserId: String?,
        @SerializedName("narration") val narration: String?,
        @SerializedName("updated_by") val updatedBy: String?,
        @SerializedName("comment") val comment: Any?,
        @SerializedName("status") val status: String?,
        @SerializedName("chip_used") val chipUsed: Int?
    )
}