package com.cheq.retail.ui.main.viewModel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.loans.model.LoanBillersResponse
import com.cheq.retail.ui.main.model.DashBoardResponse
import com.cheq.retail.ui.main.model.SmsPostModel
import com.cheq.retail.utils.SmsUtils
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductBottomViewModel : ViewModel() {
    val responseObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()


    val topBillersObserver: MutableLiveData<LoanBillersResponse> =
        MutableLiveData<LoanBillersResponse>()


    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()


    fun getTopBillers() {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            // progressObserverGetDashBoard.postValue(true)
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {


                try {
                    val response = RestClient.getInstance().service.getTopBillers()

                    when {
                        response.code() == 404 -> {
                            topBillersObserver.postValue(null)
                        }
                        response.code() == 200 -> {
                            topBillersObserver.postValue(response.body())
                        }
                        response.code() != 401 -> {
                            topBillersObserver.postValue(null)
                        }
                    }
                } catch (e: Exception) {
                    statusObserver.postValue("Something went wrong")
                }

                // progressObserverGetDashBoard.postValue(false)
            }

        } else {
            statusObserver.postValue("Please connect to the internet!")
        }

    }


}