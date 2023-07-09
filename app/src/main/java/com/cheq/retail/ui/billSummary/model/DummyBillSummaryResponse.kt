package com.cheq.retail.ui.billSummary.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DummyBillSummaryResponse(

	@field:SerializedName("display_subtext")
	val displaySubtext: Any? = null,

	@field:SerializedName("info_text")
	val infoText: String? = null,

	@field:SerializedName("display_text")
	val displayText: String? = null,

	@field:SerializedName("value")
	val value: Int? = null,

	@field:SerializedName("key")
	val key: String? = null
)
