package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.ui.dashboard.model.homedashboad.RewardLimitManager
import com.cheq.retail.ui.loans.model.loanv4.LoanV4
import com.cheq.retail.ui.login.model.Product
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.ui.login.model.loan.Loan
import com.cheq.retail.ui.login.modelv2.loanv1.LoanV2
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class DashBoardResponse(
    var products: ArrayList<ProductV2>,
    var loans: ArrayList<ProductV2>,
    var rewardPoints: Int,
    var totalDue: Double,
    var minDue: Double,
    var rewardLimitManager: RewardLimitManager,
    val apiMessage: String? = null,
    val blockedData: BlockData?,
    val status: Boolean? = null
)
{
    @Keep
    data class DataEntity(
        var _id: String,
        var isActive: Boolean,
        var isDeleted: Boolean,
        var productName: String,
        var rewardPoints: Int,
        var bankName: String?,
        var cardType: String,
        var cardHolderName: String,
        var cardBackgroundImage: String,
        var last4: String,
        var productNumber: String,
        var encCardNumber: String,
        var encExpiry: String,
        var cardCreationSource: String,
        var userId: String,
        var productMasterId: String,
        var updatedAt: String,
        var createdAt: String,
        var __v: Int,
        var beneId: String,
        var bankMasterId: String,
        var isAutoPayEnabled: Boolean,
        var totalDueEnabled: Boolean,
        var billDetail: BillDetailEntity,
        var bankLogo: String,
        var bankLogoWithName: String,
        var outerGridGradientColors : OuterGridGradientColors
    ): Serializable

    @Keep
    data class OuterGridGradientColors(
        var gOne : String,
        var gTwo : String,
        var gThree : String,
    ): Serializable
    @Keep
    data class InnerGridGradientColors(
        var gOne : String,
        var gTwo : String,
        var gThree : String,
    ):Serializable

    @Keep
    data class BillDetailEntity(
        var _id: String,
        var isActive: Boolean,
        var isDeleted: Boolean,
        var userId: String,
        var productId: String,
        var month: Int,
        var year: Int,
        var dueDate: String,
        var minDue: Double,
        var totalDue: Double,
        var amountPaid: Double,
        var isPaid: Boolean,
        var isFullPaid: Boolean,
        var updatedAt: String,
        var createdAt: String,
        var __v: Int,
    ): Serializable
}

fun DashBoardResponse?.isUserBlocked(): Boolean {
    return this != null && this.blockedData != null
}

