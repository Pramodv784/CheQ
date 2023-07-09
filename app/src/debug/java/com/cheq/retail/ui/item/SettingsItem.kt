package com.cheq.retail.ui.item

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
interface SettingsItem {
    fun getLayout(): Int
    fun getType(): SettingsItemType
}

enum class SettingsItemType {
    EDITTEXT,
    SWITCH
}