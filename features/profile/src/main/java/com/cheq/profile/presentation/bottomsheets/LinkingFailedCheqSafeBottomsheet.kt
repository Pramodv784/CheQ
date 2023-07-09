package com.cheq.profile.presentation.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.profile.databinding.BottomsheetLinkingFailedCheqSafeBinding
import com.cheq.profile.presentation.viewmodels.MyAccountViewModel


/**
 * Created by Akash Khatkale on 25th May, 2023.
 * akash.k@cheq.one
 */
class LinkingFailedCheqSafeBottomsheet: GenericBottomSheet<BottomsheetLinkingFailedCheqSafeBinding>() {

    private var viewModel: MyAccountViewModel? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetLinkingFailedCheqSafeBinding {
        return BottomsheetLinkingFailedCheqSafeBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MyAccountViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.retryButton.setOnClickListener {
            dismiss()
            viewModel?.notifyEnableCheqSafe()
        }
        binding.cancelIv.setOnClickListener {
            dialog?.dismiss()
        }
    }

    companion object {
        const val TAG = "LinkingFailedCheqSafeBottomsheet"
        fun newInstance(): LinkingFailedCheqSafeBottomsheet =
            LinkingFailedCheqSafeBottomsheet()
    }

}