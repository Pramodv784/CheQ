package com.cheq.profile.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.common.annotations.IoDispatcher
import com.cheq.common.models.UIState
import com.cheq.profile.domain.entities.ReferralHistory
import com.cheq.profile.domain.usecase.GetReferralHistoryUseCase
import com.cheq.profile.presentation.viewstate.ReferralHistoryViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
@HiltViewModel
class ReferralRewardsViewModel @Inject constructor(
    private val getReferralHistoryUseCase: GetReferralHistoryUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): ViewModel() {

    private val _referralHistoryData: MutableLiveData<UIState<ReferralHistory>> = MutableLiveData(UIState.Loading())
    val referralHistoryData: LiveData<UIState<ReferralHistory>> get() = _referralHistoryData

    fun getReferralHistory(userId: String) = viewModelScope.launch(dispatcher) {
        _referralHistoryData.postValue(UIState.Loading())
        val result = getReferralHistoryUseCase.invoke(
            GetReferralHistoryUseCase.GetReferralHistoryInput(userId)
        )
        result.onSuccess { data ->
            _referralHistoryData.postValue(UIState.Success(data))
        }.onFailure { error ->
            _referralHistoryData.postValue(UIState.Error(error))
        }
    }

}