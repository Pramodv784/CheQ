package com.cheq.retail.ui.emandate.model

import androidx.annotation.Keep

@Keep
data class EmandateConfirmationPost(
    var paymentId: String,
    var userName: String,
    var accountNumber: String,
    var ifscCode: String,
    var bankName: String,
    var bankId: String
)