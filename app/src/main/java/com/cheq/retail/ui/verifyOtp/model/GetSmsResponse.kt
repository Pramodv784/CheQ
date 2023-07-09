package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetSmsResponse(
    var isRead: Boolean,
    var smsTime: String,
    var text: String,
    var sender: String,
    var usersmsId: String,
    var userId: String,
    var isDeleted: Boolean,
    var isActive: Boolean,
    var createdAt: String,
    var updatedAt: String,
    var _id: String,
    var isExist: Boolean,

    val apiMessage: String? = null,


    val status: Boolean? = null
)

