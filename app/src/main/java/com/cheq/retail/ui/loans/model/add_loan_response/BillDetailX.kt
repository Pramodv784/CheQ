package com.cheq.retail.ui.loans.model.add_loan_response

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class BillDetailX(
    val __v: Int,
    val amountPaid: Int,
    val cheqUserId: String,
    val createdAt: String,
    val dueDate: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val isFullPaid: Boolean,
    val isPaid: Boolean,
    val minDue: Any,
    val month: Int,
    val productId: String,
    val totalDue: Double?,
    val updatedAt: String,
    val year: Int
): Serializable