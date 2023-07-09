package com.cheq.retail.ui.dashboard.model.homedashboad

import androidx.annotation.Keep

@Keep
data class WaitListRewardWidget(
    val status: Boolean,
    val lockedChips: Int?,
    val visibility: Boolean,
    val steps: List<StepData>
)
