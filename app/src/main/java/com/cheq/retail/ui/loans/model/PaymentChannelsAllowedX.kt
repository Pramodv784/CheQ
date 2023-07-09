package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class PaymentChannelsAllowedX(
    val maxLimit: Double,
    val minLimit: Double,
    val paymentMode: String,
    val supportPendingStatus: String
): Serializable