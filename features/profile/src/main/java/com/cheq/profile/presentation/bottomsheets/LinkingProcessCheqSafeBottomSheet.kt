package com.cheq.profile.presentation.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cheq.common.ui.bottomsheet.GenericBottomSheet
import com.cheq.profile.databinding.BottomsheetLinkingProcessCheqSafeBinding

/**
 * Created by Akash Khatkale on 25th May, 2023.
 * akash.k@cheq.one
 */
class LinkingProcessCheqSafeBottomSheet: GenericBottomSheet<BottomsheetLinkingProcessCheqSafeBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetLinkingProcessCheqSafeBinding {
        return BottomsheetLinkingProcessCheqSafeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false
    }

    companion object {
        const val TAG = "LinkingProcessCheqSafeBottomSheet"
        fun newInstance(): LinkingProcessCheqSafeBottomSheet = LinkingProcessCheqSafeBottomSheet()
    }
}