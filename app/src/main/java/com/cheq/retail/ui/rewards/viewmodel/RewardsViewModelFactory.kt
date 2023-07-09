package com.cheq.retail.ui.rewards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.ui.rewards.repository.RewardsRepository

class RewardsViewModelFactory(private val rewardsRepository: RewardsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RewardsViewModelNew(rewardsRepository) as T
    }
}