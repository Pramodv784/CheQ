package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class Data(
    val billDetails: Any,
    val customerPaymentInfo: CustomerPaymentInfo,
    val transactionDate: String,
    val transactionId: String
)