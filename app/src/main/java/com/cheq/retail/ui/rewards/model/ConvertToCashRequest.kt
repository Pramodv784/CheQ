package com.cheq.retail.ui.rewards.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ConvertToCashRequest(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("vpa")
	val vpa: String? = null,

	@field:SerializedName("chip_used")
	val chipUsed: Int? = null
)
