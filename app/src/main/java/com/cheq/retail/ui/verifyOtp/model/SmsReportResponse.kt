package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class SmsReportResponse(

    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    val message: String
) {

}

