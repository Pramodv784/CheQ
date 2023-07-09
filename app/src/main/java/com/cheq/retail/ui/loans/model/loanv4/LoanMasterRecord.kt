package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class LoanMasterRecord(
    val __v: Int,
    val alias: List<Any>,
    val billerAliasName: String,
    val billerDescription: String,
    val billerEffctvFrom: String,
    val billerEffctvTo: String,
    val billerId: String,
    val billerMode: String,
    val billerName: String,
    val billerOwnerShp: String,
    val billerResponseType: String,
    val billerTempDeactivationEnd: Any,
    val billerTempDeactivationStart: Any,
    val billerTimeOut: Any,
    val blrAdditionalInfo: List<BlrAdditionalInfo>,
    val blrResponseParams: BlrResponseParams,
    val category: String,
    val createdAt: String,
    val customerParamGroups: Any,
    val customerParams: List<CustomerParam>,
    val fetchOption: String,
    val flowType: String,
    val fullResponse: Any,
    val innerGridGradientColors: Any,
    val interchangeFee: List<InterchangeFee>,
    val interchangeFeeConf: List<InterchangeFeeConf>,
    val isActive: Boolean,
    val isAdhoc: Boolean,
    val isBNPL: Boolean,
    val isBnpl: Boolean,
    val isDeleted: Boolean,
    val isExperianLender: Boolean,
    val json: Json,
    val logo: String?,
    val logoWithName: Any,
    val originalLenderName: Any,
    val outerGridGradientColors: Any,
    val parentBiller: String,
    val parentBillerId: Any,
    val paymentAmountExactness: String,
    val paymentChannelsAllowed: List<PaymentChannelsAllowedX>,
    val paymentModesAllowed: List<PaymentModesAllowedX>,
    val planAdditionalInfo: List<Any>,
    val planMdmRequirement: String,
    val region: String,
    val regionCode: String,
    val sequence: Int,
    val state: String,
    val status: String,
    val supportBillValidation: String,
    val supportDeemed: Boolean,
    val supportPendingStatus: Boolean,
    val updatedAt: String
)