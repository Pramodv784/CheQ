package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep

@Keep
data class User(
    val __v: Int,
    val _id: String,
    val cheqUserId: String,
    val createdAt: String,
    val deviceId: String,
    val fcmToken: String,
    val isActive: Boolean,
    val isAutoPayEnabled: Boolean,
    val isDeleted: Boolean,
    val isEmandateDone: Boolean,
    val mobile: String,
    val panUpdated: Boolean,
    val updatedAt: String,
    val customerStatus: String,
    val whatsAppAccess: Boolean
)