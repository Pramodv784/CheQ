package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep
import com.cheq.retail.ui.login.modelv2.productv1.UI
import com.google.gson.annotations.SerializedName



@Keep
data class GetProductByIdResponse(

    @field:SerializedName("products") val products: Products? = null,@field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("user_err_msg")
    val userErrorMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
)

@Keep
data class OuterGridGradientColors(

    @field:SerializedName("gThree") val gThree: String? = null,

    @field:SerializedName("gTwo") val gTwo: String? = null,

    @field:SerializedName("gOne") val gOne: String? = null
)

@Keep
data class BankMasterRecord(

    @field:SerializedName("bureauBankName") val bureauBankName: String? = null,

    @field:SerializedName("innerGridGradientColors") val innerGridGradientColors: InnerGridGradientColors? = null,

    @field:SerializedName("logoWithName") val logoWithName: String? = null,

    @field:SerializedName("outerGridGradientColors") val outerGridGradientColors: OuterGridGradientColors? = null,

    @field:SerializedName("bankName") val bankName: String? = null,

    @field:SerializedName("isActive") val isActive: Boolean? = null,

    @field:SerializedName("IFSC_Prefix") val iFSCPrefix: String? = null,

    @field:SerializedName("createdAt") val createdAt: String? = null,

    @field:SerializedName("isDeleted") val isDeleted: Boolean? = null,

    @field:SerializedName("ocrBankName") val ocrBankName: String? = null,

    @field:SerializedName("__v") val V: Int? = null,

    @field:SerializedName("originalBankName") val originalBankName: String? = null,

    @field:SerializedName("logo") val logo: String? = null,

    @field:SerializedName("rank") val rank: Int? = null,

    val ui_config: UI,

    @field:SerializedName("alias") val alias: List<String?>? = null,

    @field:SerializedName("_id") val id: String? = null,

    @field:SerializedName("updatedAt") val updatedAt: String? = null
)

@Keep
data class Products(

    @field:SerializedName("cb_created_from") val cbCreatedFrom: Any? = null,

    @field:SerializedName("last4") val last4: String? = null,

    @field:SerializedName("ifsc_prefix") val ifscPrefix: Any? = null,

    @field:SerializedName("instrumentMaster") val bankMasterRecord: BankMasterRecord? = null,

    @field:SerializedName("institution_master_id") val institutionMasterId: String? = null,

    @field:SerializedName("created_at") val createdAt: String? = null,

    @field:SerializedName("product_status") val productStatus: String? = null,

    @field:SerializedName("bankName") val bankName: String? = null,

    @field:SerializedName("interim_date") val interimDate: Any? = null,

    @field:SerializedName("netwok_token_payout_support") val netwokTokenPayoutSupport: Boolean? = null,

    @field:SerializedName("product_number") val productNumber: String? = null,

    @field:SerializedName("iin") val iin: String? = null,

    @field:SerializedName("updated_at") val updatedAt: String? = null,

    @field:SerializedName("is_total_due_enabled") val isTotalDueEnabled: Boolean? = null,

    @field:SerializedName("logo") val logo: String? = null,

    @field:SerializedName("tokenization_date") val tokenizationDate: Any? = null,

    @field:SerializedName("id") val id: String? = null,

    @field:SerializedName("bankmaster_id") val bankmasterId: String? = null,

    @field:SerializedName("bill_repeat_count") val billRepeatCount: Int? = null,

    @field:SerializedName("innerGridGradientColors") val innerGridGradientColors: InnerGridGradientColors? = null,

    @field:SerializedName("logoWithName") val logoWithName: String? = null,

    @field:SerializedName("outerGridGradientColors") val outerGridGradientColors: OuterGridGradientColors? = null,

    @field:SerializedName("is_tokenized") val isTokenized: String? = null,

    @field:SerializedName("bill_generated_date") val billGeneratedDate: Any? = null,

    @field:SerializedName("created_by") val createdBy: String? = null,

    @field:SerializedName("is_enabled_for_autopay") val isEnabledForAutopay: Boolean? = null,

    @field:SerializedName("beneficiary_id") val beneficiaryId: Any? = null,

    @field:SerializedName("product_type") val productType: String? = null,

    @field:SerializedName("cheq_user_id") val cheqUserId: String? = null,

    @field:SerializedName("updated_by") val updatedBy: String? = null,

    @field:SerializedName("customer_name") val customerName: String? = null,

    @field:SerializedName("created_from") val createdFrom: String? = null,

    @field:SerializedName("status") val status: String? = null,

    val ui_config: UI
)

@Keep
data class InnerGridGradientColors(

    @field:SerializedName("gThree") val gThree: String? = null,

    @field:SerializedName("gTwo") val gTwo: String? = null,

    @field:SerializedName("gOne") val gOne: String? = null
)
