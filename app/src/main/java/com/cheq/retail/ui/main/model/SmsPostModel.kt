package com.cheq.retail.ui.main.model

import androidx.annotation.Keep

@Keep
data class SmsPostModel(
    var smsData: List<SmsData>
) {
    @Keep
    data class SmsData(
        var smsTime: String,
        var text: String,
        var sender: String
    )
}

