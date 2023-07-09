package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class PaymentModesAllowedX(
    val minLimit: String,
    val paymentMode: String
)