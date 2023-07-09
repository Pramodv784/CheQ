package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class CxDetails2(
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
    val whatsAppAccess: Boolean
): Serializable