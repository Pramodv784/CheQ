package com.cheq.retail.ui.socialLogin.model

import androidx.annotation.Keep

@Keep
data class UserGmailDetailsResponse(
    var status: Boolean,
//    val apiMessage: String,
//    val parsedEmails: Int,
    val message: String?,
    val apiMessage: String?,
    val emailLinkingStatus: EmailLinkingStatus
)

@Keep
data class EmailLinkingStatusData(
    val emailLinkingStatus: EmailLinkingStatus
)