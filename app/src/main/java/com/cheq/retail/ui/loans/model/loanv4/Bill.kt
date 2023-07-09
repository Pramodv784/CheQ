package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class Bill(
    val amount_paid: Double=0.0,
    val bill_status: String,
    val cheq_user_id: String,
    val created_at: String,
    val created_by: String,
    val due_date: String,
    val id: String,
    val min_due: Double,
    val month: Int,
    val product_id: String,
    val status: String,
    val total_due: Double=0.0,
    val updated_at: String,
    val updated_by: String,
    val year: Int
)