package com.cheq.retail.ui.billSummary.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GuaranteedEarning(

	@field:SerializedName("subtitle") var subtitle: String? = null,

	@field:SerializedName("is_monthly_limit_breached") var isMonthlyLimitBreached: Boolean? = null,

	@field:SerializedName("title") var title: String? = null,

	@field:SerializedName("splitTitle") var splitTitle: Boolean? = null,

	@field:SerializedName("infoPopup") val infoPopup: List<InfoPopupItem?>? = null
)

@Keep
data class InfoPopupItem(

	@field:SerializedName("title") val title: String? = null,

	@field:SerializedName("titleValue") val titleValue: String? = null,

	@field:SerializedName("items") val items: List<ItemsItem?>? = null
)

@Keep
data class ItemsItem(

	@field:SerializedName("itemName") val itemName: String? = null,

	@field:SerializedName("itemValue") val itemValue: String? = null,

	@field:SerializedName("isBold") val isBold: Boolean? = null
)
