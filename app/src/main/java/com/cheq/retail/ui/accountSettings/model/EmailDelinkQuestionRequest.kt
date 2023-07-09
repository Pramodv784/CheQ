package com.cheq.retail.ui.accountSettings.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class EmailDelinkQuestionRequest(

	@field:SerializedName("answerId")
	val answerId: String? = null,

	@field:SerializedName("questionId")
	val questionId: String? = null,

	@field:SerializedName("emailTokenId")
	val emailTokenId: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
