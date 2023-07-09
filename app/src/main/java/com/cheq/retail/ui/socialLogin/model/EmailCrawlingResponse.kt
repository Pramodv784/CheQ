package com.cheq.retail.ui.socialLogin.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EmailCrawlingResponse(

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
