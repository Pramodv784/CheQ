package com.cheq.profile.presentation.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.common.config.remoteconfig.RemoteConfig
import com.cheq.common.config.remoteconfig.RemoteConfig.Companion.CHEQ_SAFE_PRIMARY_CTA
import com.cheq.common.config.remoteconfig.RemoteConfig.Companion.CHEQ_SAFE_SECONDARY_CTA
import com.cheq.common.config.remoteconfig.RemoteConfig.Companion.CHEQ_SAFE_SUB_TITLE
import com.cheq.common.config.remoteconfig.RemoteConfig.Companion.CHEQ_SAFE_TITLE
import com.cheq.profile.databinding.BottomsheetConfirmationCheqSafeBinding
import com.cheq.profile.presentation.viewmodels.MyAccountViewModel

/**
 * Created by Akash Khatkale on 25th May, 2023.
 * akash.k@cheq.one
 */
class ConfirmationCheqSafeBottomsheet: GenericBottomSheet<BottomsheetConfirmationCheqSafeBinding>() {

    private var remoteConfig: RemoteConfig? = null
    private var viewModel: MyAccountViewModel? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetConfirmationCheqSafeBinding {
        return BottomsheetConfirmationCheqSafeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MyAccountViewModel::class.java]

        setupClickListeners()
        setupUi()
    }

    private fun setupUi() {
        with(binding) {
            titleTv.text = remoteConfig?.getString(CHEQ_SAFE_TITLE)
            subtitleTv.text = remoteConfig?.getString(CHEQ_SAFE_SUB_TITLE)
            allowButton.text = remoteConfig?.getString(CHEQ_SAFE_PRIMARY_CTA)
            denyButton.text = remoteConfig?.getString(CHEQ_SAFE_SECONDARY_CTA)
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            denyButton.setOnClickListener {
                dismiss()
            }
            allowButton.setOnClickListener {
                viewModel?.notifyEnableCheqSafe()
            }
            cancelIv.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setRemoteConfig(config: RemoteConfig?) {
        this.remoteConfig = config
    }

    companion object {
        const val TAG = "ConfirmationCheqSafeBottomsheet"
        fun newInstance(remoteConfig: RemoteConfig?): ConfirmationCheqSafeBottomsheet =
            ConfirmationCheqSafeBottomsheet().apply {
                setRemoteConfig(remoteConfig)
            }
    }
}