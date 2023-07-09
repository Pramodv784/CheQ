package com.cheq.retail.ui.login.model

import androidx.annotation.Keep

@Keep
data class RequestOTPConsent(
    var cheqUserId: String? = null,
    var issueName: String? = null,
)

