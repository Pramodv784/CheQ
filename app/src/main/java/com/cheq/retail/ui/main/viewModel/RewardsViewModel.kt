package com.cheq.retail.ui.main.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.main.model.RedeemCouponResponse
import com.cheq.retail.ui.main.model.RewardsDashboardResponse
import com.cheq.retail.ui.main.model.VoucherListResponse
import com.cheq.retail.ui.main.model.VoucherRedeemHistoryResponse
import com.cheq.retail.ui.main.model.request.RedeemCouponRequest
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RewardsViewModel : ViewModel() {


    val statusObserver: MutableLiveData<String> = MutableLiveData<String>()
    val progressObserver: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val redeemHistoryObserver: MutableLiveData<VoucherRedeemHistoryResponse> =
        MutableLiveData<VoucherRedeemHistoryResponse>()


    fun getRedeemHistory() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.redeemVoucherHistory()
                    redeemHistoryObserver.postValue(response?.body())
                    progressObserver.postValue(false)
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong ")
        }
    }
}