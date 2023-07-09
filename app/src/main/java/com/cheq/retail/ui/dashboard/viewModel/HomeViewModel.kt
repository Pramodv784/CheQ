package com.cheq.retail.ui.dashboard.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.dashboard.model.homedashboad.HomeDashBoardResponse
import com.cheq.retail.ui.dashboard.repository.HomeRepository
import com.cheq.retail.ui.main.model.RewardsDashboardResponse
import com.cheq.retail.ui.main.model.VoucherRedeemHistoryResponse
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {


    val homeDashBoard: LiveData<State<HomeDashBoardResponse>> get() = homeRepository.homeDashBoardLiveData

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val cheqSafeStatusObserver: MutableLiveData<CheqSafeStatus> =
        MutableLiveData<CheqSafeStatus>()

    init {
        checkCheqSafeStatus()
    }

    fun getDashBoard() {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getDashBoard()
        }
    }

    fun checkCheqSafeStatus() {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                val response = RestClient.getInstance().service.getUserProfile()

                if (response!!.body() != null) {
                    cheqSafeStatusObserver.postValue(response.body()?.cheqSafeStatus)
                }

            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }
}