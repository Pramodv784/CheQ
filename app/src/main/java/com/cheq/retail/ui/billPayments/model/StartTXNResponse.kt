package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class StartTXNResponse(

	@field:SerializedName("txn_id")
	val data: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
