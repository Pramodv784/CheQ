package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DummyResponse(

	@field:SerializedName("compliant_with_tokenization_guidelines")
	val compliantWithTokenizationGuidelines: Boolean? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("cheqUserId")
	val cheqUserId: String? = null,

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

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("cardlast4")
	val cardlast4: String? = null,

	@field:SerializedName("cardEntity")
	val cardEntity: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("bankMasterId")
	val bankMasterId: Any? = null,

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

	@field:SerializedName("cardNetwork")
	val cardNetwork: String? = null,

	@field:SerializedName("cardSub_type")
	val cardSubType: String? = null,

	@field:SerializedName("usedAt")
	val usedAt: String? = null,

	@field:SerializedName("_id")
	val id: String? = null
)
