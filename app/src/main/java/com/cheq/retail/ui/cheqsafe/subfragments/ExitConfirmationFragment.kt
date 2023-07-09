package com.cheq.retail.ui.cheqsafe.subfragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.cheq.retail.R
import com.cheq.retail.databinding.BottomSheetDialogLinkedConfirmBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

class ExitConfirmationFragment : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetDialogLinkedConfirmBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogLinkedConfirmBinding.inflate(layoutInflater, container, false)
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

        binding.btnDeny.setOnClickListener {
            dialog?.dismiss()
            //  openFailedDialog()

        }
        binding.btnAllow.setOnClickListener {
            dialog?.dismiss()

        }
        binding.ivCancel.setOnClickListener {
            dialog?.dismiss()
        }
    }

}