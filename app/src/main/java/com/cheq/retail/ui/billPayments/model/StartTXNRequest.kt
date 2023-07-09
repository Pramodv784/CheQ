package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class StartTXNRequest(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("billId")
	val billId: String? = null,

	@field:SerializedName("bankmaster_id")
	val bankmasterId: String? = null
)
