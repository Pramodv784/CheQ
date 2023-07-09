package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Request(

	@field:SerializedName("txn_id")
	val txnId: String? = null,

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("expiry_month")
	val expiryMonth: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("cvv")
	val cvv: String? = null,

	@field:SerializedName("upiPackageName")
	val upiPackageName: String? = null,

	@field:SerializedName("productId")
	val productId: String? = null,

	@field:SerializedName("paymentMode")
	val paymentMode: String? = null,

	@field:SerializedName("billId")
	val billId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("expiry_year")
	val expiryYear: String? = null,

	@field:SerializedName("netBankingObject")
	val netBankingObject: NetBankingRequest? = null
)
