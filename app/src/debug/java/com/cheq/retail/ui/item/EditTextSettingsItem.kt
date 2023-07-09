package com.cheq.retail.ui.item

import com.cheq.retail.R

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
class EditTextSettingsItem(
    val helpText: String,
    val editTextValue: String,
    val onTextChanged: (value: String) -> Unit
): SettingsItem {
    override fun getLayout(): Int = R.layout.edit_text_dev_settings_item

    override fun getType(): SettingsItemType = SettingsItemType.EDITTEXT

    fun onTextChange(s: CharSequence) {
        onTextChanged(s.toString())
    }
}