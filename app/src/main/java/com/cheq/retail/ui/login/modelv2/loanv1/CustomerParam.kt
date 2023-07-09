package com.cheq.retail.ui.login.modelv2.loanv1

import androidx.annotation.Keep

@Keep
data class CustomerParam(
    val cheqParam: String,
    val dataType: String,
    val maxLength: Int,
    val minLength: Int,
    val optional: Boolean,
    val paramName: String,
    val regex: String,
    val visibility: Boolean
)