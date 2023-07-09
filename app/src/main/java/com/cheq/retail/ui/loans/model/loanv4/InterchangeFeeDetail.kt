package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class InterchangeFeeDetail(
    val effctvFrom: String,
    val effctvTo: String,
    val flatFee: Int,
    val percentFee: Double,
    val tranAmtRangeMax: Long,
    val tranAmtRangeMin: Int
)