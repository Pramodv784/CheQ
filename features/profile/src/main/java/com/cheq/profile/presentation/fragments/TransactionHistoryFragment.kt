package com.cheq.profile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQ
import com.cheq.common.freshchat.FreshChat
import com.cheq.common.models.UIState
import com.cheq.common.models.takeIfError
import com.cheq.common.models.takeIfLoading
import com.cheq.common.models.takeIfSuccess
import com.cheq.common.ui.base.BaseFragment
import com.cheq.common.utils.StatusBarUtils
import com.cheq.profile.databinding.FragmentTransactionHistoryBinding
import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.domain.entities.TransactionHistoryDetail
import com.cheq.profile.presentation.adapters.TransactionHistoryAdapter
import com.cheq.profile.presentation.bottomsheets.TransactionHistoryDetailBottomsheet
import com.cheq.profile.presentation.listeners.OnTransactionHistoryItemClicked
import com.cheq.profile.presentation.viewmodels.TransactionHistoryViewModel
import com.cheq.profile.presentation.viewstate.TransactionHistoryViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
@AndroidEntryPoint
class TransactionHistoryFragment : BaseFragment<FragmentTransactionHistoryBinding>(), OnTransactionHistoryItemClicked {

    private val viewModel: TransactionHistoryViewModel by viewModels()
    private var isRecyclerViewTouchable = true

    @Inject
    @SharedPrefsCheQ
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var freshChat: FreshChat

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTransactionHistoryBinding {
        return FragmentTransactionHistoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setLightStatusBar(requireActivity())
        viewModel.getTransactionHistory()

        setupClickListeners()
        setupObservers()

        // todo: mparticle utils log event
    }

    private fun setupObservers() {
        viewModel.transactionHistoryData.observe(viewLifecycleOwner) {
            setupUi(it)
        }

        viewModel.transactionHistoryDetailData.observe(viewLifecycleOwner) {
            setupBottomSheet(it)
        }
    }

    private fun setupBottomSheet(state: UIState<TransactionHistoryDetail>?) {
        state?.takeIfLoading {
            isRecyclerViewTouchable = false
            binding.loadingLottie.isVisible = true
        }?.takeIfSuccess {
            isRecyclerViewTouchable = true
            binding.loadingLottie.isVisible = false
            val bottomsheet = TransactionHistoryDetailBottomsheet.newInstance(data, sharedPrefs)
            bottomsheet.show(parentFragmentManager, TransactionHistoryDetailBottomsheet.TAG)
        }?.takeIfError {
            showWarningToast(binding.root, error.localizedMessage.orEmpty())
            binding.loadingLottie.isVisible = false
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            backIv.setOnClickListener {
                findNavController().popBackStack()
            }
            helpIv.setOnClickListener {
                freshChat.showConversation()
            }
        }
    }

    private fun setupUi(state: UIState<TransactionHistory>) {
        state.takeIfLoading {
            binding.loadingLottie.isVisible = true
            binding.transactionHistoryRv.isVisible = false
        }.takeIfSuccess {
            binding.loadingLottie.isVisible = false
            if (data.transactionList.isNotEmpty()) {
                binding.transactionHistoryRv.isVisible = true
                setupRecyclerView(data)
            } else {
                binding.transactionHistoryRv.isVisible = false
                binding.noTransactionHistoryFl.isVisible = true
            }
        }.takeIfError {
            showWarningToast(binding.root, error.localizedMessage.orEmpty())
            binding.loadingLottie.isVisible = false
            binding.transactionHistoryRv.isVisible = false
            binding.noTransactionHistoryFl.isVisible = true
        }
    }

    private fun setupRecyclerView(data: TransactionHistory) {
        with(binding.transactionHistoryRv) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TransactionHistoryAdapter(data.transactionList, this@TransactionHistoryFragment)
            setHasFixedSize(true)

            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean = isRecyclerViewTouchable.not()
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
            })
        }
    }

    override fun onItemClicked(item: TransactionHistory.TransactionHistoryItem) {
        viewModel.getTransactionHistoryDetails(
            billId = item.id,
            cheqUniTransactionId = item.cheqUniTransactionId
        )
    }

}