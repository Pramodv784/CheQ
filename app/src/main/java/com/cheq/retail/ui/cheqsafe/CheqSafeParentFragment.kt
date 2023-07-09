package com.cheq.retail.ui.cheqsafe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.ui.cheqsafe.subfragments.*
import com.cheq.retail.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CheqSafeParentFragment : Fragment(), EmailLinkingInitiatedFragment.Callback, EmailLinkingFailedFragment.Callback {

    companion object {
        private val TAG = CheqSafeParentFragment::class.java.simpleName
        private val VISIBLE_FRAGMENT_TAG = "$TAG.VISIBLE_FRAGMENT_TAG"
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[CheqSafeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cheq_safe, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeView()
    }

    private fun observeView() {
        viewModel.view.observe(viewLifecycleOwner) {
            val fragment = when (it) {
                ViewVO.Initial -> InitialLoadingFragment()
                ViewVO.Intro -> IntroductionFragment()
                is ViewVO.AlreadyLinkedEmail -> AlreadyLinkedEmailFragment()
                ViewVO.LinkingInProcess -> EmailLinkingInProcessFragment()
                ViewVO.LinkingFailed -> EmailLinkingFailedFragment()
                is ViewVO.LinkingInitiated -> EmailLinkingInitiatedFragment()
                ViewVO.UserConfirmation -> ExitConfirmationFragment()
                 is ViewVO.ExitConfirmation -> InitialLoadingFragment()
            }
            updateFragmentIfNeeded(fragment)
        }
    }

    private fun updateFragmentIfNeeded(fragment: BottomSheetDialogFragment) {
        val existingFragment = childFragmentManager.findFragmentByTag(VISIBLE_FRAGMENT_TAG)

        if (existingFragment?.javaClass != fragment.javaClass) {
            // do the replacement

            if (existingFragment != null) {
                childFragmentManager
                    .beginTransaction()
                    .remove(existingFragment)
                    .commit()
            }

            fragment.show(childFragmentManager, VISIBLE_FRAGMENT_TAG)
        }
    }

    override fun onFailureRetry() {
        viewModel.startFlow()
    }

    override fun onBackToHomeClicked() {
        parentFragmentManager.beginTransaction().remove(this).commit()
        if (activity !is MainActivity) {
            activity?.finish()
        }
    }

}