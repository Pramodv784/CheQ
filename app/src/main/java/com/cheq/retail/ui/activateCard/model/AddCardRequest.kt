package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddCardRequest(

	@field:SerializedName("isToken")
	val isToken: Boolean? = null,

	@field:SerializedName("bankMasterId")
	val bankMasterId: String? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("cardHolderName")
	val cardHolderName: String? = null,

	@field:SerializedName("ccexpyr")
	val ccexpyr: String? = null,

	@field:SerializedName("ccexpmon")
	val ccexpmon: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("cardNumber")
	val cardNumber: String? = null,

	@field:SerializedName("ccvv")
	val ccvv: String? = null,

	@field:SerializedName("productInfo")
	val productInfo: String? = null,

	@field:SerializedName("network")
	val network: String? = null
)
