package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class NewTranX(
    val payout_ect_narration_v2: String?,
    val txn: Txn?,
    val UPI_Payment_status: String?,
    val product_type: String?,
    val payout_mode: String?,
    val user_err_msg: String?,
    val cheq_error_code: String?,
    val apiMessage: String?,
    val status: Boolean?,
)