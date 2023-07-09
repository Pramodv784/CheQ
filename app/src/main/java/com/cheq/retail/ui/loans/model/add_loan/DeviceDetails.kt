package com.cheq.retail.ui.loans.model.add_loan

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DeviceDetails(
    @field:SerializedName("APP")
    val APP: String,
    @field:SerializedName("IMEI")
    val IMEI: String,
    @field:SerializedName("INITIATING_CHANNEL")
    val INITIATING_CHANNEL: String,
    @field:SerializedName("IP")
    val IP: String,
    @field:SerializedName("OS")
    val OS: String
)