package com.cheq.profile.presentation.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsConstants.IS_CARD_SHOWN
import com.cheq.cache.sharedprefs.SharedPrefsConstants.IS_CHEQ_SAFE_VISIBLE
import com.cheq.cache.sharedprefs.SharedPrefsConstants.IS_LOGGED_IN
import com.cheq.cache.sharedprefs.SharedPrefsConstants.QUICK_LOGIN_AVAILABLE
import com.cheq.cache.sharedprefs.SharedPrefsConstants.SSL_SAVED
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.navigation.IntentFactory
import com.cheq.navigation.IntentKey
import com.cheq.profile.databinding.BottomsheetLogoutBinding
import com.moengage.core.MoECoreHelper

/**
 * Created by Akash Khatkale on 28th May, 2023.
 * akash.k@cheq.one
 */
class LogoutBottomsheet: GenericBottomSheet<BottomsheetLogoutBinding>() {

    private var sharedPrefs: SharedPrefs? = null
    private var sharedPrefsReferral: SharedPrefs? = null
    private var intentFactory: IntentFactory? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetLogoutBinding {
        return BottomsheetLogoutBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            confirmButton.setOnClickListener {
                logoutUser()
            }
            cancelButton.setOnClickListener {
                // todo: log mparticle event
                dismiss()
            }
        }
    }

    private fun logoutUser() {
        sharedPrefs?.let {
            it.putBoolean(IS_LOGGED_IN, false)
            it.putBoolean(QUICK_LOGIN_AVAILABLE, true)
            it.putBoolean(IS_CHEQ_SAFE_VISIBLE, false)
        }
        sharedPrefsReferral?.let {
            it.putBoolean(IS_CARD_SHOWN, false)
            it.putBoolean(SSL_SAVED, false)
        }
        MoECoreHelper.logoutUser(requireContext())
        val intent = intentFactory?.createIntent(requireContext(), IntentKey.ExistingUserActivityKey)
        requireActivity().apply {
            startActivity(intent)
            finish()
        }

        // todo: log mparticle event
    }

    private fun setSharedPrefs(sharedPrefs: SharedPrefs) {
        this.sharedPrefs = sharedPrefs
    }

    private fun setSharedPrefsReferral(sharedPrefs: SharedPrefs) {
        this.sharedPrefsReferral = sharedPrefs
    }

    private fun setIntentFactory(intentFactory: IntentFactory) {
        this.intentFactory = intentFactory
    }

    companion object {
        const val TAG = "LogoutBottomsheet"
        fun newInstance(
            sharedPrefs: SharedPrefs,
            sharedPrefsReferral: SharedPrefs,
            intentFactory: IntentFactory
        ): LogoutBottomsheet =
            LogoutBottomsheet().apply {
                setSharedPrefs(sharedPrefs)
                setSharedPrefsReferral(sharedPrefsReferral)
                setIntentFactory(intentFactory)
            }
    }
}

