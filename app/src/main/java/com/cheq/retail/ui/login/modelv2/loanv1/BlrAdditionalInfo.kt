package com.cheq.retail.ui.login.modelv2.loanv1

import androidx.annotation.Keep

@Keep
data class BlrAdditionalInfo(
    val dataType: String,
    val optional: Boolean,
    val paramName: String
)