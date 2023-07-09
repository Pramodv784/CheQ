package com.cheq.retail.ui.dashboard.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cheq.retail.api.RestClient
import com.cheq.retail.ui.dashboard.model.homedashboad.HomeDashBoardResponse
import com.cheq.retail.ui.main.model.VoucherRedeemHistoryResponse

import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.utils.Utils

class HomeRepository(private val context: Context) {
    private val homeDashBoard = MutableLiveData<State<HomeDashBoardResponse>>()
    val homeDashBoardLiveData: LiveData<State<HomeDashBoardResponse>> get() = homeDashBoard

    suspend fun getDashBoard() {

        try {
            if (Utils.isNetworkAvailable(context)) {
                homeDashBoard.postValue(State.loading())
                val response = RestClient.getInstance().service.homeAndCreditDashBoard()
                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        homeDashBoard.postValue(State.success(response.body()!!))
                    } else {
                        homeDashBoard.postValue(State.error(""))
                    }
                } else {
                    homeDashBoard.postValue(State.error("Something went wrong"))
                }
            } else {
                homeDashBoard.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            //homeDashBoard.postValue(State.error("Something went wrong"))
        }
    }
}