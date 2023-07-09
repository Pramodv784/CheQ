package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class BillPaymentOrderPost(
    var amount: Double,
    var billId: String,
    var productId: String
)