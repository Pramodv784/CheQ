package com.cheq.retail.ui.loans.model.add_loan

import androidx.annotation.Keep

@Keep
data class CustomerParam(
    val cheqParam: String,
    val dataType: String,
    val maxLength: Int,
    val minLength: Int,
    val optional: Int,
    val paramName: String,
    val regex: String,
    val value: String,
    val visibility: Int
)