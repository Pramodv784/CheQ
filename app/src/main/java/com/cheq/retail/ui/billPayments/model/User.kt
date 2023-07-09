package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep

@Keep
data class User(
    val __v: Int,
    val createdAt: String,
    val dateOfBirth: String,
    val deviceId: String,
    val email: String,
    val fcmToken: String,
    val firstName: String,
    val isActive: Boolean,
    val isAutoPayEnabled: Boolean,
    val isDeleted: Boolean,
    val isEmandateDone: Boolean,
    val lastName: String,
    val mobile: String,
    val panUpdated: Boolean,
    val updatedAt: String,
    val userSecret: UserSecret,
    val whatsAppAccess: Boolean
)