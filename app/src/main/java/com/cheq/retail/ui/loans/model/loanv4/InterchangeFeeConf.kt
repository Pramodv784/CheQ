package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class InterchangeFeeConf(
    val defaultFee: Boolean,
    val effctvFrom: String,
    val fees: List<String>,
    val mti: String,
    val responseCode: String
)