package com.cheq.retail.ui.verifyOtp.viewModel

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerLib
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AFConstants.CURRENT_USER_CHEQ_ID
import com.cheq.retail.constants.AFConstants.ERROR_DESCRIPTION
import com.cheq.retail.constants.AFConstants.REFERRAL_REQEUST_DATA
import com.cheq.retail.databinding.ActivityVerifyOtpBinding
import com.cheq.retail.sharePreferences.*
import com.cheq.retail.ui.login.OtpReportRequest
import com.cheq.retail.ui.login.model.GenerateLoginOtpPost
import com.cheq.retail.ui.login.model.isUserBlocked
import com.cheq.retail.ui.main.model.CreateReferralRequest
import com.cheq.retail.ui.verifyOtp.model.*
import com.cheq.retail.utils.*
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class VerifyOtpViewModel : ViewModel() {
    val responseObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val errorBlockObserver: MutableLiveData<BlockData?> = MutableLiveData<BlockData?>()

    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val smsObserver: MutableLiveData<ReportOtpResponse> =
        MutableLiveData<ReportOtpResponse>()
    val loadingObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val timeOutObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val createReferralObserver: MutableLiveData<NetworkResult<JsonObject>> =
        MutableLiveData<NetworkResult<JsonObject>>()

    private val _verifyOTPState: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val verifyOTPStateObserver: LiveData<Boolean> = _verifyOTPState

    lateinit var timer: CountDownTimer


    init {
        SharePrefs.getInstance(MainApplication.getApplicationContext())!!.clearSharePrefs()
    }

    fun verifyOtp(otp: String, mobile: String, fcmToken: String, context: Context) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val cheqUserId =
                        SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                            ?.getString(SharePrefsKeys.CHEQ_USER_ID)
                    //   Freshchat.getInstance(context).setPushRegistrationToken(fcmToken)
                    val response = RestClient.getInstance().service.verifyOtpByMobileNumber(

                        EncryptionProvider(
                            VerifyOtpPost(
                                otp,
                                mobile,
                                Utils.getDeviceID(),
                                fcmToken,
                                "" + cheqUserId,
                                AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""

                            )
                        )

                    )


                    if(response?.body()!=null)
                    {
                        progressObserver.postValue(false)
                        if (response.body().isUserBlocked()) {
                            errorBlockObserver.postValue(response.body()?.blockedData)
                        } else {
                            if(response.body()?.status == true){
                                createReferralRequest()
                                SharePrefs.getInstance(MainApplication.getApplicationContext())
                                    ?.setSharedPreferences(response.body())
                                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                    ?.putString(
                                        SharePrefsKeys.ACCESS_TOKEN,
                                        response.body()?.token?.accessToken
                                    )
                                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                    ?.putString(
                                        SharePrefsKeys.REFRESH_TOKEN,
                                        response.body()?.token?.refreshToken
                                    )

                                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                    ?.putString(
                                        SharePrefsKeys.CHEQ_USER_ID,
                                        response.body()?.user?.cheqUserId
                                    )
                                _verifyOTPState.postValue(true)
                                checkUserProfile()
                                Utils.createFreshChatUser()
                            }else{
                                statusObserver.postValue(response.body()?.apiMessage?:AFConstants.SOMETHING_WENT_WRONG)
                                progressObserver.postValue(false)
                            }
                        }
                    }else{
                        statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
                        progressObserver.postValue(false)
                    }



                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
        }
    }

    fun verifyOtpCall(binding: ActivityVerifyOtpBinding, mobile: String) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                //   progressObserver.postValue(true)
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    RestClient.getInstance().service.verifyOtpByMobileNumberCall(
                        EncryptionProvider(
                            VerifyCallPost(
                                mobile
                            )
                        )
                    )

                    // progressObserver.postValue(false)
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun generateOtp(mobile: String, context: Context) {
        MparticleUtils.logEvent(
            "Onboarding_OTP_RequestCall_Clicked",
            "User opts to request a call for the OTP",
            "Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_OTP_REQUEST_CALL_CLICKED),
            context
        )
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            // progressObserver.postValue(true)
            loadingObserver.postValue(true)
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {

                try {
                    val response = RestClient.getInstance().service.generateOtpForLogin(

                        EncryptionProvider(
                            GenerateLoginOtpPost(
                                mobile,
                                Utils.getDeviceID(),
                                "" + SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                    ?.getString(SharePrefsKeys.CHEQ_USER_ID)

                            )
                        )

                    )

                    if (response?.body().isUserBlocked()) {
                        errorBlockObserver.postValue(response?.body()?.blockedData)
                    } else {
                        if (response?.body()?.OTP?.length == 4) {
                            MessagingServiceUtils.sendNotification(
                                "Login OTP : ",
                                response.body()?.OTP

                            )
                            loadingObserver.postValue(false)
                            if (response.body()?.messageId != null && response.body()?.messageId?.isNotEmpty() == true) {
                                SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())!!
                                    .putString(
                                        SharePrefsKeys.MESSAGE_Id,
                                        response.body()?.messageId
                                    )
                            }
                        } else {
                            //statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
                        }
                    }
                } catch (e: Exception) {
                    statusObserver.postValue("Something went wrong")
                }

            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun otpReport(messageId: String) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                //  progressObserver.postValue(true)
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.otpReport(
                        EncryptionProvider(
                            OtpReportRequest(
                                messageId
                            )

                        )
                    )

                    if (response!!.body() != null) {
                        //   progressObserver.postValue(false)
                        smsObserver.postValue(response.body())
                    } else {

                        //  progressObserver.postValue(false)
                        smsObserver.postValue(null)

                    }

                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
        }
    }

    private fun getLastSyncTime() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.getSyncedSmsFromServer()

                    if (response?.body()?.isExist != false) {
                        SharePrefs.getInstance(MainApplication.getApplicationContext())!!.putString(
                            SharePrefsKeys.LAST_SYNC_TIME,
                            (response?.body()?.let { Utils.getDateToMilliseconds(it.smsTime) }
                                ?.plus(1000)).toString()
                        )
                    }

                    checkUserProfile()
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
        }
    }

    private fun checkUserProfile() {
        if (NavigationUtils.isProfileIncomplete()) {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.getUserProfile()

                    if (response!!.body() != null) {
                        SharePrefs.getInstance(MainApplication.getApplicationContext())
                            ?.setSharedPreferences(response.body()!!)
                        SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                            ?.getString(SharePrefsKeys.CHEQ_USER_ID).let {
                                viewModelScope.launch {
                                    Utils.setMoengageuser(
                                        MainApplication.getApplicationContext(),
                                        it ?: ""
                                    )
                                }
                            }
                    }

                    responseObserver.postValue(true)
                    progressObserver.postValue(false)
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } else {
            responseObserver.postValue(true)
            progressObserver.postValue(false)
        }
    }

    fun finartTrigger(cheqUserId: String, screen: String) {
        if (NavigationUtils.isProfileIncomplete()) {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    RestClient.getInstance().service.finartTrigger(
                        EncryptionProvider(
                            FinartRequest(
                                cheqUserId,
                                screen
                            )
                        )
                    )
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

    private fun createReferralRequest() {
        val referrerId = SharePrefsReferral.getInstance(MainApplication.getApplicationContext())
            ?.getReferralId(SharePrefsKeys.REFERRAL_KEY)
        val cheqUserId =
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.CHEQ_USER_ID)
        if (!referrerId.isNullOrEmpty()) {
            val createReferralRequest = CreateReferralRequest(
                "OTP_SUCCESS", referrerId, cheqUserId, "new user",
                "android", "whatsapp", "onboarding", "CHEQONE"
            )
            createReferral(createReferralRequest)
        } else {
            if (cheqUserId != null) {
                MparticleUtils.logReferralEvent(
                    "log_referral_payloadcheck",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_REFERRAL_PAYLOAD_CHECK),
                    mapOf(CURRENT_USER_CHEQ_ID to cheqUserId)
                )
            }
        }
    }

    private fun createReferral(createReferralRequest: CreateReferralRequest) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().referralApi.createReferral(
                        EncryptionProvider(createReferralRequest)
                    )

                    response?.body()?.let {
                        if (response.isSuccessful && response.body() != null) {
                            val jsonObject = response.body()
                            createReferralObserver.postValue(NetworkResult.Success(jsonObject!!))
                            if (jsonObject.get(AFConstants.STATUS)?.asBoolean == false) {
                                logSaveFailEvent(
                                    RestClient.getInstance().referralApi.createReferral(
                                        EncryptionProvider(createReferralRequest)
                                    )?.raw()?.request.toString(),
                                    jsonObject.get(AFConstants.API_MESSAGE)?.asString!!
                                )
                            }
                        } else if (response.errorBody() != null) {
                            val errorObj =
                                JSONObject(response.errorBody()!!.charStream().readText())
                            createReferralObserver.postValue(
                                NetworkResult.Error(
                                    errorObj.getString(
                                        AFConstants.MESSAGE
                                    )
                                )
                            )
                            logSaveFailEvent(
                                RestClient.getInstance().referralApi.createReferral(
                                    EncryptionProvider(createReferralRequest)
                                )?.raw()?.request.toString(),
                                response.errorBody()!!.charStream().readText()
                            )
                        } else {
                            createReferralObserver.postValue(NetworkResult.Error(AFConstants.SOMETHING_WENT_WRONG))
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

    private fun logSaveFailEvent(requestData: String, errorMsg: String) {
        MparticleUtils.logReferralEvent(
            "log_referral_savefail",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_REFERRAL_SAVE_FAIL),
            mapOf(
                REFERRAL_REQEUST_DATA to requestData,
                ERROR_DESCRIPTION to errorMsg
            )
        )
    }

    fun startTimeout() {
        timer = object : CountDownTimer(180000, 1000) {
            override fun onTick(p0: Long) {
                val remainingTime = p0 / 1000

                val stRemTime: String = if (remainingTime.toString().length == 1) {
                    "0$remainingTime" + "s"
                } else {
                    "$remainingTime" + "s"
                }
                timeOutObserver.postValue(false)
            }

            override fun onFinish() {
                timeOutObserver.postValue(true)
            }

        }.start()
    }
    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

}