package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class Txn(
    val amount: Int,
    val bankmaster_id: String,
    val bill_split: Int,
    val bill_status: String,
    val txn_status: String,
    val cheq_user_id: String,
    val created_at: String,
    val created_by: String,
    val id: String,
    val payin_time: Any,
    val payment_split: String,
    val payout_time: Any,
    val product_id: String,
    val status: String,
    val updated_at: String,
    val updated_by: String,
    val payin_status : String
)