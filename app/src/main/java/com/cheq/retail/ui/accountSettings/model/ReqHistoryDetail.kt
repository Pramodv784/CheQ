package com.cheq.retail.ui.accountSettings.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ReqHistoryDetail(

	@field:SerializedName("bill_id")
	val billId: String? = "B0D5BEF9DC272121",

	@field:SerializedName("cheq_uni_txn_id")
	val cheqUniTxnId: String? = "EA05E7600C40F5E9"
)
