package com.cheq.retail.ui.rewards.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class VpaListResponse(

	@field:SerializedName("apiMessage")
	val apiMessage: String? = null,

	@field:SerializedName("upiList")
	val upiList: List<UpiListItem?>? = null
)
@Keep
data class UpiListItem(

	@field:SerializedName("cheq_user_id")
	val cheqUserId: String? = null,

	@field:SerializedName("vpa")
	val vpa: String? = null,

	@field:SerializedName("num")
	val num: String? = null,

	@field:SerializedName("txn_status")
	val txnStatus: String? = null
)
