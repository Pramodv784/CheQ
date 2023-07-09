package com.cheq.retail.ui.accountSettings.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class TxnHistoryResponse(

    @field:SerializedName("data")
    val data: DataHistory? = null,

    @field:SerializedName("requestId")
    val requestId: String? = null,

    @field:SerializedName("httpStatus")
    val httpStatus: Int? = null
)

@Keep
data class DataHistory(

    @field:SerializedName("txnLists")
    val txnLists: List<TxnListsItem?>? = null
)

@Keep
data class TxnListsItem(

    @field:SerializedName("cash_amount")
    val cashAmount: Int? = null,

    @field:SerializedName("payin_status")
    val payinStatus: String? = null,

    @field:SerializedName("payin_failure_reason")
    val payinFailureReason: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("reward_txn_id")
    val rewardTxnId: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("product_id")
    val productId: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("bankmaster_id")
    val bankmasterId: Any? = null,

    @field:SerializedName("payin_txn_id")
    val payinTxnId: Any? = null,

    @field:SerializedName("payout_txn_id")
    val payoutTxnId: Any? = null,

    @field:SerializedName("payment_method")
    val paymentMethod: Any? = null,

    @field:SerializedName("cheq_uni_txn_id")
    val cheqUniTxnId: String? = null,

    @field:SerializedName("bill_id")
    val billId: String? = null,

    @field:SerializedName("tender")
    val tender: Any? = null,

    @field:SerializedName("bill_amount")
    val billAmount: Int? = null,

    @field:SerializedName("paid_together")
    val paidTogether: Boolean? = null,

    @field:SerializedName("created_by")
    val createdBy: String? = null,

    @field:SerializedName("chip_amount")
    val chipAmount: Int? = null,

    @field:SerializedName("reward_ref_id")
    val rewardRefId: Any? = null,

    @field:SerializedName("system_generated")
    val systemGenerated: Boolean? = null,

    @field:SerializedName("payout_failure_reason")
    val payoutFailureReason: Any? = null,

    @field:SerializedName("payout_status")
    val payoutStatus: Any? = null,

    @field:SerializedName("cheq_user_id")
    val cheqUserId: String? = null,

    @field:SerializedName("narration")
    val narration: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: String? = null,

    @field:SerializedName("comment")
    val comment: Any? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("chip_used")
    val chipUsed: Int? = null,

    @field:SerializedName("bill_total_amount")
    val billTotalAmount: Int? = null


)
