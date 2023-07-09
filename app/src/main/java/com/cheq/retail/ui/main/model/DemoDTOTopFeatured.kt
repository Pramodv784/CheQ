package com.cheq.retail.ui.main.model

import androidx.annotation.Keep

@Keep
data class DemoDTOTopFeatured(
    val bgImage: Int,
    val voucherAmount: Int,
    val voucherType: String,
    val voucherDescription: String
)
