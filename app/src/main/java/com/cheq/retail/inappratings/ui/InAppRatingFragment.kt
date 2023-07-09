package com.cheq.retail.inappratings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cheq.retail.R
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics
import com.cheq.retail.inappratings.base.ARG_INAPP_TEMPLATE
import com.cheq.retail.inappratings.extensions.parcelable
import com.cheq.retail.inappratings.mappers.toUITemplate
import com.cheq.retail.inappratings.models.response.Action
import com.cheq.retail.inappratings.models.uimodels.FormTemplate
import com.cheq.retail.inappratings.models.uimodels.PopUpTemplate
import com.cheq.retail.inappratings.models.uimodels.Template
import com.cheq.retail.inappratings.models.uistate.AppRatingUIState
import com.cheq.retail.inappratings.viewmodels.InAppRatingViewModel
import com.cheq.retail.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

/**
 * Created by Sanket Mendon on 20,June,2023.
 * sanket@cheq.one
 */

const val TOUCHPOINT_PAYIN = "payin"
const val TOUCHPOINT_VOUCHER = "voucher"
const val TOUCHPOINT_C2C = "c2c"

class InAppRatingFragment : BottomSheetDialogFragment() {
    private val viewModel: InAppRatingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.in_app_rating_parent_fragment, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUIState(arguments?.parcelable(ARG_INAPP_TEMPLATE))
        setupUI()
    }

    fun setupUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        AppRatingUIState.Loading -> {}
                        is AppRatingUIState.ShowPrompt -> {
                            val template = uiState.ratingPrompt?.toUITemplate()
                            template?.let {
                                when (it) {
                                    is PopUpTemplate -> { openPopUpTemplate(it) }
                                    is FormTemplate -> { openFormTemplate(it) }
                                }
                            }
                        }
                        is AppRatingUIState.Error, AppRatingUIState.NoPrompt -> {
                            redirect()
                        }
                    }
                }
            }
        }
    }

    private fun openPopUpTemplate(template: Template) {
        TemplatePopUpFragment.newInstance(template)
            .show(parentFragmentManager, TemplatePopUpFragment.TAG)
        dismiss()
    }

    private fun openFormTemplate(template: Template) {
        TemplateFormFragment.newInstance(template)
            .show(parentFragmentManager, TemplateFormFragment.TAG)
        dismiss()
    }

    private fun redirect() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    companion object {
        const val TAG = "InAppRatingFragment"

        @JvmStatic
        fun newInstance(touchpoint: String, cheqUserId: String, responseAction: Action? = null) =
            InAppRatingFragment().apply {
                InAppRatingAnalytics.setUserProperties(cheqUserId, touchpoint)
                responseAction?.let { action ->
                    arguments = Bundle().apply {
                        putParcelable(ARG_INAPP_TEMPLATE, action)
                    }
                }
            }
    }
}