package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class PaymentModesAllowedX(
    val maxLimit: String,
    val minLimit: Double,
    val paymentMode: String,
    val supportPendingStatus: String
): Serializable