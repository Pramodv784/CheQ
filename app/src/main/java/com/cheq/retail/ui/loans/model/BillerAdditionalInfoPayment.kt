package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class BillerAdditionalInfoPayment(
    val dataType: String,
    val optional: Boolean,
    val paramName: String
): Serializable