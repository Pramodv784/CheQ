package com.cheq.retail.ui.main.viewModel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.main.model.*
import com.cheq.retail.ui.verifyOtp.model.FinartRequest
import com.cheq.retail.ui.verifyOtp.model.FinartResponse
import com.cheq.retail.utils.NavigationUtils
import com.cheq.retail.utils.NetworkResult
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProductViewModel : ViewModel() {
    val responseObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val responsComeFromeObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val dashboardObserver: MutableLiveData<DashBoardResponse> =
        MutableLiveData<DashBoardResponse>()

     val topBillersObserver: MutableLiveData<List<Loan2>> =
        MutableLiveData<List<Loan2>>()

    val errorBlockObserver: MutableLiveData<BlockData?> = MutableLiveData<BlockData?>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()


    val progressObserverGetDashBoard: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()


    val PayTogatherObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val finartObserver: MutableLiveData<FinartResponse> =
        MutableLiveData<FinartResponse>()

    val hideProductObserver: MutableLiveData<NetworkResult<HideProductResponse>> =
        MutableLiveData<NetworkResult<HideProductResponse>>()

    //read sms list
    fun readDeviceSms() {
//        val smsList = SmsUtils.getLatestDeviceSms()
//
//        println("SMS LIST ++++++++++++ " + smsList.size)
//
//        when {
////            smsList.isNotEmpty() -> {
////                postSmsToServer(smsList)
////            }
//
//            else -> {
//                getDashboardData()
//            }
//        }
        getDashboardData()
    }



    fun updatePayToGatherToggleStatus(toGatherStatus: Boolean){
        PayTogatherObserver.postValue(toGatherStatus)
    }

    fun postSmsToServer(smsList: List<SmsPostModel.SmsData>) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    // progressObserverPostSms.postValue(true)
                    progressObserverGetDashBoard.postValue(true)

                    RestClient.getInstance().service.postSmsToServer(
                        EncryptionProvider(
                            SmsPostModel(smsList)
                        )
                    )

                    SharePrefs.getInstance(MainApplication.getApplicationContext())
                        ?.putString(SharePrefsKeys.LAST_SYNC_TIME, smsList.last().smsTime)

                    Handler(Looper.getMainLooper()).postDelayed({
                        progressObserverGetDashBoard.postValue(false)
                        getDashboardData()
                    }, 5000)


                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }

    }

    fun getDashboardData() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserverGetDashBoard.postValue(true)
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {


                    val response = RestClient.getInstance().service.getDashBoardData()

                    if (response != null) {
                        when {
                            response.code() == 404 -> {
                                dashboardObserver.postValue(null)
                            }
                            response.code() == 200 && response.code() != 401 -> {
                                if (response.body().isUserBlocked()) {
                                    errorBlockObserver.postValue(response.body()?.blockedData)
                                } else {
                                    dashboardObserver.postValue(response.body())
                                }
                            }
                        }
                    }

                    progressObserverGetDashBoard.postValue(false)
                }

            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
        }

    }

     fun hideProduct(hideProductRequest: HideProductRequest) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.hideProduct(
                        EncryptionProvider(
                        hideProductRequest
                        )
                    )
                    response?.body()?.let {
                        if (response.isSuccessful && response.body() != null) {
                            hideProductObserver.postValue(NetworkResult.Success(response.body()!!))
                        } else if (response.errorBody() != null) {
                            val errorObj = response.errorBody()?.charStream()?.readText()
                                ?.let { it1 -> JSONObject(it1) }
                            hideProductObserver.postValue(
                                NetworkResult.Error(errorObj?.getString(
                                AFConstants.MESSAGE
                            )))
                        } else {
                            hideProductObserver.postValue(NetworkResult.Error(AFConstants.SOMETHING_WENT_WRONG))
                        }
                    }

                }
            } else {
                statusObserver.postValue(AFConstants.CONNECT_TO_INTERNET)
            }
        } catch (e: Exception) {
            statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
        }
    }

    fun finartTrigger(cheqUserId: String,screen:String) {
        if (NavigationUtils.isProfileIncomplete()) {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.finartTrigger(EncryptionProvider(FinartRequest(cheqUserId,screen)))

                    try {
                        if (response != null) {
                            if (response.body() != null) {
                                finartObserver.postValue(response.body())
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } else {
            // getUserWaitList()
            /*responseObserver.postValue(true)
            progressObserver.postValue(false)*/
        }
    }

}