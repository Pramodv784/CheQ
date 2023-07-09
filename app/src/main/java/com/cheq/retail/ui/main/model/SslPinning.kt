package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class SslPinning(

	@field:SerializedName("keys")
	val keys: ArrayList<String>? = null,

	@field:SerializedName("host")
	val host: String? = null,

	@field:SerializedName("enabled")
	val enabled: Boolean? = false,

	@field:SerializedName("issuer")
	val issuer: String? = null
)
