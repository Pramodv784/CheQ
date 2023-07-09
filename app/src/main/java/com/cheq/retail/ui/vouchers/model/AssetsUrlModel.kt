package com.cheq.retail.ui.vouchers.model

import androidx.annotation.Keep
@Keep
data class AssetsUrlModel(
    val accessTokenExpiry: Int,
    val assetsBaseUrl: AssetsBaseUrl,
    val waitlistCounter: Int
)
@Keep
data class AssetsBaseUrl(
    val waitlist: String
)
