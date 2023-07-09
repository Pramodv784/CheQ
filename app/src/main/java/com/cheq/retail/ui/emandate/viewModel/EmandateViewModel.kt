package com.cheq.retail.ui.emandate.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.emandate.model.*
import com.cheq.retail.ui.main.model.DashBoardResponse
import com.cheq.retail.utils.Utils
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmandateViewModel : ViewModel() {
    val bankListObserver: MutableLiveData<BankListResponse.Banks> =
        MutableLiveData<BankListResponse.Banks>()

    val productObserver: MutableLiveData<DashBoardResponse> =
        MutableLiveData<DashBoardResponse>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val generateOrderObserver: MutableLiveData<EmandateOrderResponse> =
        MutableLiveData<EmandateOrderResponse>()

    val confirmationStatusObserver: MutableLiveData<JsonObject> =
        MutableLiveData<JsonObject>()

    fun getBanksList() {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.getAllEmandateBanksList()
                    progressObserver.postValue(false)
                    bankListObserver.postValue(response!!.body()!!.data)
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }

    }

    fun getAddedProducts() {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.getDashBoardData()

                    when {
                        response!!.code() == 404 -> {
                            progressObserver.postValue(false)
                            productObserver.postValue(null)
                        }
                        response.code() != 401 -> {
                            progressObserver.postValue(false)
                            productObserver.postValue(response.body()!!)
                        }
                    }
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
        }

    }

    fun modifyAutoPay(
        productId: String,
        totalDueEnabled: Boolean,
        isAutoPayEnabled: Boolean
    ) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.setAutopay(
                        EncryptionProvider(
                            AutopayTogglePost(
                                productId,
                                totalDueEnabled,
                                isAutoPayEnabled,
                            )
                        )
                    )
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }
    }

    fun generateRazorpayOrder(model: EmandateRpPostModel) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    progressObserver.postValue(true)

                    val response =
                        RestClient.getInstance().service.getRazorpayEmandateOrderId(
                            EncryptionProvider(
                                model
                            )
                        )

                    if (response?.body() != null){
                        generateOrderObserver.postValue(response.body())
                    } else {
                        statusObserver.postValue("Some error occurred!")
                    }
                    progressObserver.postValue(false)
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }

    }

    fun postConfirmationToServer(request: EmandateConfirmationPost) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.postEmandateConfirmation(EncryptionProvider(request))
                    confirmationStatusObserver.postValue(response!!.body())
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }

    }

}