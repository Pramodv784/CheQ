package com.cheq.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cheq.common.ui.base.BaseFragment
import com.cheq.common.utils.StatusBarUtils
import com.cheq.profile.R
import com.cheq.profile.databinding.FragmentPersonalDetailsBinding
import com.cheq.profile.domain.entities.PersonalDetails
import com.cheq.profile.presentation.viewmodels.PersonalDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */

private const val personalDetailsArgs = "personalDetailsArgs"
@AndroidEntryPoint
class PersonalDetailsFragment : BaseFragment<FragmentPersonalDetailsBinding>() {
    private val personalDetailsViewModel: PersonalDetailsViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalDetailsBinding {
        return FragmentPersonalDetailsBinding.inflate(inflater, container, false).apply {
            viewmodel = personalDetailsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setLightStatusBar(requireActivity())
        setupClickListeners()
        setupData()
    }
    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setupData() {
        val personalDetails = arguments?.get(personalDetailsArgs) as PersonalDetails?
        personalDetails?.let { personalDetailsViewModel.setPersonalDetails(it) }
    }
}