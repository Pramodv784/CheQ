package com.cheq.profile.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.common.annotations.IoDispatcher
import com.cheq.common.models.UIState
import com.cheq.profile.domain.entities.ReferralEarned
import com.cheq.profile.domain.entities.ReferralStatic
import com.cheq.profile.domain.usecase.GetReferralEarnedUseCase
import com.cheq.profile.domain.usecase.GetReferralStaticUseCase
import com.cheq.profile.domain.usecase.SendReferralUrlUseCase
import com.cheq.profile.presentation.viewstate.ReferEarnViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
private const val REFER_EARN_VIEWMODEL = "REFER_EARN_VIEWMODEL"
@HiltViewModel
class ReferEarnViewModel @Inject constructor(
    private val getReferralStaticUseCase: GetReferralStaticUseCase,
    private val getReferralEarnedUseCase: GetReferralEarnedUseCase,
    private val sendReferralUrlUseCase: SendReferralUrlUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): ViewModel() {

    private val _referralStaticData: MutableLiveData<UIState<ReferralStatic>> = MutableLiveData(UIState.Loading())
    val referralStaticData: LiveData<UIState<ReferralStatic>> get() = _referralStaticData

    private val _referralEarnedData: MutableLiveData<ReferralEarned?> = MutableLiveData(null)
    val referralEarnedData: LiveData<ReferralEarned?> get() = _referralEarnedData


    fun getReferralStaticData(userId: String) = viewModelScope.launch(dispatcher) {
        _referralStaticData.postValue(UIState.Loading())
        val result = getReferralStaticUseCase.invoke(
            GetReferralStaticUseCase.GetReferralStaticInput(
                userId
            )
        )
        result.onSuccess { data ->
            _referralStaticData.postValue(UIState.Success(data))
        }.onFailure { error ->
            _referralStaticData.postValue(UIState.Error(error))
        }
    }

    fun getReferralEarnedData(userId: String) = viewModelScope.launch(dispatcher) {
        val result = getReferralEarnedUseCase.invoke(
            GetReferralEarnedUseCase.GetReferralEarnedInput(
                userId
            )
        )
        result.onSuccess { data ->
            _referralEarnedData.postValue(data)
        }.onFailure { error ->
            _referralEarnedData.postValue(null)
        }
    }

    fun sendReferralUrl(userId: String, url: String) = viewModelScope.launch(dispatcher) {
        val result = sendReferralUrlUseCase.invoke(
            SendReferralUrlUseCase.SendReferralUrlInput(
                userId,
                url
            )
        )
        result.onSuccess {
            Log.d(REFER_EARN_VIEWMODEL, "Send referral success: $it")
        }.onFailure {
            Log.d(REFER_EARN_VIEWMODEL, "Send referral failure: $it")
        }
    }

}