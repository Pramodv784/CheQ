package com.cheq.retail.ui.dashboard.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cheq.retail.api.RestClient
import com.cheq.retail.ui.dashboard.model.creditDashBoard.CreditDashBoardProductResponse
import com.cheq.retail.ui.dashboard.model.creditDashBoard.CreditDashBoardResponse
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.utils.Utils

class CreditRepository(private val context: Context) {
    private val creditDashBoard = MutableLiveData<State<CreditDashBoardResponse>>()
    private val creditDashBoardProduct = MutableLiveData<State<CreditDashBoardProductResponse>>()
    val crediteDashBoardLiveData: LiveData<State<CreditDashBoardResponse>> get() = creditDashBoard
    val crediteDashBoardProductLiveData: LiveData<State<CreditDashBoardProductResponse>> get() = creditDashBoardProduct

    suspend fun getDashBoard() {

        if (Utils.isNetworkAvailable(context)) {
            creditDashBoard.postValue(State.loading())
            try {
                val response = RestClient.getInstance().service.creditDashBoard()
                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        creditDashBoard.postValue(State.success(response.body()!!))
                    } else {
                        creditDashBoard.postValue(State.error(""))
                    }
                } else {
                    creditDashBoard.postValue(State.error("Something went wrong"))
                }
            } catch (e: Exception) {
                creditDashBoard.postValue(State.error("Something went wrong"))
            }
        } else {
            creditDashBoard.postValue(State.networkError("CONNECT TO INTERNET"))
        }
    }

    suspend fun getDashBoardCreditProduct() {

        try {
            if (Utils.isNetworkAvailable(context)) {
                creditDashBoardProduct.postValue(State.loading())
                val response = RestClient.getInstance().service.creditDashBoardProdcut()
                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        creditDashBoardProduct.postValue(State.success(response.body()!!))
                    } else {
                        creditDashBoardProduct.postValue(State.error(""))
                    }
                } else {
                    creditDashBoardProduct.postValue(State.error("Something went wrong"))
                }
            } else {
                creditDashBoardProduct.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            creditDashBoardProduct.postValue(State.error("Something went wrong"))
        }
    }
}