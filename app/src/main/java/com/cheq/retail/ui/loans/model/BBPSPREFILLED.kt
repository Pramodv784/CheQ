package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import com.cheq.retail.ui.login.model.loan.Loan
@Keep
data class BBPSPREFILLED(
    val httpStatus: Int,
    val message: String,
    val product: Loan2,
    val status: Boolean
)