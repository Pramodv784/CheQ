package com.cheq.profile.presentation.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.profile.databinding.BottomsheetLinkingInitiatedCheqSafeBinding
import com.cheq.profile.databinding.LayoutCheqSafeEmailLinkingItemBinding
import com.cheq.profile.domain.entities.EmailLinkingStatus
import com.cheq.profile.presentation.viewstate.CheqSafeState
import com.cheq.common.R as commonR
import com.cheq.proxima.R as proxima

/**
 * Created by Akash Khatkale on 25th May, 2023.
 * akash.k@cheq.one
 */
class LinkingInitiatedCheqSafeBottomSheet: GenericBottomSheet<BottomsheetLinkingInitiatedCheqSafeBinding>() {

    private var data: CheqSafeState.LinkingInitiated? = null
    private var emailRow: LayoutCheqSafeEmailLinkingItemBinding? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetLinkingInitiatedCheqSafeBinding {
        return BottomsheetLinkingInitiatedCheqSafeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailRow = LayoutCheqSafeEmailLinkingItemBinding.bind(binding.emailLayout.root)

        setupUi()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.homeBtn.setOnClickListener {
            dismiss()
        }
        binding.cancelIv.setOnClickListener {
            dismiss()
        }
    }

    private fun setupUi() {
        emailRow?.emailTv?.text = data?.linkingEmail
        when (data?.status) {
            EmailLinkingStatus.IN_PROGRESS -> emailRow?.cheqSafeStatusTv?.apply {
                text = getString(commonR.string.linking)
                setTextColor(ContextCompat.getColor(requireContext(), proxima.color.golden_p6))
                setCompoundDrawables(null, null, null, null)
            }
            EmailLinkingStatus.SUCCESS -> emailRow?.cheqSafeStatusTv?.apply {
                text = getString(commonR.string.success)
                setTextColor(ContextCompat.getColor(requireContext(), proxima.color.colorPrimary))
                setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    commonR.drawable.ic_check_right,
                    0
                )
            }
            EmailLinkingStatus.FAILED, null -> {
                emailRow?.cheqSafeStatusTv?.apply {
                    text = null
                    setTextColor(resources.getColor(proxima.color.golden_p6))
                    setCompoundDrawables(null, null, null, null)
                }
            }
        }
    }

    private fun setData(data: CheqSafeState.LinkingInitiated?) {
        this.data = data
    }

    companion object {
        const val TAG = "LinkingInitiatedCheqSafeBottomSheet"
        fun newInstance(state: CheqSafeState.LinkingInitiated?): LinkingInitiatedCheqSafeBottomSheet {
            return LinkingInitiatedCheqSafeBottomSheet().apply {
                setData(state)
            }
        }
    }
}