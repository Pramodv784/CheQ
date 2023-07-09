package com.cheq.retail.ui.login.model

import androidx.annotation.Keep
import com.cheq.retail.ui.login.modelv2.productv1.BankMasterRecord
import com.cheq.retail.ui.main.model.DashBoardResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
class Product  (
    val isActive: Boolean?,
    val isDeleted: Boolean?,
    val productType: String?,
    val productName: String?,
    val bankName: String?,
    val holderName: String?,
    val insitutionId: String?,
    val creditCardType: String?,
    val creationSource: String?,
    val bureauCreationSource: String?,
    val last4: String?,
    val product_number: String?,
    val encProductNumber: String?,
    val expiry: String?,
    val userId: String?,
    val bankMasterId: String?,
    val productMasterId: String?,
    val isAutoPayEnabled: Boolean?,
    val totalDueEnabled: Boolean?,
    val _id: String?,
    val logo: String?,
    val logoWithName: String?,
    val institutionName: String?,
    @SerializedName("instrumentMaster") val bankMasterRecord: BankMasterRecord?,
    var billDetail: DashBoardResponse.BillDetailEntity?,
    var outerGridGradientColors : DashBoardResponse.OuterGridGradientColors?,
    var innerGridGradientColors : DashBoardResponse.InnerGridGradientColors?,
): Serializable