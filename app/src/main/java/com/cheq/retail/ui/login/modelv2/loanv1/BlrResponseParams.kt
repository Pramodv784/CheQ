package com.cheq.retail.ui.login.modelv2.loanv1

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class BlrResponseParams(
    val amountOptions: List<AmountOption>
) : Serializable