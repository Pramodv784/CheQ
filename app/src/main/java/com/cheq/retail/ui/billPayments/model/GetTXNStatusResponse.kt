package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetTXNStatusResponse(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("enc")
	val enc: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
@Keep
data class DataStatus(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("bill_status")
	val billStatus: String? = null,

	@field:SerializedName("payin_time")
	val payinTime: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("cheq_user_id")
	val cheqUserId: String? = null,

	@field:SerializedName("bill_split")
	val billSplit: Int? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: String? = null,

	@field:SerializedName("payment_split")
	val paymentSplit: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("bankmaster_id")
	val bankmasterId: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("payout_time")
	val payoutTime: Any? = null
)
