package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep

@Keep
data class CustomerParam(
    val dataType: String,
    val maxLength: Int,
    val minLength: Int,
    val optional: Boolean,
    val paramName: String,
    val regex: String,
    val visibility: Boolean
)