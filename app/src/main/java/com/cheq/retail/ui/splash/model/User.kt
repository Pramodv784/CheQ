package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep

@Keep

data class User(
    val __v: Int,
    val cheqUserId: String,
    val createdAt: String,
    val deviceId: String,
    val isActive: Boolean,
    val isAutoPayEnabled: Boolean,
    val isDeleted: Boolean,
    val isEmandateDone: Boolean,
    val mobile: Any,
    val panUpdated: Boolean,
    val updatedAt: String,
    val whatsAppAccess: Boolean
)