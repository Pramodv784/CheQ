package com.cheq.retail.inappratings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.InappTemplateFormBinding
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.EVENT_BAD_RATING_HELP
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.EVENT_BAD_RATING_SUBMIT
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.KEY_FEEDBACK
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.SCREEN_RATING_FILTER_BADRATING
import com.cheq.retail.inappratings.base.ARG_INAPP_TEMPLATE
import com.cheq.retail.inappratings.base.InAppRatingBaseFragment
import com.cheq.retail.inappratings.extensions.createChips
import com.cheq.retail.inappratings.models.request.FeedbackRequest
import com.cheq.retail.inappratings.models.uimodels.FormTemplate
import com.cheq.retail.inappratings.models.uimodels.Template
import com.cheq.retail.inappratings.models.uistate.FormTemplateUIState
import com.cheq.retail.inappratings.viewmodels.TemplateFormViewModel
import com.cheq.retail.utils.Utils
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

/**
 * Created by Sanket Mendon on 14,June,2023.
 * sanket@cheq.one
 */

class TemplateFormFragment :
    InAppRatingBaseFragment<InappTemplateFormBinding>() {
    private val viewModel: TemplateFormViewModel by viewModels()

    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): InappTemplateFormBinding {
        return InappTemplateFormBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        viewModel.setUiTemplate(template(arguments) as FormTemplate)
        setupUI()
        initListener()
    }

    private fun initListener() {
        binding.apply {
            btnSecondary.setOnClickListener {
                submitSecondaryAction()
            }
            btnPrimary.setOnClickListener { submitPrimaryAction() }
            btnCancel.setOnClickListener { redirect() }
            chipGroupOptions.setOnCheckedStateChangeListener { group, checkedIds ->
                val selections: MutableList<String> = mutableListOf()
                val ids = group.checkedChipIds
                for (id in ids) {
                    val chip: Chip = group.findViewById(id)
                    selections.add(chip.text.toString())
                }
                viewModel.updateSelectedList(selections)
            }
        }
    }

    private fun setupUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        FormTemplateUIState.Loading -> {
                            binding.apply {
                                chipGroupOptions.visibility = View.GONE
                                btnPrimary.isEnabled = false
                            }
                        }
                        is FormTemplateUIState.LoadingSuccess -> {
                            binding.apply {
                                labelFormTitle.text = uiState.title
                                Glide.with(requireContext()).load(uiState.titleImage)
                                    .into(imgTitleDrawable)
                                chipGroupOptions.visibility = View.VISIBLE
                                chipGroupOptions.createChips(uiState.options)
                                edtFeedback.isEnabled = false

                                uiState.buttons.forEach { index, buttonTemplate ->
                                    when (index) {
                                        0 -> {
                                            btnSecondary.text = buttonTemplate.title
                                        }
                                        1 -> {
                                            btnPrimary.text = buttonTemplate.title
                                        }
                                    }
                                }
                                btnPrimary.isEnabled = false
                            }
                            InAppRatingAnalytics.trackScreen(SCREEN_RATING_FILTER_BADRATING)
                        }
                        is FormTemplateUIState.SubmitEnabled -> {
                            binding.apply {
                                edtFeedback.isEnabled = true
                                btnPrimary.isEnabled = true
                            }
                        }
                        FormTemplateUIState.SubmitDisabled -> {
                            binding.apply {
                                edtFeedback.isEnabled = false
                                btnPrimary.isEnabled = false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun submitPrimaryAction() {
        viewModel.getAction(1)?.let { action ->
            navAction(
                this,
                action,
                FeedbackRequest(
                    detailedFeedbackText = binding.edtFeedback.text.toString(),
                    options = viewModel.getSelectedOptions(),
                    page = "Feedback Page"
                )
            )
            InAppRatingAnalytics.trackEvent(
                EVENT_BAD_RATING_SUBMIT,
                map = hashMapOf(
                    KEY_FEEDBACK to viewModel.getSelectedOptions().joinToString(",") { it })
            )
        }
        Utils.showToast(requireContext(), resources.getString(R.string.message_feedback_thanks))
    }

    private fun submitSecondaryAction() {
        viewModel.getAction(0)?.let { action ->
            navAction(this, action)
            InAppRatingAnalytics.trackEvent(EVENT_BAD_RATING_HELP)
        }
    }
    companion object {
        const val TAG = "TemplateFormFragment"
        fun newInstance(selectedTemplate: Template?): TemplateFormFragment =
            TemplateFormFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_INAPP_TEMPLATE, selectedTemplate)
                }
            }
    }
}