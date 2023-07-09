package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class UserSecret(
    val __v: Int,
    val createdAt: String,
    val eMandateAccountNumber: Any,
    val eMandateBankName: Any,
    val eMandateDate: Any,
    val eMandateExpires: Any,
    val eMandateIfscCode: Any,
    val eMandateUserName: Any,
    val emandateToken: Any,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val panCard: String,
    val rzpCustomerId: String,
    val updatedAt: String,
    val userId: String
)