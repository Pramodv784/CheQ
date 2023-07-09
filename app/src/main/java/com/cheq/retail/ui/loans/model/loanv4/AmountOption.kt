package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class AmountOption(
    val amountBreakupSet: List<String>
) : Serializable