package com.cheq.retail.inappratings.models.request

data class FeedbackRequest(
    val detailedFeedbackText: String?,
    val options: List<String>,
    val page: String
)