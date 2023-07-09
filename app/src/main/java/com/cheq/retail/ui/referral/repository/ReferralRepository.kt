package com.cheq.retail.ui.referral.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.ReferralAPI
import com.cheq.retail.constants.AFConstants.CONNECT_TO_INTERNET
import com.cheq.retail.constants.AFConstants.MESSAGE
import com.cheq.retail.constants.AFConstants.SOMETHING_WENT_WRONG
import com.cheq.retail.ui.main.model.*
import com.cheq.retail.ui.referral.model.CheckShorUrlRequest
import com.cheq.retail.utils.NetworkResult
import com.cheq.retail.utils.Utils
import com.google.gson.JsonObject
import org.json.JSONObject

class ReferralRepository(private val referralAPI: ReferralAPI, private val applicationContext: Context) {
    private val createRefLiveData = MutableLiveData<NetworkResult<JsonObject>>()
    private val refEarnedLiveData = MutableLiveData<NetworkResult<ReferredEarnedResponse>>()
    private val refShortUrlLiveData = MutableLiveData<NetworkResult<ReferralUrlResponse>>()
    private val sendRefUrlLiveData = MutableLiveData<NetworkResult<ReferralUrlResponse>>()
    private val refStaticLiveData = MutableLiveData<NetworkResult<ReferralStaticData>>()
    private val refHistoryLiveData = MutableLiveData<NetworkResult<ReferralHistory>>()

    val createReferral: MutableLiveData<NetworkResult<JsonObject>>
        get() = createRefLiveData

    val referral: MutableLiveData<NetworkResult<ReferredEarnedResponse>>
        get() = refEarnedLiveData

    val refShortUrl: MutableLiveData<NetworkResult<ReferralUrlResponse>>
        get() = refShortUrlLiveData

    val refStaticData: MutableLiveData<NetworkResult<ReferralStaticData>>
        get() = refStaticLiveData

    val referralHistory: MutableLiveData<NetworkResult<ReferralHistory>>
        get() = refHistoryLiveData


    suspend fun createReferral(createReferralRequest: CreateReferralRequest) {
        if (Utils.isNetworkAvailable(applicationContext)) {
            try {
                val response = referralAPI.createReferral(EncryptionProvider(createReferralRequest))
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        createRefLiveData.postValue(NetworkResult.Success(response.body()!!))
                    } else if (response.errorBody() != null) {
                        val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                        createRefLiveData.postValue(NetworkResult.Error(errorObj.getString(MESSAGE)))
                    } else {
                        createRefLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
                    }
                }
            } catch (e: Exception) {
                createRefLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
            }
        } else {
            createRefLiveData.postValue(NetworkResult.Error(CONNECT_TO_INTERNET))
        }
    }

    suspend fun getReferralEarnedAndInvites(cheqUserId: String?) {
        if (Utils.isNetworkAvailable(applicationContext)) {
            try {
                val response = referralAPI.getReferralEarnedAndInvites(EncryptionProvider(CheckShorUrlRequest(cheqUserId)))
                if (response.isSuccessful && response.body() != null) {
                    refEarnedLiveData.postValue(NetworkResult.Success(response.body()!!))
                } else if (response.errorBody() != null) {
                    val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                    refEarnedLiveData.postValue(NetworkResult.Error(errorObj.getString(MESSAGE)))
                } else {
                    refEarnedLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
                }
            } catch (e: Exception) {
                refEarnedLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
            }
        } else {
            refEarnedLiveData.postValue(NetworkResult.Error(CONNECT_TO_INTERNET))
        }
    }


    suspend fun sendReferralUrl(sendReferralRequest: SendReferralRequest) {
        if (Utils.isNetworkAvailable(applicationContext)) {
            try {
                val response = referralAPI.sendReferral(EncryptionProvider(sendReferralRequest))
                if (response != null) {
                    if (response.isSuccessful && response.body() != null) {
                        sendRefUrlLiveData.postValue(NetworkResult.Success(response.body()!!))
                    } else if (response.errorBody() != null) {
                        val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                        sendRefUrlLiveData.postValue(NetworkResult.Error(errorObj.getString(MESSAGE)))
                    } else {
                        sendRefUrlLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
                    }
                }
            } catch (e: Exception) {
                sendRefUrlLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
            }
        } else {
            sendRefUrlLiveData.postValue(NetworkResult.Error(CONNECT_TO_INTERNET))
        }

    }

    suspend fun getReferralStaticData() {
        if (Utils.isNetworkAvailable(applicationContext)) {
            try {
                val response = referralAPI.getReferralStaticData(EncryptionProvider(CheckShorUrlRequest("")))
                if (response.isSuccessful && response.body() != null) {
                    refStaticLiveData.postValue(NetworkResult.Success(response.body()!!))
                } else if (response.errorBody() != null) {
                    val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                    refStaticLiveData.postValue(NetworkResult.Error(errorObj.getString(MESSAGE)))
                } else {
                    refStaticLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
                }
            } catch (e: Exception) {
                refStaticLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
            }
        } else {
            refStaticLiveData.postValue(NetworkResult.Error(CONNECT_TO_INTERNET))
        }

    }

    suspend fun getReferralHistory(cheqUserId: String?) {
        if (Utils.isNetworkAvailable(applicationContext)) {
            try {
                refHistoryLiveData.postValue(NetworkResult.Loading())
                val response = referralAPI.getReferralHistory(EncryptionProvider(CheckShorUrlRequest(cheqUserId)) )
                if (response.isSuccessful && response.body() != null) {
                    refHistoryLiveData.postValue(NetworkResult.Success(response.body()!!))
                } else if (response.errorBody() != null) {
                    val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                    refHistoryLiveData.postValue(NetworkResult.Error(errorObj.getString(MESSAGE)))
                } else {
                    refHistoryLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
                }
            } catch (e: Exception) {
                refHistoryLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
            }
        } else {
            refHistoryLiveData.postValue(NetworkResult.Error(CONNECT_TO_INTERNET))
        }
    }

}