package com.cheq.retail.ui.main.model

import androidx.annotation.Keep

@Keep
data class CheqSafeData(
    val isVisible:Boolean,
    val successBannerUrl: String?,
    val failedBannerUrl: String?,
)
