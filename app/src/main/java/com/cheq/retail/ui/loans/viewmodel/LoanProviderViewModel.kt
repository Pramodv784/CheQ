package com.cheq.retail.ui.loans.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.loans.model.BBPSPREFILLED
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.loans.model.LoanProviderResponse
import com.cheq.retail.ui.loans.model.LoanProviders
import com.cheq.retail.ui.loans.model.add_loan.AddLoanDTO
import com.cheq.retail.ui.loans.model.add_loan_response.AddLoanResponse
import com.cheq.retail.ui.loans.model.add_loan_response.AddLoanResponseX
import com.cheq.retail.ui.loans.model.add_loan_response.ErrorResponse
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus


import com.cheq.retail.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject

class LoanProviderViewModel : ViewModel() {

    var loanProviderObserver: MutableLiveData<LoanProviderResponse?> = MutableLiveData()
    var addLoanResponse: MutableLiveData<ProductV2> = MutableLiveData()
    var GetLoanResponse: MutableLiveData<BBPSPREFILLED> = MutableLiveData()
    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()
    val cheqSafeStatusObserver: MutableLiveData<CheqSafeStatus> =
        MutableLiveData<CheqSafeStatus>()

    init {
        checkCheqSafeStatus()
    }

    fun getLoanList() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.getLoanProvider()
                    progressObserver.postValue(false)
                    loanProviderObserver.postValue(response.body())
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Soemthing went wrong")
        }
    }

    fun postLoanToServer(request: AddLoanDTO) {
        progressObserver.postValue(true)
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                val gson = Gson()
                print(
                    "data request ${
                        gson.toJson(
                            request
                        )
                    }"
                )
                try {
                    val response =
                        RestClient.getInstance().service.postLoanToServer(
                            EncryptionProvider(
                                request
                            )
                        )
                    // addLoanResponse.postValue(response?.body())
                    if (response?.code() == 200 || response?.code() == 201) {

                        addLoanResponse.postValue(response.body())

                    } else if (response?.code() == 600) {

                        statusObserver.postValue("We are unable to identify the loan. Please verify & try again")
                    } else if (response?.code() == 406) {

                        statusObserver.postValue("Please Try again")

                    } else {


                        statusObserver.postValue("No Biller Found")

                    }
                } catch (e: Exception) {
                    statusObserver.postValue("Something went wrong")
                }

                progressObserver.postValue(false)
            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
            progressObserver.postValue(false)
        }


    }


    fun bbpsPrefilledData(productId: String): MutableLiveData<BBPSPREFILLED> {
        progressObserver.postValue(true)
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {


                try {
                    val response =
                        RestClient.getInstance().service.bbpsPrefilledData(
                            productId
                        )

                    GetLoanResponse.postValue(response?.body())
                } catch (e: Exception) {
                    statusObserver.postValue("Something went wrong")
                }
                progressObserver.postValue(false)
            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
            progressObserver.postValue(false)
        }

        return GetLoanResponse
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