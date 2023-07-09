package com.cheq.retail.ui.referral.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cheq.retail.ui.main.model.ReferralHistory
import com.cheq.retail.ui.referral.repository.ReferralRepository
import com.cheq.retail.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReferralHistoryViewModel(val repository: ReferralRepository, val cheqUserId: String?) : ViewModel() {
    class HistoryVMFactory(private val repository: ReferralRepository, private val cheqUserId: String?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReferralHistoryViewModel(repository,cheqUserId) as T
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getReferralHistory(cheqUserId)
        }
    }


    val refHistory: MutableLiveData<NetworkResult<ReferralHistory>>
        get() = repository.referralHistory


}