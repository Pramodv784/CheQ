package com.cheq.retail.ui.login.model

import androidx.annotation.Keep

@Keep
data class GenerateLoginOtpPost(
    var mobile: String? = null,
    var deviceId: String? = null,
    var cheqUserId: String? = null,
    var isFromLogin: Boolean? = null
)