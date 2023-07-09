package com.cheq.retail.ui

import androidx.lifecycle.ViewModel
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.cache.sharedprefs.SharedPrefsConstants.NEW_PROFILE_ENABLED
import com.cheq.retail.ui.item.SettingsItem
import com.cheq.retail.ui.item.SwitchSettingsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 5th June, 2023.
 * akash.k@cheq.one
 */
@HiltViewModel
class DevSettingsViewModel @Inject constructor(
    @SharedPrefsCheQ private val sharedPrefs: SharedPrefs
): ViewModel() {

    fun getItems(): List<SettingsItem> = buildList {
        addProfileEnabled()
    }

    private fun MutableList<SettingsItem>.addProfileEnabled() {
        add(
            SwitchSettingsItem(
                "New profile enabled",
                sharedPrefs.getBoolean(NEW_PROFILE_ENABLED)
            ) {
                sharedPrefs.putBoolean(NEW_PROFILE_ENABLED, it)
            }
        )
    }

}