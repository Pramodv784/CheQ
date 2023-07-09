package com.cheq.retail.ui.emandate.model

import androidx.annotation.Keep

@Keep
data class EmandateRpPostModel(
    var account_type: String?,
    var account_number: String?,
    var auth_type: String?,
    var ifsc_code: String?
)