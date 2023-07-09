package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class Product(
    val __v: Int,
    val accountNumber: Any,
    val bankMasterId: Any,
    val bankName: Any,
    val beneId: Any,
    val billGeneratedDate: Any,
    val billRepeatCount: Int,
    val bureauCreationSource: Any,
    val createdAt: String,
    val creationSource: String,
    val creditCardType: Any,
    val encProductNumber: Any,
    val expiry: Any,
    val holderName: String,
    val insitutionId: String,
    val interimDate: Any,
    val isActive: Boolean,
    val isAutoPayEnabled: Boolean,
    val isDeleted: Boolean,
    val last4: String,
    val productMasterId: Any,
    val productName: Any,
    val productNumber: String,
    val productStatus: String,
    val productType: String,
    val totalDueEnabled: Boolean,
    val updatedAt: String,
    val userId: String
)