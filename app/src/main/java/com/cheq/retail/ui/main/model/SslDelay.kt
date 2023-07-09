package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class SslDelay(

	@field:SerializedName("delay")
	val delay: Long? = null
)
