package com.cheq.retail.ui.login.modelv2.loanv1

import androidx.annotation.Keep
import com.cheq.retail.ui.loans.model.PaymentChannelsAllowedX
import com.cheq.retail.ui.loans.model.PaymentModesAllowedX
@Keep
data class Json(
    val billerAdditionalInfoPayment: List<Any>,
    val billerAliasName: String,
    val billerDescription: String,
    val billerEffctvFrom: String,
    val billerEffctvTo: String,
    val billerId: String,
    val billerLogoURL: Any,
    val billerMode: String,
    val billerName: String,
    val billerOwnerShp: String,
    val billerResponseType: String,
    val billerTempDeactivationEnd: Any,
    val billerTempDeactivationStart: Any,
    val billerTimeOut: Any,
    val blrAdditionalInfo: List<BlrAdditionalInfo>,
    val blrResponseParams: BlrResponseParamsX,
    val category: String,
    val customerParamGroups: Any,
    val customerParams: List<CustomerParam>,
    val fetchOption: String,
    val flowType: String,
    val interchangeFee: List<Any>,
    val interchangeFeeConf: List<Any>,
    val isAdhoc: Boolean,
    val parentBiller: Boolean,
    val parentBillerId: Any,
    val paymentAmountExactness: String,
    val paymentChannelsAllowed: List<PaymentChannelsAllowedX>,
    val paymentModesAllowed: List<PaymentModesAllowedX>,
    val planAdditionalInfo: List<Any>,
    val planMdmRequirement: String,
    val region: String,
    val regionCode: String,
    val state: String,
    val status: String,
    val supportBillValidation: String,
    val supportDeemed: Boolean,
    val supportPendingStatus: Boolean
)