package com.cheq.retail.ui.main.model.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class RedeemCouponRequest(

	@field:SerializedName("cheqBrandId")
	val cheqBrandId: String? = null
)
