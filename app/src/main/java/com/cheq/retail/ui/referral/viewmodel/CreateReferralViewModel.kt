package com.cheq.retail.ui.referral.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cheq.retail.ui.main.model.CreateReferralRequest
import com.cheq.retail.ui.referral.repository.ReferralRepository
import com.cheq.retail.utils.NetworkResult
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateReferralViewModel(val repository: ReferralRepository, val createReferralRequest: CreateReferralRequest) : ViewModel() {
    class CreateRefVMFactory(private val repository: ReferralRepository, private val createReferralRequest: CreateReferralRequest) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateReferralViewModel(repository,createReferralRequest) as T
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createReferral(createReferralRequest)
        }
    }


    val createReferral: MutableLiveData<NetworkResult<JsonObject>>
        get() = repository.createReferral


}