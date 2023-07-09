package com.cheq.retail.ui.main.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.application.MainApplication
import com.cheq.retail.api.RestClient
import com.cheq.retail.ui.accountSettings.model.UserSettingResponse
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppMenuViewModel : ViewModel() {
    val responseObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val userEmailStatusObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val userEmailResponseObserver: MutableLiveData<UserSettingResponse> =
        MutableLiveData<UserSettingResponse>()
    val emailCountObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val cheqSafeStatusObserver: MutableLiveData<CheqSafeStatus?> =
        MutableLiveData<CheqSafeStatus?>()

    init {
     checkCheqSafeStatus()
    }

    fun getUserLinkedEmail() {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    userEmailStatusObserver.postValue(true)
                    val response =
                        RestClient.getInstance().service.getUserSettingDetails()
                    if (response?.body() != null) {
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
            statusObserver.postValue("Something went wrong")
        }

    }

    fun logOut() {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.logOut()

                    if (response != null) {
                        if (response.isSuccessful) {
                            /*if (response.body()!!.message.equals(
                                                "User update successfully",
                                                ignoreCase = true
                                            )
                                        )*/
                            responseObserver.postValue(true)
                            progressObserver.postValue(false)
                        } else {
                            statusObserver.postValue(response.message())
                            progressObserver.postValue(false)
                        }
                    }
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
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
                } else {
                    cheqSafeStatusObserver.postValue(null)
                }

            }
        } else {
            cheqSafeStatusObserver.postValue(null)
            statusObserver.postValue("Please connect to the internet!")
        }
    }
}


