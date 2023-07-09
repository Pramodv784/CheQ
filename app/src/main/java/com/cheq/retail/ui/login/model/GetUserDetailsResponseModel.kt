package com.cheq.retail.ui.login.model

import androidx.annotation.Keep

@Keep
data class GetUserDetailsResponseModel(
    var apiMessage: String,
    var token: String,
    var user: UserEntity
) {
    @Keep
    data class UserEntity(
        var panCard: String,
        var dateOfBirth: String,
        var lastName: String,
        var firstName: String,
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
        var _id: String
    )
}