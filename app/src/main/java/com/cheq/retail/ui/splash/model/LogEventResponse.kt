package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class LogEventResponse(
    @SerializedName("data")
    val event: EventData,
    @SerializedName("status")
    val status: Boolean
): Serializable


@Keep
data class EventData(
    @SerializedName("events")
    val event: Event,
    @SerializedName("httpStatus")
    val status: Boolean
): Serializable