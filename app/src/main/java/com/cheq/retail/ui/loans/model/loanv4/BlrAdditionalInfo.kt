package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class BlrAdditionalInfo(
    val dataType: String,
    val optional: Boolean,
    val paramName: String
)