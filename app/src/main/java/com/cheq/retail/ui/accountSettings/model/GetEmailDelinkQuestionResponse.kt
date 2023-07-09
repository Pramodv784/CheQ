package com.cheq.retail.ui.accountSettings.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetEmailDelinkQuestionResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("Answer")
	val answer: List<AnswerItem?>? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
@Keep
data class AnswerItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("questionId")
	val questionId: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

