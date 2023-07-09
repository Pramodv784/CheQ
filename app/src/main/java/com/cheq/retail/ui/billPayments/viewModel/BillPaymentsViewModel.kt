package com.cheq.retail.ui.billPayments.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.billPayments.model.*
import com.cheq.retail.ui.billSummary.model.BillSummaryRequest
import com.cheq.retail.ui.billSummary.model.BillSummaryResponse
import com.cheq.retail.ui.dashboard.view.customview.pendingchiptimeline.data.ChipLineItem
import com.cheq.retail.ui.main.model.RewardsDashboardResponse
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BillPaymentsViewModel : ViewModel() {
    val confirmationStatusObserver: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val confirmationResponseObserver: MutableLiveData<AddPaymentResponse2> =
        MutableLiveData<AddPaymentResponse2>()

    val startPaymentObserver: MutableLiveData<ProcessTXNResponse> =
        MutableLiveData<ProcessTXNResponse>()

    val generateOrderObserver: MutableLiveData<BillPaymentOrderResponse2> =
        MutableLiveData<BillPaymentOrderResponse2>()

    val paymentOptionsObserver: MutableLiveData<PaymentOptionsResponse> =
        MutableLiveData<PaymentOptionsResponse>()

    val statusObserver: MutableLiveData<String> = MutableLiveData<String>()

    val progressObserver: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val txnStartObserver: MutableLiveData<StartTXNResponse> = MutableLiveData<StartTXNResponse>()
    val getTXNStatusObserver: MutableLiveData<NewTranX> =
        MutableLiveData<NewTranX>()

    private val lockedChip = MutableLiveData<LockedChipResponse>()
    val lockedChipLiveData: LiveData<LockedChipResponse> get() = lockedChip

    val cheqSafeStatusObserver: MutableLiveData<CheqSafeStatus> =
        MutableLiveData<CheqSafeStatus>()
    private val getBillSummaryLiveData = MutableLiveData<State<BillSummaryResponse?>>()
    val getBillSummary: LiveData<State<BillSummaryResponse?>> get() = getBillSummaryLiveData

    init {
//        getDashboard()
        checkCheqSafeStatus()
        getLockedChips()
    }


    fun startPayment(startPaymentRequest: StartPaymentRequestNew) {
        progressObserver.postValue(true)

        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {

                try {
                    val response = RestClient.getInstance().service.startPaymentProcess(
                        EncryptionProvider(startPaymentRequest)
                    )
                    if (response?.body() != null && response.body()?.status != false) {
                        confirmationStatusObserver.postValue(true)
                        startPaymentObserver.postValue(response.body())
                    } else {
                        statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
                    }

                } catch (e: Exception) {
                    statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
                } finally {
                    progressObserver.postValue(false)
                }

            }
        } else {
            progressObserver.postValue(false)
            statusObserver.postValue("Please connect to the internet!")
        }
    }

    fun getPaymentOptions() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.getPaymentOptions()
                    progressObserver.postValue(false)
                    paymentOptionsObserver.postValue(response!!.body())
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }

    }


    fun getTXNStatus(txn_id: String, tazorTranId: String, upiAppName: String) {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {

                try {
                    val response =
                        RestClient.getInstance().service.postTXNStatusById(
                            EncryptionProvider(
                                TransectionId(upiAppName, tazorTranId, txn_id)
                            )
                        )
                    if (response!=null) {
                        getTXNStatusObserver.postValue(response.body())
                    } else {
                        getTXNStatusObserver.postValue(null)
                    }

                } catch (e: Exception) {
                    statusObserver.postValue("Something went wrong")

                }
            }

        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }



    fun getLockedChips() {
        val cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
            ?.getString(SharePrefsKeys.CHEQ_USER_ID) ?: return

        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {

                val response =
                    RestClient.getInstance().service.getLockedChips(cheqUserId)

                lockedChip.postValue(response.body())
            }

        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }

    fun checkCheqSafeStatus() {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->

            }) {
                val response = RestClient.getInstance().service.getUserProfile()

                if (response?.body() != null) {
                    cheqSafeStatusObserver.postValue(response.body()?.cheqSafeStatus)
                }

            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }

    fun getBillSummary(billSummaryRequest: BillSummaryRequest) {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {

                try {
                    getBillSummaryLiveData.postValue(State.Loading())
                    val response = RestClient.getInstance().service.getBillPaymentDetails(
                        EncryptionProvider(billSummaryRequest)
                    )
                    if (response?.isSuccessful == true && response.body() != null) {
                        getBillSummaryLiveData.postValue(State.Success(response.body()))
                    } else {
                        getBillSummaryLiveData.postValue(State.Error(AFConstants.SOMETHING_WENT_WRONG))
                    }
                } catch (e: Exception) {
                    getBillSummaryLiveData.postValue(State.Error(AFConstants.SOMETHING_WENT_WRONG))

                }
            }

        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }
}