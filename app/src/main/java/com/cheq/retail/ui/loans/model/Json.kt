package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import com.cheq.retail.ui.loans.model.loanv4.BlrResponseParams
import java.io.Serializable
@Keep
data class Json(
    val billerAdditionalInfoPayment: List<BillerAdditionalInfoPayment>,
    val billerAliasName: String,
    val billerDescription: String,
    val billerEffctvFrom: String,
    val billerEffctvTo: String,
    val billerId: String,
    val billerLogoURL: String,
    val billerMode: String,
    val billerName: String,
    val billerOwnerShp: String,
    val billerPlanResponseParams: String,
    val billerResponseType: String,
    val billerTempDeactivationEnd: String,
    val billerTempDeactivationStart: String,
    val billerTimeOut: Int,
    val blrAdditionalInfo: List<BlrAdditionalInfo>,
    val blrResponseParams: BlrResponseParams,
    val category: String,
    val customerParamGroups: String,
    val customerParams: List<CustomerParam >,
    val fetchOption: String,
    val flowType: String,
    val interchangeFee: List<InterchangeFee >,
    val interchangeFeeConf: List<InterchangeFeeConf >,
    val isAdhoc: Boolean,
    val parentBiller: Boolean,
    val parentBillerId: Any,
    val paymentAmountExactness: String,
    val paymentChannelsAllowed: List<PaymentChannelsAllowedX>,
    val paymentModesAllowed: List<PaymentModesAllowedX>,
  //  val planAdditionalInfo: List<String>,
    val planMdmRequirement: String,
    val region: String,
    val regionCode: String,
    val state: String,
    val status: String,
    val supportBillValidation: String,
    val supportDeemed: Boolean,
    val supportPendingStatus: Boolean
): Serializable