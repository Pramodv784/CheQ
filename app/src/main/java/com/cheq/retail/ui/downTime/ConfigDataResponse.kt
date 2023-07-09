package com.cheq.retail.utils

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class ConfigDataResponse(

	@field:SerializedName("cta")
	val cta: Cta? = null,

	@field:SerializedName("isenabled")
	val isenabled: Boolean? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("illustration")
	val illustration: Illustration? = null,

	@field:SerializedName("startTime")
	val startTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("title")
	val title: String? = null
): Serializable

@Keep
data class Illustration(

	@field:SerializedName("android")
	val android: String? = null,

	@field:SerializedName("iOS")
	val iOS: String? = null
) : Serializable
@Keep
data class Cta(

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("enabled")
	val enabled: Boolean? = null
) : Serializable
