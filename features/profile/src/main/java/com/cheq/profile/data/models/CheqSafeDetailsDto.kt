package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
data class CheqSafeDetailsDto(
    @SerializedName("status") var status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("apiMessage") val apiMessage: String?,
    @SerializedName("emailLinkingStatus") val emailLinkingStatus: String?
)