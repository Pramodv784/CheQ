package com.cheq.retail.ui.login.modelv2.loanv1

import androidx.annotation.Keep

@Keep
data class PaymentModesAllowedX(
    val minLimit: String,
    val paymentMode: String
)