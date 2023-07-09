package com.cheq.retail.ui.login.model.loan

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class BillDetail(
    val __v: Int,
    val id: String,
    val amount_paid: Double,
    val bill_due_amount: Double?,
    val pending_min_due_amount: Double?,
    val overpaid_amount: Double?,
    val createdAt: String,
    val due_date: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val isFullPaid: Boolean,
    val isPaid: Boolean,
    val min_due: Double?,
    val month: Int,
    val productId: String,
    val total_due: Double,
    val updated_at: String,
    val cheq_user_id: String,
    val product_id: String,
    val userId: String,
    val status: String,
    val created_by: String,
    val bill_status: String,
    val updated_by: String,
    val year: Int
): Serializable