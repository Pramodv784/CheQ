package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName

data class ReferralHistoryDto(
    @SerializedName("httpStatus") val httpStatus: Int? = null,
    @SerializedName("history") val history: List<HistoryItem?>? = null
) {
    data class HistoryItem(
        @SerializedName("name") val name: String? = null,
        @SerializedName("creation_date") val creationDate: String? = null,
        @SerializedName("message") val message: List<String?>? = null,
        @SerializedName("status") val status: String? = null,
        @SerializedName("type") val type: String? = null
    )
}