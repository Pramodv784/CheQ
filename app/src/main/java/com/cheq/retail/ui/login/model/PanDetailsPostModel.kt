package com.cheq.retail.ui.login.model

import androidx.annotation.Keep

@Keep
data class PanDetailsPostModel(
    var dateOfBirth: String,
    var panCard: String
)