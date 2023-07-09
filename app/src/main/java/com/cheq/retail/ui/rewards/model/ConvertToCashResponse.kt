package com.cheq.retail.ui.rewards.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ConvertToCashResponse(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user_err_msg")
	val userErrorMessage: String? = null,

	@field:SerializedName("txn_status")
	val txn_status: String? = null,

	@field:SerializedName("apiMessage")
	val apiMessage: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null


)
