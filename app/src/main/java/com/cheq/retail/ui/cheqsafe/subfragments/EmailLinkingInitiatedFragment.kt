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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.databinding.BottomSheetDialogSuccessfullyLinkedBinding
import com.cheq.retail.ui.cheqsafe.CheqSafeViewModel
import com.cheq.retail.ui.cheqsafe.ViewVO
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.viewModel.AppMenuViewModel
import com.cheq.retail.ui.socialLogin.model.EmailLinkingStatus
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmailLinkingInitiatedFragment : BottomSheetDialogFragment() {

    interface Callback {
        fun onBackToHomeClicked()
    }

    private val viewModel by lazy {
        ViewModelProvider(parentFragment ?: this)[CheqSafeViewModel::class.java]
    }
    lateinit var viewModelAppMenu: AppMenuViewModel

    private lateinit var binding: BottomSheetDialogSuccessfullyLinkedBinding

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

        binding = BottomSheetDialogSuccessfullyLinkedBinding.inflate(
            layoutInflater,
            container,
            false
        )
        viewModelAppMenu=ViewModelProvider(requireActivity()).get(AppMenuViewModel::class.java)
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
        setupView()
        setUpAction()
    }

    private fun setupView() {

        val viewValue = viewModel.view.value as? ViewVO.LinkingInitiated

        when (viewValue?.status) {
            EmailLinkingStatus.IN_PROGRESS -> binding.linkingTV.apply {
                text = getString(R.string.linking)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.golden_color))
                setCompoundDrawables(null, null, null, null)
            }
            EmailLinkingStatus.SUCCESS -> binding.linkingTV.apply {
                text = getString(R.string.success)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_success_checq_safe,
                    0
                )
            }
            EmailLinkingStatus.FAILED,
            null -> {
                binding.linkingTV.apply {
                    text = null
                    setTextColor(R.color.golden_color)
                    setCompoundDrawables(null, null, null, null)
                }
            }
        }

        binding.tvLinkedEmail.text = viewValue?.linkingEmail
    }

    private fun setUpAction() {
        binding.btnLetRoll.setOnClickListener {
            callback?.onBackToHomeClicked()
        }
        binding.ivCancel.setOnClickListener {
            dialog?.dismiss()
            viewModelAppMenu?.getUserLinkedEmail()

        }
    }
}