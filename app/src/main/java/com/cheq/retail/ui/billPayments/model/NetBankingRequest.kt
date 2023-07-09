package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class NetBankingRequest(
    val IFSC_Prefix: String, val bankName: String
) {}