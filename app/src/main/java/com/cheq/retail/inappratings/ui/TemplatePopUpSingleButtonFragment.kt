package com.cheq.retail.inappratings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cheq.retail.databinding.InappTemplatePopupSinglebuttonBinding
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.EVENT_RATING_CTA_CLICKED
import com.cheq.retail.inappratings.analytics.InAppRatingAnalytics.Companion.SCREEN_RATING_FILTER_REDIRECT
import com.cheq.retail.inappratings.base.ARG_INAPP_TEMPLATE
import com.cheq.retail.inappratings.base.InAppRatingBaseFragment
import com.cheq.retail.inappratings.models.uimodels.PopUpTemplate
import com.cheq.retail.inappratings.models.uimodels.Template
import com.cheq.retail.inappratings.viewmodels.TemplatePopUpSingleButtonViewModel

/**
 * Created by Sanket Mendon on 14,June,2023.
 * sanket@cheq.one
 */

class TemplatePopUpSingleButtonFragment :
    InAppRatingBaseFragment<InappTemplatePopupSinglebuttonBinding>() {
    private val templatePopUpVM: TemplatePopUpSingleButtonViewModel by viewModels()

    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): InappTemplatePopupSinglebuttonBinding {
        return InappTemplatePopupSinglebuttonBinding.inflate(
            layoutInflater,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        templatePopUpVM.setUiTemplate(template(arguments) as PopUpTemplate)
        setupUI()
        initListener()
    }

    fun setupUI() {
        binding.apply {
            templatePopUpVM.uiTemplate()?.let { data ->
                labelPopupTitle.text = data.title
                Glide.with(requireContext()).load(data.titleIcon).into(imgTitleDrawable)
                btnTitle.text = data.btnText
                Glide.with(requireContext()).load(data.btnIcon).into(btnTitleDrawable)
                inappLottieContainer.lottie.visibility =
                    if (data.lottieAvailable) View.VISIBLE else View.GONE
            }
        }
        InAppRatingAnalytics.trackScreen(SCREEN_RATING_FILTER_REDIRECT)
    }

    private fun initListener() {
        binding.btnPrimary.setOnClickListener {
            templatePopUpVM.uiTemplate()?.btnAction?.let { action ->
                submitPrimaryAction(action)
            }
        }
        binding.btnCancel.setOnClickListener { redirect() }
    }

    fun submitPrimaryAction(action: Template) {
        navAction(this, action)
        InAppRatingAnalytics.trackEvent(EVENT_RATING_CTA_CLICKED)
    }
    companion object {
        const val TAG = "TemplatePopUpSingleButtonFragment"

        fun newInstance(selectedTemplate: Template?): TemplatePopUpSingleButtonFragment =
            TemplatePopUpSingleButtonFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_INAPP_TEMPLATE, selectedTemplate)
                }
            }
    }
}