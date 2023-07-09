package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class LoanProviders(
    val providers: List<Loan2>,
    val topProviders: List<Loan2>,
) : Serializable
