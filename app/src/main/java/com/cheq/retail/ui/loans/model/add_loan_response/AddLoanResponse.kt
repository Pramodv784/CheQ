package com.cheq.retail.ui.loans.model.add_loan_response

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class AddLoanResponse(
    val __v: Int,
    val accountNumber: AccountNumber,
    val amountExactness: String,
    val bankMasterId: Any,
    val bankName: Any,
    val beneId: Any,
    val billDetail: BillDetail,
    val billerId: String,
    val billerName: String,
    val bureauCreationSource: Any,
    val createdAt: String,
    val creationSource: String,
    val creditCardType: Any,
    val encProductNumber: Any,
    val expiry: Any,
    val holderName: String,
    val insitutionId: String,
    val isActive: Boolean,
    val isAutoPayEnabled: Boolean,
    val isDeleted: Boolean,
    val last4: String,
    val logo: String,
    val logoWithName: String,
    val productMasterId: Any,
    val productName: Any,
    val productNumber: String,
    val productType: String,
    val totalDueEnabled: Boolean,
    val updatedAt: String,
    val userId: String
) : Serializable
@Keep
data class AccountNumber(
    val value: String,
) : Serializable