package com.cheq.retail.ui.login.model

import androidx.annotation.Keep
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.ui.dashboard.model.homedashboad.RewardLimitManager
import com.cheq.retail.ui.login.model.loan.Loan
import com.cheq.retail.ui.main.model.DashBoardResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class UpdateProfileResponse(
    var rewardLimitManager: RewardLimitManager,
    var userdata: DataEntity,
    val message: String,
    val status: Boolean?=false,
    val apiMessage: String?,
    val blockedData: BlockData?,
    val creditResponse: CreditResponse
) {
    @Keep
    data class DataEntity(
        val mobile: String,
        val customerStatus: String,
        var __v: Int,

        )
    @Keep
    data class CreditResponse(
        val status: Boolean?,
        val pan: String,
        val Date_Of_Birth_Applicant: String,
        val processStage: String,
        val bureauContext: String,
        val message: String,
        val creditScore: Int?,
        val httpStatus: Int,
        val bureauReport: BureauReport?,
        var cardsToBeProcessed: ArrayList<CardsToBeProcessed>?,
        var loans: ArrayList<Loan>?,
        var products: List<Product>?
    ) : Serializable
    @Keep
    data class BureauReport(
        val creditScoreStateTemplate: String?,
        val creditScoreStateValue: Int?,
        val creditScoreStateValueTop: Boolean?,
    ) : Serializable {

        val getDescriptionAsHtml: String?
            get() {
                if (creditScoreStateValueTop != true) return null


                if (creditScoreStateTemplate == null || creditScoreStateValue == null) return null

                return creditScoreStateTemplate.replace(
                    Regex("top.*\\{\\{.*\\}\\}"),
                    "<span style=\"color: #00C197;\">top $creditScoreStateValue%</span>"
                )
            }

    }
    @Keep
    data class CardsToBeProcessed(

        var productName: String? = null,
        var cardHolderName: String? = null,
        var rewardPoints: Int? = null,
        var bankName: String? = null,
        var cardType: String? = null,
        var cardBackgroundImage: String? = null,
        var last4: String? = null,
        var cbCardNumber: String? = null,
        var encCardNumber: String? = null,
        var encExpiry: String? = null,
        var cardCreationSource: String? = null,
        var beneId: String? = null,
        var userId: String? = null,
        var bankMasterId: String? = null,
        var productMasterId: String? = null,
        var isAutoPayEnabled: Boolean? = null,
        var totalDueEnabled: Boolean? = null

    ) : Serializable

    @Keep
    data class HolderName(
        var value: String
    ) : Serializable
    @Keep
    data class AccountNumber(
        var value: String
    ) : Serializable

}
fun UpdateProfileResponse?.isUserBlocked(): Boolean {
    return this?.blockedData != null
}
