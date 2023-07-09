package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ReportOtpResponse(
	val doneAt: String? = null,
	val mccMnc: String? = null,
	val smsCount: Int? = null,
	val price: Price? = null,
	val messageId: String? = null,
	val from: String? = null,
	val to: String? = null,
	val sentAt: String? = null,
	val error: Error? = null,
	val status: Status? = null,
	@field:SerializedName("apiMessage")
	val apiMessage: String? = null
)
@Keep
data class Error(
	val groupName: String? = null,
	val permanent: Boolean? = null,
	val groupId: Int? = null,
	val name: String? = null,
	val description: String? = null,
	val id: Int? = null
)
@Keep
data class Status(
	val groupName: String? = null,
	val groupId: Int? = null,
	val name: String? = null,
	val description: String? = null,
	val id: Int? = null
)
@Keep
data class Price(
	val pricePerMessage: Double? = null,
	val currency: String? = null
)

