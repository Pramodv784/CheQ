package com.cheq.retail.ui.login.model

import androidx.annotation.Keep

@Keep
data class RequestUserConsent(
    var cheqUserId: String? = null,
    var permissionName: String? = null,
    var status: Boolean? = null,
//    var whatsAppAccess: Boolean? = null,
//    var sMSPermission: Boolean? = null,
//    var phoneStatePermission: Boolean? = null,
//    var notificationPermission: Boolean? = null,
    var deviceInfo: DeviceInfo? = null
)

@Keep
data class DeviceInfo(
    var deviceId: String? = null
)