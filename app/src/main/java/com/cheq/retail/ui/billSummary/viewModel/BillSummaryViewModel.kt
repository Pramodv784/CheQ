package com.cheq.retail.ui.billSummary.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.ui.billSummary.model.BillSummaryRequest
import com.cheq.retail.ui.billSummary.model.BillSummaryResponse
import com.cheq.retail.ui.rewards.util.State


class BillSummaryViewModel : ViewModel() {
    private val getBillSummaryLiveData = MutableLiveData<State<BillSummaryResponse?>>()
    val getBillSummary: LiveData<State<BillSummaryResponse?>> get() = getBillSummaryLiveData


    suspend fun getBillSummary(billSummaryRequest: BillSummaryRequest) : LiveData<State<BillSummaryResponse?>>{
        try {
            getBillSummaryLiveData.postValue(State.Loading())
            val response = RestClient.getInstance().service.getBillPaymentDetails(
                EncryptionProvider(billSummaryRequest)
            )
            if (response?.isSuccessful == true && response.body() != null) {
                getBillSummaryLiveData.postValue(State.Success(response.body()))
            } else {
                getBillSummaryLiveData.postValue(State.Error(AFConstants.SOMETHING_WENT_WRONG))
            }
        } catch (e: Exception) {
            getBillSummaryLiveData.postValue(State.Error(e.message.toString()))
        }
        return getBillSummary
    }
}