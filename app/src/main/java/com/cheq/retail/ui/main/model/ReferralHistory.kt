package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ReferralHistory(

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("history")
	val history: List<HistoryItem?>? = null
)
@Keep
data class HistoryItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("creation_date")
	val creationDate: String? = null,

	@field:SerializedName("message")
	val message: List<String?>? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)
