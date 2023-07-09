package com.cheq.retail.ui.rewards.repository

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cheq.retail.R
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.ui.main.model.*
import com.cheq.retail.ui.main.model.request.RedeemCouponRequest
import com.cheq.retail.ui.rewards.model.*
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.utils.Utils
import com.google.firebase.crashlytics.FirebaseCrashlytics

class RewardsRepository(private val context: Context) {

    companion object {
        const val REQUEST_CODE_200 = 200
        const val REQUEST_CODE_201 = 201

    }

    private val rewardsDashboardLiveData = MutableLiveData<State<RewardsDashboardResponse>>()
    val rewardsLiveData: LiveData<State<RewardsDashboardResponse>> get() = rewardsDashboardLiveData

    private val voucherList = MutableLiveData<State<VoucherListResponse>>()
    val voucherListLiveData: LiveData<State<VoucherListResponse>> get() = voucherList

    private val redeemVoucher = MutableLiveData<State<RedeemCouponResponse>>()
    val redeemVoucherLiveData: LiveData<State<RedeemCouponResponse>> get() = redeemVoucher

    private val redeemVoucherHistory = MutableLiveData<State<VoucherRedeemHistoryResponse?>>()
    val redeemVoucherHistoryLiveData: LiveData<State<VoucherRedeemHistoryResponse?>> get() = redeemVoucherHistory

    private val verifyVPA = MutableLiveData<State<DataRewardVPA3>>()
    val verifyVPALiveData: LiveData<State<DataRewardVPA3>> get() = verifyVPA

    private val convertToCash = MutableLiveData<State<ConvertToCashResponse>>()
    val convertToCahLiveData: LiveData<State<ConvertToCashResponse>> get() = convertToCash

    private val vpaList = MutableLiveData<State<VpaListResponse>>()
    val vpaListLiveData: LiveData<State<VpaListResponse>> get() = vpaList

    suspend fun getRewardDashBoard() {
        try {
            if (Utils.isNetworkAvailable(context)) {
                rewardsDashboardLiveData.postValue(State.loading())
                val response = RestClient.getInstance().service.getRewardDashboard()
                if (response?.isSuccessful!! && response.code() == REQUEST_CODE_200 || response.code() == REQUEST_CODE_201) {
                    if (response.body() != null) {
                        rewardsDashboardLiveData.postValue(State.success(response.body()!!))
                    } else {
                        rewardsDashboardLiveData.postValue(State.error(""))
                    }
                } else {
                    rewardsDashboardLiveData.postValue(State.error("Something went wrong"))
                }
            } else {
                rewardsDashboardLiveData.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            //rewardsDashboardLiveData.postValue(State.error("Something went wrong"))
        }
    }

    suspend fun getVoucherListById(catId: String) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                voucherList.postValue(State.loading())
                val response = RestClient.getInstance().service.getVoucherListById(catId)
                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        voucherList.postValue(State.success(response.body()!!))
                    } else {
                        voucherList.postValue(State.error(response.message()))
                    }
                } else {
                    voucherList.postValue(State.error("Something went wrong"))
                }
            } else {
                voucherList.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            voucherList.postValue(State.error("Something went wrong"))
        }
    }

    suspend fun getVpaList(){
        try {
            if (Utils.isNetworkAvailable(context)) {
                vpaList.postValue(State.loading())
                val response = RestClient.getInstance().service.getVpaList()
                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        vpaList.postValue(State.success(response.body()!!))
                    } else {
                        vpaList.postValue(State.error(response.message()))
                    }
                } else {
                    vpaList.postValue(State.error("Something went wrong"))
                }
            } else {
                voucherList.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            vpaList.postValue(State.error("Something went wrong"))
        }
    }

    suspend fun verifyVPA(request: VerifyVPARequest) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                verifyVPA.postValue(State.loading())
                val response = RestClient.getInstance().service.verifyVPA(EncryptionProvider(request))
                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body()?.status ==true) {
                        verifyVPA.postValue(State.success(response.body()!!))
                    } else {
                        verifyVPA.postValue(State.error("Unable to verify the UPI ID. Please check & try again"))
                        //verifyVPA.postValue(response.body()?.data?.description?.let { State.error(it) })
                    }
                } else {
                    verifyVPA.postValue(State.error("Something went wrong"))
                }

            } else {
                verifyVPA.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            verifyVPA.postValue(State.error("Something went wrong"))
        }
    }

    suspend fun convertToCash(request: ConvertToCashRequest) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                convertToCash.postValue(State.loading())
                val response = RestClient.getInstance().service.startC2C(EncryptionProvider(request))
                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body()?.data != null && response.body()?.status == true) {
                        convertToCash.postValue(State.success(response.body()!!))
                    } else {

                        val errorMessage = response.body()?.let {
                            it.userErrorMessage ?: it.message
                        }

                       convertToCash.postValue(errorMessage?.let { State.error(it) })
                    }
                } else {
                    convertToCash.postValue(State.error("Failed to convert to cash, please try again"))
                }

            } else {
                convertToCash.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            convertToCash.postValue(State.error("Something went wrong"))
        }
    }

    suspend fun redeemCoupon(request: RedeemCouponRequest) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                redeemVoucher.postValue(State.loading())
                val response =
                    RestClient.getInstance().service.redeemCoupons(EncryptionProvider(request))

                if (response?.isSuccessful!! && response.code() == 200 || response.code() == 201) {
                    if (response.body() != null && response.body()!!.status != true) {
                        val errorMessage = response.body()?.userErrorMessage ?: response.body()?.massage.toString()
                        redeemVoucher.postValue(State.error(errorMessage))
                    } else {
                        redeemVoucher.postValue(State.success(response.body()!!))
                    }
                } else if (response.code() == 404) {
                    redeemVoucher.postValue(State.error("Something Went Wrong"))
                } else{
                    redeemVoucher.postValue(State.error("Something Went Wrong"))
                }
            } else {
                redeemVoucher.postValue(State.networkError("CONNECT TO INTERNET"))
            }
        } catch (e: Exception) {
            redeemVoucher.postValue(State.error("Something Went Wrong"))
        }
    }

    suspend fun redeemCouponHistory() {
        try {
            if (Utils.isNetworkAvailable(context)) {
                redeemVoucherHistory.postValue(State.loading())
                val response = RestClient.getInstance().service.redeemVoucherHistory()
                if (response?.isSuccessful == true && response.code() == 200 || response?.code() == 201) {
                    if (response.body() != null && response.body()?.vouchers?.size != 0) {
                        redeemVoucherHistory.postValue(State.success(response.body()))
                    } else {
                        redeemVoucherHistory.postValue(State.error(context.getString(R.string.no_record_found)))
                    }
                } else {
                    redeemVoucherHistory.postValue(State.error(context.getString(R.string.something_went_wrong)))
                }
            } else {
                redeemVoucherHistory.postValue(State.networkError(context.getString(R.string.connect_to_internet)))
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


}
