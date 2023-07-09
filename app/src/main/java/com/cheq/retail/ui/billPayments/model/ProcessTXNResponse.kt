package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProcessTXNResponse(

	@field:SerializedName("txn_id")
	val txnId: String? = null,

	@field:SerializedName("data")
	val data: Any? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user_err_msg")
	val userErrorMessage: String? = null,


	@field:SerializedName("razorpay_payment_id")
	val razorpay_payment_id: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("txn_completed")
	val txn_completed: Boolean? = null,


)
