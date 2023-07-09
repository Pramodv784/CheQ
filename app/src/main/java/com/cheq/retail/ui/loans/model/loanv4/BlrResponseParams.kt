package com.cheq.retail.ui.loans.model.loanv4

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class BlrResponseParams(
    val amountOptions: List<AmountOption>
) : Serializable