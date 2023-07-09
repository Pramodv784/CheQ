package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class LockedChipResponse(
    val lockedChips: Int?,
    val paymentMessage: String?,
    val showPaymentMessage: Boolean?
)
