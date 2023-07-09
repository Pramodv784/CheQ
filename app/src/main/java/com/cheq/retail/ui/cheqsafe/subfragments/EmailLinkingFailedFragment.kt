package com.cheq.retail.ui.cheqsafe.subfragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.cheq.retail.R
import com.cheq.retail.databinding.BottomSheetDialogLinkingFailedBinding
import com.cheq.retail.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmailLinkingFailedFragment : BottomSheetDialogFragment() {

    interface Callback {
        fun onFailureRetry()
    }

    lateinit var binding: BottomSheetDialogLinkingFailedBinding

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = parentFragment as? Callback
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogLinkingFailedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as? BottomSheetDialog)?.apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dismissWithAnimation = true
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAction()
    }

    private fun setUpAction() {
        binding.btnRetry.setOnClickListener {
            callback?.onFailureRetry()
        }
        binding.ivCancel.setOnClickListener {
            dialog?.dismiss()
        }
    }
}