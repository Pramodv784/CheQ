package com.cheq.retail.ui.emandate.model

import androidx.annotation.Keep

@Keep
data class AutopayTogglePost(
    var productId: String,
    var totalDueEnabled: Boolean,
    var isAutoPayEnabled: Boolean
)