package com.cheq.profile.presentation.models

/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */
class SettingsModel(
    var id: SettingsIdentifier,
    var title: Int,
    var drawable: Int,
    var emailCount: Int = 0
) {
    enum class SettingsIdentifier {
        PERSONAL_DETAILS,
        TRANSACTION_HISTORY,
        MANAGE_CHEQ_SAFE,
        REFER_EARN,
        TERMS_CONDITIONS,
        PRIVACY_POLICY,
    }
}