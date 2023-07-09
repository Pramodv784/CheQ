package com.cheq.retail.ui.activateCard.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.activateCard.model.*
import com.cheq.retail.ui.billPayments.model.DebitCardResponse
import com.cheq.retail.ui.main.model.DashBoardResponse

import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductActivationViewModel : ViewModel() {
    val generateOrderObserver: MutableLiveData<GenerateOrderIdResponse> =
        MutableLiveData<GenerateOrderIdResponse>()
    val dashboardObserver: MutableLiveData<DashBoardResponse> = MutableLiveData<DashBoardResponse>()
    val statusObserver: MutableLiveData<String> = MutableLiveData<String>()
    val progressObserver: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val debitcardObserver: MutableLiveData<DebitCardResponse> = MutableLiveData<DebitCardResponse>()
    val addcardObserver: MutableLiveData<AddCardResponse> = MutableLiveData<AddCardResponse>()
    val productByIdObserver: MutableLiveData<GetProductByIdResponse> =
        MutableLiveData<GetProductByIdResponse>()
    val cardTypeObserver: MutableLiveData<CardDetailTypeResponse> =
        MutableLiveData<CardDetailTypeResponse>()


    fun fetchDebitCardDetails(cardNo: String) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response =
                        RestClient.getInstance().service.fetchDebitCardDetails(cardNo, "debit")
                    debitcardObserver.postValue(response!!.body())
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }

    }

    fun fetchCardDetailsType(request: BinCheckBodyRequest) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.fetchCardType(EncryptionProvider(request))
                    cardTypeObserver.postValue(response?.body())
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }

    }

    fun getDashboardData() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.getDashBoardData()

                    when {
                        response?.code() == 404 -> {
                            progressObserver.postValue(false)
                            dashboardObserver.postValue(null)
                        }
                        response?.code() == 200 || response?.code() == 201 -> {
                            progressObserver.postValue(false)
                            dashboardObserver.postValue(response.body())
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


    fun addCard(request: AddCardRequest) {
        progressObserver.postValue(true)

        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                try {
                    val response =
                        RestClient.getInstance().service.addCard(EncryptionProvider(request))
                    if (response?.code() != 500) {
                        addcardObserver.postValue(response!!.body())
                        progressObserver.postValue(false)
                    }
                } catch (e: Exception) {
                    statusObserver.postValue("Please connect to the internet!")
                }
                progressObserver.postValue(false)
            }
        } else {
            progressObserver.postValue(false)
            statusObserver.postValue("Please connect to the internet!")
        }

    }


    fun getProductById(getProductByIdRequest: GetProductByIdRequest) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.getProductById(
                        EncryptionProvider(getProductByIdRequest)
                    )
                    productByIdObserver.postValue(response!!.body())
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }

    }


}