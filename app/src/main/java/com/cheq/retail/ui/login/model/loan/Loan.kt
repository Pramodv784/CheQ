package com.cheq.retail.ui.login.model.loan

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class Loan(

    val id: String?,
    val accountNumber: String?,
    val bankMasterId: String?,
    val bankName: String?,
    val beneId: String?,
    val bill: BillDetail?,
    val billGeneratedDate: String?,
    val billRepeatCount: Int?,
    val billerId: String?,
    val billerName: String?,
    val bureauCreationSource: String?,
    val createdAt: String?,
    val creationSource: String?,
    val creditCardType: String?,
    val encProductNumber: String?,
    val expiry: String?,
    val holderName: String?,
    val institution_master_id: String?,
    val isActive: Boolean,
    val isAutoPayEnabled: Boolean,
    val isDeleted: Boolean,
    val json: Json?,
    val last4: String?,
    val logo: String?,
    val logoWithName: String?,
    val outerGridGradientColors: OuterGridGradientColors?,
    val productMasterId: Any?,
    val productName: Any?,
    val productNumber: String?,
    val productStatus: String?,
    val productType: String?,
    val totalDueEnabled: Boolean,
    val updatedAt: String?,
    val userId: String?
): Serializable