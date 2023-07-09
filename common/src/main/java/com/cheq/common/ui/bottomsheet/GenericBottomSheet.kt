package com.cheq.common.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cheq.common.extension.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by Akash Khatkale on 17th May, 2023.
 * akash.k@cheq.one
 */
abstract class GenericBottomSheet<T : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: T by viewBinding()
    protected val binding: T
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return _binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (showFullScreen()) {
            BottomSheetDialog(requireContext(), theme)
                .apply {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    dismissWithAnimation = true
                }
        } else {
            BottomSheetDialog(requireContext(), theme)
        }
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T

    open fun showFullScreen(): Boolean = true

}