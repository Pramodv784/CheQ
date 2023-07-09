package com.cheq.retail.ui.activateCard.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class AddCardResponse(

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("txn_id")
	val txn_id: String? = null,

	@field:SerializedName("PaymentLink")
	val paymentLink: String? = null,

	@field:SerializedName("txn_completed")
	val txn_completed: Boolean? = null,
	@field:SerializedName("apiMessage")
	val apiMessage: String? = null,

	@field:SerializedName("user_err_msg")
	val userErrorMessage: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)