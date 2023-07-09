package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class LoanProviderResponse(
    val providers: ArrayList<Loan2>?,
    val topProviders: ArrayList<Loan2>?,
): Serializable