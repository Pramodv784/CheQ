package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class InterchangeFeeX(
    val feeCode: String,
    val feeDesc: String,
    val feeDirection: String,
    val interchangeFeeDetails: List<InterchangeFeeDetail>
)