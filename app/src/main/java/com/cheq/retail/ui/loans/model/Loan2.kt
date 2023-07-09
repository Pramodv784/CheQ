package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep

import com.cheq.retail.ui.loans.model.loanv4.BlrResponseParams
import com.cheq.retail.ui.login.model.loan.OuterGridGradientColors
import com.google.gson.annotations.SerializedName

import java.io.Serializable
@Keep
data class Loan2(
    val __v: Int,
    @SerializedName("id")
    val id: String,
    val alias: List<String>,
    // val billerAdditionalInfoPayment: List<BillerAdditionalInfoPayment>,
    val billerAliasName: String,
    val billerDescription: String,
    val billerEffctvFrom: String,
    val billerEffctvTo: String,
    val billerId: String,
    val billerMode: String,
    val billerName: String,
    val billerOwnerShp: String,
    //  val billerPlanResponseParams: Any,
    val billerResponseType: String,
    //  val billerTempDeactivationEnd: Any,
    //   val billerTempDeactivationStart: Any,
    val billerTimeOut: String,
    //  val blrAdditionalInfo: List<BlrAdditionalInfo>,
    val blrResponseParams: BlrResponseParams,
    val category: String,
    val createdAt: String,
    val customerParamGroups: String,
    val customerParams: ArrayList<CustomerParam>,
    val fetchOption: String,
    val flowType: String,
//    val interchangeFee: List<InterchangeFee>,
//    val interchangeFeeConf: List<InterchangeFeeConf>,
    val isActive: Boolean,
    val isAdhoc: Boolean,
    val isBNPL: Boolean,
    val isBnpl: Boolean,
    val isDeleted: Boolean,
    // val isExperianLender: Any,
    // val json: Json?,
    val logo: String?,
    val logoWithName: Any,
    //  val originalLenderName: Any,
    val parentBiller: String,
    // val parentBillerId: Any,
    val paymentAmountExactness: String,
    //  val paymentChannelsAllowed: List<PaymentChannelsAllowedX>,
    // val paymentModesAllowed: List<PaymentModesAllowedX>,
    //    val planAdditionalInfo: List<Any>,
    val outerGridGradientColors: OuterGridGradientColors?,
    val planMdmRequirement: String,
    val region: String,
    val regionCode: String,
    val state: String,
    val status: String,
    val supportBillValidation: String,
    val supportDeemed: Boolean,
    val supportPendingStatus: Boolean,
    val uiConfig: UiConfig,
    val updatedAt: String
) : Serializable

class UiConfig(val cardColor: String): Serializable