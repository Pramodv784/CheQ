package com.cheq.retail.ui.rewards.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VerifyVPARequest(

	@field:SerializedName("vpa")
	val vpa: String? = null
)
