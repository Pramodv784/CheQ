package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DebitCardResponse(

	@field:SerializedName("bank")
	val bank: Bank? = null,

	@field:SerializedName("response")
	val response: Any? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user_err_msg")
	val userErrorMessage: String? = null,

	@field:SerializedName("error")
	val error: Any? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("iin")
	val iin: Iin? = null
)
@Keep
data class Emi(

	@field:SerializedName("available")
	val available: Boolean? = null
)
@Keep
data class Iin(

	@field:SerializedName("emi")
	val emi: Emi? = null,

	@field:SerializedName("recurring")
	val recurring: Recurring? = null,

	@field:SerializedName("issuer_code")
	val issuerCode: String? = null,

	@field:SerializedName("authentication_types")
	val authenticationTypes: List<AuthenticationTypesItem?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("iin")
	val iin: String? = null,

	@field:SerializedName("network")
	val network: String? = null,

	@field:SerializedName("issuer_name")
	val issuerName: String? = null,

	@field:SerializedName("sub_type")
	val subType: String? = null,

	@field:SerializedName("card_iin")
	val cardIin: String? = null,

	@field:SerializedName("tokenised")
	val tokenised: Boolean? = null,

	@field:SerializedName("international")
	val international: Boolean? = null,

	@field:SerializedName("entity")
	val entity: String? = null
)
@Keep
data class Recurring(

	@field:SerializedName("available")
	val available: Boolean? = null
)
@Keep
data class InnerGridGradientColors(

	@field:SerializedName("gThree")
	val gThree: Any? = null,

	@field:SerializedName("gTwo")
	val gTwo: Any? = null,

	@field:SerializedName("gOne")
	val gOne: Any? = null
)
@Keep
data class Bank(

	@field:SerializedName("bureauBankName")
	val bureauBankName: String? = null,

	@field:SerializedName("innerGridGradientColors")
	val innerGridGradientColors: InnerGridGradientColors? = null,

	@field:SerializedName("logoWithName")
	val logoWithName: String? = null,

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

	@field:SerializedName("benePaymentMethod")
	val benePaymentMethod: Any? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("ocrBankName")
	val ocrBankName: Any? = null,

	@field:SerializedName("IFSC_Code")
	val iFSCCode: Any? = null,

	@field:SerializedName("creditCardIfsc")
	val creditCardIfsc: Any? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("originalBankName")
	val originalBankName: String? = null,

	@field:SerializedName("alias")
	val alias: List<String?>? = null,

	@field:SerializedName("logo")
	val logo: String? = null,

	@field:SerializedName("rank")
	val rank: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
@Keep
data class AuthenticationTypesItem(

	@field:SerializedName("type")
	val type: String? = null
)
@Keep
data class OuterGridGradientColors(

	@field:SerializedName("gThree")
	val gThree: Any? = null,

	@field:SerializedName("gTwo")
	val gTwo: Any? = null,

	@field:SerializedName("gOne")
	val gOne: Any? = null
)
