package com.cheq.retail.ui.landing.model

import androidx.annotation.Keep

@Keep
data class LandingVO(
    val items: List<LandingItemVO>,
    val isDecoratedFooterVisible: Boolean
)