package com.cheq.retail.ui.dashboard.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.ui.dashboard.repository.CreditRepository
import com.cheq.retail.ui.dashboard.repository.HomeRepository
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.viewmodel.RewardsViewModelNew


class CreditDashBoardViewModelFactory(private val creditRepository: CreditRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreditViewModel(creditRepository) as T
    }
}