package com.cheq.retail.ui.accountSettings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.accountSettings.model.*
import com.cheq.retail.utils.Utils
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountSettingsViewModel : ViewModel() {
    val userEmailStatusObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val userEmailResponseObserver: MutableLiveData<UserSettingResponse> =
        MutableLiveData<UserSettingResponse>()

    val linkEmailClickedObserver: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val userDelinkResponseObserver: MutableLiveData<GetEmailDelinkQuestionResponse> =
        MutableLiveData<GetEmailDelinkQuestionResponse>()
    val transactionListResponseObserver: MutableLiveData<DataHistory> =
        MutableLiveData<DataHistory>()

    val transactionDetailResponseObserver: MutableLiveData<DetailList> =
        MutableLiveData<DetailList>()

    val delinkResponseObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val delinkStatusObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()
    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()


    val emailCountObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()


    fun getUserLinkedEmail() {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    userEmailStatusObserver.postValue(true)
                    val response =
                        RestClient.getInstance().service.getUserSettingDetails()
                    if (response?.body()!= null) {
                        userEmailStatusObserver.postValue(false)
                        emailCountObserver.postValue(true)
                        userEmailResponseObserver.postValue(response.body())
                    } else {
                        userEmailStatusObserver.postValue(false)
                        emailCountObserver.postValue(false)
                    }

                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }

    }

    fun getUserDelinkQuestion() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response =
                        RestClient.getInstance().service.getUserDelinkQuestion()
                    if (response!!.body()!=null) {
                        userDelinkResponseObserver.postValue(response.body())
                    }

                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }

    }

    fun getTransactionList() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    progressObserver.postValue(true)
                    val response = RestClient.getInstance().service.getTransactionHistory()
                   // if (response?.body()?.data?.txnLists?.isNotEmpty() == true) {
                    if (response?.body()!=null) {
                        progressObserver.postValue(false)
                        transactionListResponseObserver.postValue(response.body())
                    } else {
                        progressObserver.postValue(false)
                    }

                }
            } else {

                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }

    }

    fun getTransactionDetail(request: ReqHistoryDetail) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            //val request = ReqHistoryDetail()
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                progressObserver.postValue(true)
                val response = RestClient.getInstance().service.getTransactionDetails( EncryptionProvider(
                    request
                ))
                // if (response?.body()?.data?.txnLists?.isNotEmpty() == true) {
                if (response?.body() !=null) {
                    progressObserver.postValue(false)
                    transactionDetailResponseObserver.postValue(response.body())
                } else {
                    progressObserver.postValue(false)
                }

            }
        } else {

            statusObserver.postValue("Please connect to the internet!")
        }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }

    }

    fun deLinkEmail(emailDelinkQuestionRequest: EmailDelinkQuestionRequest) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    delinkStatusObserver.postValue(true)

                    val response =
                        RestClient.getInstance().service.delinkEmail(
                            EncryptionProvider(
                                emailDelinkQuestionRequest
                            )
                        )

                    delinkStatusObserver.postValue(false)

                    if (response!!.code() == 200) {
                        delinkResponseObserver.postValue(true)
                    } else {
                        delinkResponseObserver.postValue(false)
                    }

                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }

    }

}