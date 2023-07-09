package com.cheq.retail.ui.item

import com.cheq.retail.R

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
class SwitchSettingsItem(
    val helpText: String,
    val switchValue: Boolean,
    val onSwitchChange: (value: Boolean) -> Unit
): SettingsItem {
    override fun getLayout(): Int = R.layout.switch_dev_settings_item

    override fun getType(): SettingsItemType = SettingsItemType.SWITCH

    fun onSwitchChange(value: Boolean) {
        onSwitchChange.invoke(value)
    }
}