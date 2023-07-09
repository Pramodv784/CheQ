package com.cheq.retail.inappratings.base

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cheq.retail.inappratings.actions.IntentAction
import com.cheq.retail.inappratings.actions.S2SAction
import com.cheq.retail.inappratings.actions.SDKActionImpl
import com.cheq.retail.inappratings.actions.SDKActionType
import com.cheq.retail.inappratings.extensions.parcelable
import com.cheq.retail.inappratings.models.request.FeedbackRequest
import com.cheq.retail.inappratings.models.uimodels.*
import com.cheq.retail.inappratings.ui.TemplateFormFragment
import com.cheq.retail.inappratings.ui.TemplatePopUpFragment
import com.cheq.retail.inappratings.ui.TemplatePopUpSingleButtonFragment
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by Sanket Mendon on 16,June,2023.
 * sanket@cheq.one
 */

const val ARG_INAPP_TEMPLATE = "ARG_INAPP_TEMPLATE"

abstract class InAppRatingBaseFragment<T : ViewBinding> : BottomSheetDialogFragment() {
    private var _binding: T? = null
    protected val binding: T
        get() = _binding ?: error("Binding exception")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = BottomSheetDialog(requireActivity(), theme)
        bottomSheet.apply {
            setCanceledOnTouchOutside(false)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dismissWithAnimation = true

            setOnKeyListener(object : DialogInterface.OnKeyListener {
                override fun onKey(
                    dialog: DialogInterface?,
                    keyCode: Int,
                    event: KeyEvent?
                ): Boolean {
                    when (keyCode) {
                        KeyEvent.KEYCODE_BACK -> {
                            redirect()
                        }
                    }
                    return false
                }
            })
        }
        return bottomSheet
    }

    abstract fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): T

    private fun updateBottomsheet(
        currentFragmentTag: BottomSheetDialogFragment,
        fragment: BottomSheetDialogFragment,
        fragmentTag: String
    ) {
        parentFragmentManager.let { parentFragManager ->
            currentFragmentTag.dismiss()
            fragment.show(parentFragManager, fragmentTag)
        }
    }

    fun redirect() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    fun template(arguments: Bundle?): Template? = arguments?.parcelable(ARG_INAPP_TEMPLATE)

    fun navAction(
        currentFragment: BottomSheetDialogFragment,
        action: Template,
        feedbackRequest: FeedbackRequest? = null
    ) {
        when (action) {
            is PopUpTemplate -> {
                if (action.isSingleAction()) {
                    updateBottomsheet(
                        currentFragment,
                        TemplatePopUpSingleButtonFragment.newInstance(action),
                        TemplatePopUpSingleButtonFragment.TAG
                    )
                } else {
                    updateBottomsheet(
                        currentFragment,
                        TemplatePopUpFragment.newInstance(action),
                        TemplatePopUpFragment.TAG
                    )
                }
            }
            is FormTemplate -> {
                updateBottomsheet(
                    currentFragment,
                    TemplateFormFragment.newInstance(action),
                    TemplateFormFragment.TAG
                )
            }
            is RedirectionTemplate -> {
                action.url?.let {
                    startActivity(
                        CommonWebViewActivity.getStartIntent(
                            requireContext(),
                            url = it
                        )
                    )
                }
            }
            is S2STemplate -> {
                val postUrl = action.postUrl
                postUrl?.let { url ->
                    feedbackRequest?.let { request ->
                        S2SAction(url, request).execute()
                        redirect()
                    }
                }
            }
            is SDKTemplate -> {
                SDKActionImpl(requireContext(), SDKActionType.FRESHCHAT).execute()
            }
            is IntentTemplate -> {
                IntentAction(requireContext()).execute()
            }
        }
    }
}