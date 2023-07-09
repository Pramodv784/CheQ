package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class AddPaymentResponse2(
    val `data`: Data,
    val error: Any,
    val httpStatus: Int,
    val message: String,
    val product: Product,
    val response: Any,
    val status: Boolean,
    val user: User
)