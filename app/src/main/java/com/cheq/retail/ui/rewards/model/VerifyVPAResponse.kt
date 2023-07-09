package com.cheq.retail.ui.rewards.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VerifyVPAResponse(

	@field:SerializedName("data")
	val data: DataRewardVPA3? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
@Keep
data class DataRewardVPA3(

	@field:SerializedName("vpa")
	val vpa: String? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("customer_name")
	val customerName: Any? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("accountExists")
	val accountExists: String? = null,

	@field:SerializedName("user_err_msg")
	val userErrorMessage: String? = null,
)
