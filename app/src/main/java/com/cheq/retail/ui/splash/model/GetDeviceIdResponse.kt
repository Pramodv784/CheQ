package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GetDeviceIdResponse(
        var pinCode: String,
        var panCard: String,
        var dateOfBirth: String,
        var creditScore: Int,
        var lastName: String,
        var firstName: String,
        var cheqUserId: String,
        var email: String,
        var __v: Int,
        var createdAt: String,
        var updatedAt: String,
        var isAutoPayEnabled: Boolean,
        var isEmandateDone: Boolean,
        var whatsAppAccess: Boolean,
        var deviceId: String,
        var mobile: String,
        var isDeleted: Boolean,
        var isActive: Boolean,
        var _id: String,
        val apiMessage: String? = null,

        val status: Boolean? = null
    )
