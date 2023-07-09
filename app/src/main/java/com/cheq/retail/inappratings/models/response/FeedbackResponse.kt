package com.cheq.retail.inappratings.models.response

data class FeedbackResponse(
    val message: String?,
    val apiMessage: String?,
    val status: Boolean?,
    val httpStatus: Int?
)