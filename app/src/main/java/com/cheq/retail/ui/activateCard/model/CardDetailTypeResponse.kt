package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep
import com.cheq.retail.ui.login.modelv2.productv1.UI
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class CardDetailTypeResponse(

    @field:SerializedName("bank")
    val bank: Bank? = null,

    @field:SerializedName("httpStatus")
    val httpStatus: Int? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("user_err_msg")
    val userErrorMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("iin")
    val iin: Iin? = null,

    @field:SerializedName("verification_supported")
    val verification_supported : Boolean? = null
) : Serializable

@Keep
data class DataById(

    @field:SerializedName("bureauBankName")
    val bureauBankName: String? = null,

    @field:SerializedName("innerGridGradientColors")
    val innerGridGradientColors: InnerGridGradientColors? = null,

    @field:SerializedName("outerGridGradientColors")
    val outerGridGradientColors: OuterGridGradientColors? = null,

    @field:SerializedName("bankName")
    val bankName: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("IFSC_Prefix")
    val iFSCPrefix: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("perc")
    val perc: Double? = null,

    @field:SerializedName("isDeleted")
    val isDeleted: Boolean? = null,

    @field:SerializedName("ocrBankName")
    val ocrBankName: Any? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("originalBankName")
    val originalBankName: String? = null,

    @field:SerializedName("logo")
    val logo: Any? = null,

    @field:SerializedName("rank")
    val rank: Any? = null,

    val ui_config: UI,

    @field:SerializedName("alias")
    val alias: Any? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
) : Serializable

@Keep
data class Bank(

    @field:SerializedName("bureauBankName")
    val bureauBankName: String? = null,

    @field:SerializedName("innerGridGradientColors")
    val innerGridGradientColors: InnerGridGradientColors? = null,

    @field:SerializedName("outerGridGradientColors")
    val outerGridGradientColors: OuterGridGradientColors? = null,

    @field:SerializedName("bankName")
    val bankName: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("IFSC_Prefix")
    val iFSCPrefix: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("perc")
    val perc: Double? = null,

    @field:SerializedName("isDeleted")
    val isDeleted: Boolean? = null,

    @field:SerializedName("ocrBankName")
    val ocrBankName: Any? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("originalBankName")
    val originalBankName: String? = null,

    @field:SerializedName("logo")
    val logo: Any? = null,

    @field:SerializedName("logoWithName")
    val logoWithName: Any? = null,

    @field:SerializedName("rank")
    val rank: Any? = null,

    val ui_config: UI,


    @field:SerializedName("alias")
    val alias: Any? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
) : Serializable

@Keep
data class Iin(

    @field:SerializedName("bankMasterId")
    val bankMasterId: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: Double? = null,

    @field:SerializedName("data")
    val data: GetProductByIdResponse? = null,

    @field:SerializedName("issuer_code")
    val issuerCode: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("entity")
    val entity: String? = null,

    @field:SerializedName("iin")
    val iin: String? = null,

    @field:SerializedName("network")
    val network: String? = null,

    @field:SerializedName("issuer_name")
    val issuerName: String? = null
) : Serializable
