package com.cheq.retail.ui.loans.model.add_loan_response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ErrorResponse(

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("error")
	val error: Any? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
