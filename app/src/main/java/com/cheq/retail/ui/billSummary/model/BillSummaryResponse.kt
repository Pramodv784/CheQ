package com.cheq.retail.ui.billSummary.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BillSummaryResponse(

	@field:SerializedName("lineItems")
	val lineItems: List<LineItemsItem?>? = null,

	@field:SerializedName("isLimitBreach")
	val isLimitBreach: Boolean? = null,

	@field:SerializedName("paymentMode")
	val paymentMode: String? = null,

	@field:SerializedName("isWaivedFeeApplied") val isWaivedFeeApplied: Boolean? = null,

	@field:SerializedName("payTogether") val payTogether: Boolean? = null,

	@field:SerializedName("title") val title: String? = null,

	@field:SerializedName("instrumentLimits") val instrumentLimits: Map<String, Int>? = null,

	@field:SerializedName("isRewardsFeeApplicable") val isRewardsFeeApplicable: Boolean? = null,

	@field:SerializedName("footers") val footers: List<FootersItem?>? = null,

	@field:SerializedName("guaranteedEarning") var guaranteedEarning: GuaranteedEarning? = null,
)
@Keep
data class FootersItem(

	@field:SerializedName("displaySubtext")
	val displaySubtext: String? = null,

	@field:SerializedName("infoText")
	val infoText: String? = null,

	@field:SerializedName("displayText")
	val displayText: String? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null,

	@field:SerializedName("value")
	val value: Int? = null,

	@field:SerializedName("key")
	val key: String? = null
)
@Keep
data class InstrumentLimits(

	@field:SerializedName("UPI")
	val uPI: Int? = null
)
@Keep
data class Meta(

	@field:SerializedName("waivedFee")
	val waivedFee: Int? = null
)

@Keep
data class LineItemsItem(

	@field:SerializedName("displaySubtext")
	val displaySubtext: String? = null,

	@field:SerializedName("infoText")
	val infoText: String? = null,

	@field:SerializedName("displayText")
	val displayText: String? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null,

	@field:SerializedName("value")
	val value: Int? = null,

	@field:SerializedName("key")
	val key: String? = null
)
