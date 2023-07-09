package com.cheq.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQUser
import com.cheq.cache.sharedprefs.SharedPrefsConstants
import com.cheq.common.extension.empty
import com.cheq.common.models.UIState
import com.cheq.common.models.takeIfError
import com.cheq.common.models.takeIfLoading
import com.cheq.common.models.takeIfSuccess
import com.cheq.common.ui.base.BaseFragment
import com.cheq.common.utils.StatusBarUtils
import com.cheq.profile.databinding.FragmentReferralRewardsBinding
import com.cheq.profile.domain.entities.ReferralHistory
import com.cheq.profile.presentation.adapters.ReferralsHistoryAdapter
import com.cheq.profile.presentation.viewmodels.ReferralRewardsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
@AndroidEntryPoint
class ReferralRewardsFragment : BaseFragment<FragmentReferralRewardsBinding>() {

    @Inject
    @SharedPrefsCheQUser
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: ReferralRewardsViewModel by viewModels()
    private val userId: String by lazy {
        sharedPrefs.getString(SharedPrefsConstants.CHEQ_USER_ID, String.empty)
    }


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReferralRewardsBinding {
        return FragmentReferralRewardsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setLightStatusBar(requireActivity())

        viewModel.getReferralHistory(userId)

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.referralHistoryData.observe(viewLifecycleOwner) {
            setupUi(it)
        }
    }

    private fun setupUi(state: UIState<ReferralHistory>) {
        state.takeIfLoading {
            binding.loadingLottie.isVisible = true
        }.takeIfSuccess {
            if (data.history.isNotEmpty()) {
                binding.loadingLottie.isVisible = false
                binding.friendsReferredRv.isVisible = true
                setupRecyclerView(data)
            } else {
                binding.friendsReferredRv.isVisible = false
                binding.loadingLottie.isVisible = true
            }
        }.takeIfError {
            showWarningToast(binding.root, error.localizedMessage.orEmpty())
            binding.loadingLottie.isVisible = true
        }
    }

    private fun setupRecyclerView(data: ReferralHistory) {
        with(binding) {
            friendsReferredRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ReferralsHistoryAdapter(data.history)
                setHasFixedSize(true)
            }
        }
    }
}