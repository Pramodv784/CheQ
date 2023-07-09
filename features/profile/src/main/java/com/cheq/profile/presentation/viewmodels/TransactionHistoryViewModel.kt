package com.cheq.profile.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.common.annotations.IoDispatcher
import com.cheq.common.models.UIState
import com.cheq.profile.domain.entities.TransactionHistory
import com.cheq.profile.domain.entities.TransactionHistoryDetail
import com.cheq.profile.domain.usecase.GetTransactionHistoryDetailsUseCase
import com.cheq.profile.domain.usecase.GetTransactionHistoryUseCase
import com.cheq.profile.presentation.viewstate.TransactionHistoryViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val getTransactionHistoryDetailsUseCase: GetTransactionHistoryDetailsUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): ViewModel() {

    private val _transactionHistoryData: MutableLiveData<UIState<TransactionHistory>> = MutableLiveData(UIState.Loading())
    val transactionHistoryData: LiveData<UIState<TransactionHistory>> get() = _transactionHistoryData

    private val _transactionHistoryDetailData: MutableLiveData<UIState<TransactionHistoryDetail>> = MutableLiveData(null)
    val transactionHistoryDetailData: LiveData<UIState<TransactionHistoryDetail>> get() = _transactionHistoryDetailData

    fun getTransactionHistory() = viewModelScope.launch(dispatcher) {
        _transactionHistoryData.postValue(
            UIState.Loading()
        )
        val result = getTransactionHistoryUseCase.invoke()
        result.onSuccess { data ->
            _transactionHistoryData.postValue(UIState.Success(data))
        }.onFailure { error ->
            _transactionHistoryData.postValue(UIState.Error(error))
        }
    }


    fun getTransactionHistoryDetails(billId: String, cheqUniTransactionId: String) = viewModelScope.launch(dispatcher) {
        _transactionHistoryDetailData.postValue(
            UIState.Loading()
        )
        val result = getTransactionHistoryDetailsUseCase.invoke(
            GetTransactionHistoryDetailsUseCase.GetTransactionHistoryDetailsInput(
                billId, cheqUniTransactionId
            )
        )
        result.onSuccess { data ->
            _transactionHistoryDetailData.postValue(
                UIState.Success(data)
            )
        }.onFailure { error ->
            _transactionHistoryDetailData.postValue(
                UIState.Error(error)
            )
        }
    }

}