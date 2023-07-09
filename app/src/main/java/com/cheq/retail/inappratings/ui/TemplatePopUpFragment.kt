package com.cheq.retail.inappratings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cheq.retail.databinding.InappTemplatePopupBinding
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.EVENT_RATING_NO
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.EVENT_RATING_YES
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.KEY_RATING
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.SCREEN_RATING_FILTER
import com.cheq.retail.inappratings.base.ARG_INAPP_TEMPLATE
import com.cheq.retail.inappratings.base.InAppRatingBaseFragment
import com.cheq.retail.inappratings.models.uimodels.PopUpTemplate
import com.cheq.retail.inappratings.models.uimodels.Template
import com.cheq.retail.inappratings.viewmodels.TemplatePopUpViewModel

/**
 * Created by Sanket Mendon on 14,June,2023.
 * sanket@cheq.one
 */

class TemplatePopUpFragment : InAppRatingBaseFragment<InappTemplatePopupBinding>() {
    private val viewModel: TemplatePopUpViewModel by viewModels()

    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): InappTemplatePopupBinding {
        return InappTemplatePopupBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        viewModel.setUiTemplate(template(arguments) as PopUpTemplate)
        setupUI()
        initListener()
    }

    fun setupUI() {
        binding?.apply {
            viewModel.uiTemplate()?.let { template ->
                labelPopupTitle.text = template.title
                Glide.with(requireContext()).load(template.titleIcon).into(imgTitleDrawable)
                template.buttons().takeIf {
                    it.isNotEmpty()
                }.apply {
                    btnLeft.text = this?.get(0)?.title
                    btnRight.text = this?.get(1)?.title
                }
            }
        }
        InAppRatingAnalytics.trackScreen(SCREEN_RATING_FILTER)
    }

    private fun initListener() {
        binding.btnLeft.setOnClickListener {
            viewModel.getButtonAction(0)?.let { action ->
                navAction(this, action)
            }
            InAppRatingAnalytics.trackEvent(EVENT_RATING_YES, map = hashMapOf(KEY_RATING to "good"))
        }
        binding.btnRight.setOnClickListener {
            viewModel.getButtonAction(1)?.let { action ->
                navAction(this, action)
            }
            InAppRatingAnalytics.trackEvent(EVENT_RATING_NO, map = hashMapOf(KEY_RATING to "bad"))
        }
        binding.btnCancel.setOnClickListener { redirect() }
    }
    companion object {
        const val TAG = "TemplatePopUpFragment"

        fun newInstance(selectedTemplate: Template?): TemplatePopUpFragment =
            TemplatePopUpFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_INAPP_TEMPLATE, selectedTemplate)
                }
            }
    }
}