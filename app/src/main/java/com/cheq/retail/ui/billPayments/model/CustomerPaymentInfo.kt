package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class CustomerPaymentInfo(
    val __v: Int,
    val amount: Int,
    val billId: Any,
    val createdAt: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val isEmandateTxn: Boolean,
    val paymentStatus: String,
    val pgMasterId: String,
    val pgOrderId: String,
    val pgStatus: String,
    val productId: String,
    val referenceId: String,
    val tenderMasterId: String,
    val transactionType: String,
    val txnId: String,
    val updatedAt: String,
    val userId: String
)