package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.cheq.retail.ui.login.modelv2.productv1.UI
import com.google.gson.annotations.SerializedName

@Keep
data class PaymentOptionsResponse(
    val netBanking: NetBanking,
    val savedCards: List<SavedCardsItem>,
    val message: String? = null,
    val rewardPoints: RewardPointsItem?,
    val preferredMethods: List<PreferredMethodsItem>,
    val status: Boolean? = null
) {
    @Keep
    data class BanksItem(

        @field:SerializedName("bureauBankName")
        val bureauBankName: String? = null,

        @field:SerializedName("logoWithName")
        val logoWithName: String? = null,

        @field:SerializedName("bankName")
        val bankName: String? = null,

        @field:SerializedName("isActive")
        val isActive: Boolean? = null,

        @field:SerializedName("IFSC_Prefix")
        val iFSCPrefix: String? = null,

        @field:SerializedName("ocrBankName")
        val ocrBankName: Any? = null,

        @field:SerializedName("originalBankName")
        val originalBankName: String? = null,

        @field:SerializedName("logo")
        val logo: String? = null,

        @field:SerializedName("rank")
        val rank: Any? = null,

        @field:SerializedName("id")
        val _id: String? = null,

        @field:SerializedName("updatedAt")
        val updatedAt: String? = null
    )
    @Keep
    data class Top6Item(

        val bureauBankName: String? = null,
        val logoWithName: String? = null,
        val bankName: String? = null,
        val isActive: Boolean? = null,
        val IFSC_Prefix: String? = null,
        val createdAt: String? = null,
        val isDeleted: Boolean? = null,
        val ocrBankName: String? = null,
        val __v: Int? = null,
        val originalBankName: String? = null,
        val logo: String? = null,
        val rank: Int? = null,
        @field:SerializedName("id")
        val _id: String? = null,
        val updatedAt: String? = null
    )
    @Keep
    data class NetBanking(
        @SerializedName("instrumentMaster")
        val banks: List<BanksItem>,
        val top6: List<Top6Item>
    )
    @Keep
    data class CardToken(

        @field:SerializedName("compliant_with_tokenization_guidelines")
        val compliantWithTokenizationGuidelines: Boolean? = null,

        @field:SerializedName("isActive")
        val isActive: Boolean? = null,

        @field:SerializedName("cardToken")
        val cardToken: String? = null,

        @field:SerializedName("cxName")
        val cxName: String? = null,

        @field:SerializedName("cardIssuer")
        val cardIssuer: String? = null,

        @field:SerializedName("createdAt")
        val createdAt: String? = null,

        @field:SerializedName("expiredAt")
        val expiredAt: String? = null,

        @field:SerializedName("bank")
        val bank: Any? = null,

        @field:SerializedName("isDeleted")
        val isDeleted: Boolean? = null,

        @field:SerializedName("dcc_enabled")
        val dccEnabled: Boolean? = null,

        @field:SerializedName("BankMaster")
        val BankMaster: BankMaster? = null,

        @field:SerializedName("__v")
        val V: Int? = null,

        @field:SerializedName("cardlast4")
        val cardlast4: String? = null,

        @field:SerializedName("cardEntity")
        val cardEntity: String? = null,

        @field:SerializedName("updatedAt")
        val updatedAt: String? = null,

        @field:SerializedName("bankMasterId")
        val bankMasterId: String? = null,

        @field:SerializedName("auth_type")
        val authType: Any? = null,

        @field:SerializedName("wallet")
        val wallet: Any? = null,

        @field:SerializedName("method")
        val method: String? = null,

        @field:SerializedName("cardTokenTd")
        val cardTokenTd: String? = null,

        @field:SerializedName("cardType")
        val cardType: String? = null,

        @field:SerializedName("userId")
        val userId: String? = null,

        @field:SerializedName("cardNetwork")
        val cardNetwork: String? = null,

        @field:SerializedName("cardSub_type")
        val cardSubType: String? = null,

        @field:SerializedName("usedAt")
        val usedAt: String? = null,

        @field:SerializedName("_id")
        val id: String? = null
    )
    @Keep
    data class SavedCardsItem(

        @field:SerializedName("compliant_with_tokenization_guidelines")
        val compliantWithTokenizationGuidelines: Boolean? = null,

        @field:SerializedName("isActive")
        val isActive: Boolean? = null,

        @field:SerializedName("cardToken")
        val cardToken: String? = null,

        @field:SerializedName("cxName")
        val cxName: String? = null,

        @field:SerializedName("cardIssuer")
        val cardIssuer: String? = null,

        @field:SerializedName("createdAt")
        val createdAt: String? = null,

        @field:SerializedName("expiredAt")
        val expiredAt: String? = null,

        @field:SerializedName("bank")
        val bank: Any? = null,

        @field:SerializedName("isDeleted")
        val isDeleted: Boolean? = null,

        @field:SerializedName("dcc_enabled")
        val dccEnabled: Boolean? = null,

        @field:SerializedName("BankMaster")
        val bankMaster: BankMaster? = null,

        @field:SerializedName("__v")
        val V: Int? = null,

        @field:SerializedName("cardlast4")
        val cardlast4: String? = null,

        @field:SerializedName("cardEntity")
        val cardEntity: String? = null,

        @field:SerializedName("updatedAt")
        val updatedAt: String? = null,

        @field:SerializedName("bankMasterId")
        val bankMasterId: String? = null,

        @field:SerializedName("auth_type")
        val authType: Any? = null,

        @field:SerializedName("wallet")
        val wallet: Any? = null,

        @field:SerializedName("method")
        val method: String? = null,

        @field:SerializedName("cardTokenTd")
        val cardTokenTd: String? = null,

        @field:SerializedName("cardType")
        val cardType: String? = null,

        @field:SerializedName("userId")
        val userId: String? = null,

        @field:SerializedName("cardNetwork")
        val cardNetwork: String? = null,

        @field:SerializedName("cardSub_type")
        val cardSubType: String? = null,

        @field:SerializedName("usedAt")
        val usedAt: String? = null,

        @field:SerializedName("_id")
        val id: String? = null
    )
    @Keep
    data class BankMaster(
        val bureauBankName: String? = null,
        val logoWithName: String? = null,
        val bankName: String? = null,
        val isActive: Boolean? = null,
        val IFSC_Prefix: String? = null,
        val createdAt: String? = null,
        val isDeleted: Boolean? = null,
        val ocrBankName: Any? = null,
        val __v: Int? = null,
        val originalBankName: String? = null,
        val logo: String? = null,
        val rank: Any? = null,
        var ui_config: UI? = null,
        val id: String? = null,
        val updatedAt: String? = null
    )
    @Keep
    data class RewardPointsItem(
        val availableRewardPoints: Int? = null,
        val _id: Int? = null,
        val usedRewardPoints: Int? = null,
        val rewardToRupeesConversion: Double? = null,
        val getRewardPoints: Int? = null

    )
    @Keep
    data class PreferredMethodsItem(
        val identifier: String? = null,
        val BankMaster: BankMaster? = null,
        val count: Int? = null,
        val paymentMethod: String? = null,
        val tnxDetailId: String? = null,
        val _id: String? = null,
        val cardToken: CardToken? = null,
        var isClicked: Boolean = false
    )
}