package com.cheq.profile.presentation.listeners

import com.cheq.profile.presentation.models.SettingsModel.SettingsIdentifier

/**
 * Created by Akash Khatkale on 15th May, 2023.
 * akash.k@cheq.one
 */
interface OnAccountSettingsClicked {
    fun onClick(selected: SettingsIdentifier)
}