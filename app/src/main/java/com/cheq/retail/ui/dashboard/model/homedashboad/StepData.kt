package com.cheq.retail.ui.dashboard.model.homedashboad

import androidx.annotation.Keep

@Keep
data class StepData(
    val status: Boolean,
    val chips: Int,
    val message: String
)
