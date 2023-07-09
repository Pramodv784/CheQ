package com.cheq.retail.ui.loans.model.add_loan_response

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class Data(
    val __v: Int,
    val accountNumber: Any,
    val amountExactness: String,
    val bankMasterId: Any,
    val bankName: Any,
    val beneId: Any,
    val bill: BillDetailX,
    val billGeneratedDate: Any,
    val billRepeatCount: Int,
    val billerId: String,
    val billerName: String,
    val bureauCreationSource: Any,
    val cheqUserId: String,
    val createdAt: String,
    val creationSource: String,
    val creditCardType: Any,
    val encProductNumber: Any,
    val expiry: Any,
    val holderName: String,
    val insitutionId: String,
    val insitutionName: String?,
    val interimDate: Any,
    val isActive: Boolean,
    val isAutoPayEnabled: Boolean,
    val isDeleted: Boolean,
    val last4: String,
    val logo: String,
    val logoWithName: String,
    val productMasterId: Any,
    val productName: Any,
    val productNumber: String,
    val productStatus: String,
    val productType: String,
    val totalDueEnabled: Boolean,
    val updatedAt: String
): Serializable