package com.cheq.retail.ui.splash.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsKeys.Companion.IS_BASE_URLS_AVAIL
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.login.model.RequestOTPConsent
import com.cheq.retail.ui.login.model.RequestUserConsent
import com.cheq.retail.ui.main.model.CompanyConstant
import com.cheq.retail.ui.splash.model.*
import com.cheq.retail.ui.verifyOtp.model.FinartRequest
import com.cheq.retail.utils.NavigationUtils
import com.cheq.retail.utils.Utils
import com.cheq.retail.workmanager.RefreshTokenWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SplashViewModel() : ViewModel() {
    val responseObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val accessTokenObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val successObserver: MutableLiveData<DataCheq> =
        MutableLiveData<DataCheq>()

    val companyConstantObserver: MutableLiveData<CompanyConstant> =
        MutableLiveData<CompanyConstant>()

    val isLoggedIn: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    init {
        //getLogEvents()
        getDynamicConstant()
    }

    fun fetchUserProfileDetails() {
        if (isUserLoggedIn()) {
            getUserProfileAsync()
        } else {
            getUserByDeviceIdAsync()
        }
    }

    private fun isNetworkAvailable() : Boolean {
        return Utils.isNetworkAvailable(MainApplication.getApplicationContext())
    }

    private fun isUserLoggedIn(): Boolean {
        return SharePrefs.getInstance(MainApplication.getApplicationContext())
            ?.getBoolean(SharePrefsKeys.IS_LOGGED_IN) == true
    }

    fun finartTriggerLog(cheqUserId: String, screen: String) {
        if (isNetworkAvailable()) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                RestClient.getInstance().service.finartTrigger(
                    EncryptionProvider(
                        FinartRequest(cheqUserId, screen)
                    )
                )
            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }

    private fun getUserByDeviceIdAsync() {
        if (isNetworkAvailable()) {
            viewModelScope.launch {
                val result = withContext(Dispatchers.IO) {
                    RestClient.getInstance().service.getExistingDeviceId(
                        EncryptionProvider(GetDeviceIdPost(Utils.getDeviceID()))
                    )
                }

                    result?.body()?.also {
                        SharePrefs.getInstance(MainApplication.getApplicationContext())
                            ?.setSharedPreferences(it)
                    }

            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }

    private fun getUserProfileAsync() {
        if (isNetworkAvailable()) {
            viewModelScope.launch {
                val result = withContext(Dispatchers.IO) {
                    RestClient.getInstance().service.getUserProfile()
                }
                    result?.body()?.let { response ->
                        if (response.isValid()) {
                            SharePrefs.getInstance(MainApplication.getApplicationContext())
                                ?.setSharedPreferences(response)
                        }
                    }
            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }

    fun getAccessToken() {
        try {
            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                ?.getString(SharePrefsKeys.REFRESH_TOKEN)?.let { RefreshToken(it) }
                ?.let {
                    RestClient.getInstance().service.accessToken(EncryptionProvider(it))?.subscribeOn(Schedulers.io())
                }?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<TokenUpdateResponse> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(it: TokenUpdateResponse) {
                        try {
                            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                ?.putString(SharePrefsKeys.ACCESS_TOKEN, it.accessToken)
                            SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                ?.putString(SharePrefsKeys.REFRESH_TOKEN, it.refreshToken)

                            ListenableWorker.Result.success()


                        } catch (e: Exception) {
                            ListenableWorker.Result.retry()
                        }
                    }

                    override fun onError(e: Throwable) {
                        ListenableWorker.Result.retry()
                    }
                })
        } catch (e: Exception) {
            ListenableWorker.Result.retry()
        }
    }

    fun authenticateAccessToken() {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    progressObserver.postValue(true)
                    val response =
                        RestClient.getInstance().service.validateAccessToken(

                           EncryptionProvider( ValidateAccessToken(""))

                        )

                    progressObserver.postValue(false)
                    // generateOrderObserver.postValue(response!!.body())
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue(AFConstants.SOMETHING_WENT_WRONG)
        }

    }

    fun checkUserProfile() {
        try {
            if (NavigationUtils.isProfileIncomplete()) {
                if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                    viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                        statusObserver.postValue("Something went wrong")
                        t.printStackTrace()
                    }) {

                        val response = RestClient.getInstance().service.getUserProfile()

                        val responseBody = response?.body()

                        if (responseBody != null && responseBody.isValid()) {
                            SharePrefs.getInstance(MainApplication.getApplicationContext())
                                ?.setSharedPreferences(responseBody)
                        }

                        responseObserver.postValue(true)
                    }
                } else {
                    statusObserver.postValue("Please connect to the internet!")
                }
            } else {
                viewModelScope.launch {
                    if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                        val response = RestClient.getInstance().service.getUserProfile()
                        response?.body()?.let { result ->
                            if (result.isValid()) {
                                SharePrefs.getInstance(MainApplication.getApplicationContext())
                                    ?.setSharedPreferences(result)
                            }
                        }

                        responseObserver.postValue(true)
                    } else {
                        statusObserver.postValue("Please connect to the internet!")
                    }

                }
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }
    }


    fun checkCheqUser() {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                try {
                    val response = RestClient.getInstance().service.createCheqUser(
                      EncryptionProvider(  CreatCheqUserRequest(Utils.getDeviceID()))
                    )

                    val responseBody = response?.body()

                    val userId = responseBody
                        ?.user
                        ?.cheqUserId;
                    if (userId != null) {
                        SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                            ?.putString(
                                SharePrefsKeys.CHEQ_USER_ID,
                                userId
                            )
                    }
                } catch (e: Exception) {
                    statusObserver.postValue("Something  went wrong")
                }
            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }

    }


    fun getLogEvents() {
        if (NavigationUtils.isProfileIncomplete()) {
            try {
                if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                    viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->

                    }) {
                        val response = RestClient.getInstance().service.getLogEvents()
                        if (response?.body() != null && response.body()?.event != null ) {
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.FETCHING_DETAIL,
                                    response.body()?.event?.event?.fetching_details
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.CVP_ONE,
                                    response.body()?.event?.event?.CVP_one
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.SUCCESS,
                                    response.body()?.event?.event?.success
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.CVP_THREE,
                                    response.body()?.event?.event?.CVP_three
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.CVP_TWO,
                                    response.body()?.event?.event?.CVP_two
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.CONSENTS,
                                    response.body()?.event?.event?.consents
                                )
                            if (response.body()?.event?.event?.login != null) {
                                SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                    ?.putLogBoolean(
                                        SharePrefsKeys.LOGIN,
                                        response.body()?.event?.event?.login
                                    )
                            }
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_MOBILE_NUMBER,
                                    response.body()?.event?.event?.mobile_number
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.PERMISSIONS,
                                    response.body()?.event?.event?.permissions
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.NO_PRODUCT_FOUND,
                                    response.body()?.event?.event?.no_products_found
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.PERSONAL_DETAILS,
                                    response.body()?.event?.event?.personal_details
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.PERSONAL_DETAILS_ADDITIONAL,
                                    response.body()?.event?.event?.personal_details_additional
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.SPLASH_SCREEN,
                                    response.body()?.event?.event?.splashscreen
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.VERIFY_OTP,
                                    response.body()?.event?.event?.verify_otp
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_ADD_NEW_CLICKED,
                                    response.body()?.event?.event?.Onboarding_Add_New_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_BUREAU,
                                    response.body()?.event?.event?.Onboarding_BureauConsent_Checkbox_Selected
                                )
                            if (response.body()?.event?.event?.Onboarding_CVP_One_Clicked != null) {
                                SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                    ?.putLogBoolean(
                                        SharePrefsKeys.OB_CVP_ONE_CLICK,
                                        response.body()?.event?.event?.Onboarding_CVP_One_Clicked
                                    )
                            } else {
                            }
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_CVP_ONE_SKIPPED,
                                    response.body()?.event?.event?.Onboarding_CVP_One_Skipped
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_CVP_THREE_BACK_CLICKED,
                                    response.body()?.event?.event?.Onboarding_CVP_Three_BackClicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_CVP_THREE_CLICKED,
                                    response.body()?.event?.event?.Onboarding_CVP_Three_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_CVP_TWO_BACK_CLICKED,
                                    response.body()?.event?.event?.Onboarding_CVP_Two_BackClicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_CVP_TWO_CLICKED,
                                    response.body()?.event?.event?.Onboarding_CVP_Two_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_CVP_TWO_SKIPPED,
                                    response.body()?.event?.event?.Onboarding_CVP_Two_Skipped
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_CONSENT_CLICKED,
                                    response.body()?.event?.event?.Onboarding_Consents_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_LOGIN_CLICKED,
                                    response.body()?.event?.event?.Onboarding_Login_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_LOGIN_AUTH_ENTERED,
                                    response.body()?.event?.event?.Onboarding_Login_Auth_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_LOGIN_AUTH_VIEWED,
                                    response.body()?.event?.event?.Onboarding_Login_Auth_Viewed
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_LOGIN_DIFFERENT_USER_CLICKED,
                                    response.body()?.event?.event?.Onboarding_Login_DifferentUser_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_MOBILE_NUMBER_CLICKED,
                                    response.body()?.event?.event?.Onboarding_Mobile_Number_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_MOBILE_NUMBER_ENTERED,
                                    response.body()?.event?.event?.Onboarding_Mobile_Number_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_MOBILE_NUMBER_SELECTED,
                                    response.body()?.event?.event?.Onboarding_Mobile_Number_Selected
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_NOTIFICATION_PERMISSION_ALLOWED,
                                    response.body()?.event?.event?.Onboarding_Notification_Permission_Allowed
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_NOTIFICATION_PERMISSION_DENIED,
                                    response.body()?.event?.event?.Onboarding_Notification_Permission_Denied
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_OTP_CLICKED,
                                    response.body()?.event?.event?.Onboarding_OTP_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_OTP_EDIT_BACK_CLICKED,
                                    response.body()?.event?.event?.Onboarding_OTP_Edit_BackClicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_OTP_ENTERED,
                                    response.body()?.event?.event?.Onboarding_OTP_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_OTP_REQUEST_CALL_CLICKED,
                                    response.body()?.event?.event?.Onboarding_OTP_RequestCall_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_OTP_RESEND_CLICKED,
                                    response.body()?.event?.event?.Onboarding_OTP_Resend_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_OTP_TIMEOUT_VIEWED,
                                    response.body()?.event?.event?.Onboarding_OTP_Timeout_Viewed
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERMISSION_CLICKED,
                                    response.body()?.event?.event?.Onboarding_Permissions_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_ADDITIONAL_CLICKED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_Additional_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_CLICKED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_DOB_ENTERED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_DOB_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_EMAIL_CLICKED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_Email_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_EMAIL_ENTERED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_Email_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_FNAME_CLICKED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_FName_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_FNAME_ENTERED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_FName_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_LNAME_CLICKED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_LName_Clicked
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_LNAME_ENTERED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_LName_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PERSONAL_DETAIL_PAN_ENTERED,
                                    response.body()?.event?.event?.Onboarding_PersonalDetails_PAN_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PHONE_PERMISSION_ALLOWED,
                                    response.body()?.event?.event?.Onboarding_Phone_Permission_Allowed
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_PHONE_PERMISSION_DENIED,
                                    response.body()?.event?.event?.Onboarding_Phone_Permission_Denied
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_SMS_PERMISSION_ALLOWED,
                                    response.body()?.event?.event?.Onboarding_SMS_Permission_Allowed
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_SMS_PERMISSION_DENIED,
                                    response.body()?.event?.event?.Onboarding_SMS_Permission_Denied
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_SUCCESS_CONTINUE,
                                    response.body()?.event?.event?.Onboarding_Success_Continue
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_WHATSAPP_CONSENT_CHECKBOX_DESELECTED,
                                    response.body()?.event?.event?.Onboarding_WhatsAppConsent_Checkbox_Deselected
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.OB_WHATSAPP_CONSENT_CHECKBOX_SELECTED,
                                    response.body()?.event?.event?.Onboarding_WhatsAppConsent_Checkbox_Selected
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.CC_Activation_Card_Number_Entered,
                                    response.body()?.event?.event?.CC_Activation_CardNumber_Entered
                                )
                            SharePrefsLog.getInstance(MainApplication.getApplicationContext())
                                ?.putLogBoolean(
                                    SharePrefsKeys.CC_Activation_Card_Number_Entered,
                                    response.body()?.event?.event?.CC_Activation_CardNumber_Entered
                                )
                            progressObserver.postValue(true)

                        } else {
                            progressObserver.postValue(false)
                        }

                        //startNavigation()
                    }
                } else {
                    statusObserver.postValue("Please connect to the internet!")
                }
            } catch (e: Exception) {
                statusObserver.postValue("Something went wrong")
            }
        } else {
            //startNavigation()
        }
    }

    fun checkUserByDeviceId() {
        try {
            if (NavigationUtils.isProfileIncomplete()) {
                if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                    viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                        t.printStackTrace()
                    }) {

                        val response = RestClient.getInstance().service.getExistingDeviceId(
                            EncryptionProvider(GetDeviceIdPost(Utils.getDeviceID()))
                        )

                        if (response!!.body()?.mobile != null && response.body()?.firstName != null && response.body()?.lastName != null) {
                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .clearSharePrefs()

                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .putString(SharePrefsKeys.MOBILE_NUMBER, response.body()?.mobile)
                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .putString(SharePrefsKeys.FIRST_NAME, response.body()?.firstName)
                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .putString(SharePrefsKeys.LAST_NAME, response.body()?.lastName)
                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .putString(SharePrefsKeys.LAST_NAME, response.body()?.lastName)
                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .putBoolean(SharePrefsKeys.QUICK_LOGIN_AVAILABLE, true)

                        } else {
                            SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                                .clearSharePrefs()
                        }
                        responseObserver.postValue(true)

                    }
                } else {
                    statusObserver.postValue("Please connect to the internet!")
                }
            } else {
                responseObserver.postValue(true)

            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }
    }

    // / private fun startNavigation() {
    //  Handler(Looper.getMainLooper()).postDelayed({
    //      responseObserver.postValue(true)
    //  }, 500)
    //  }

    fun updateUserConsent(requestUserConsent: RequestUserConsent) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.userConsent(
                        EncryptionProvider(requestUserConsent)
                    )


                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }
    }

    fun updateUserOTPConsent(requestUserConsent: RequestOTPConsent) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.userConsentOTP(
                       EncryptionProvider( requestUserConsent)
                    )


                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong!")
        }
    }

    private fun getDynamicConstant() {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {
                val response = RestClient.getInstance().service.getCompanyConstants()

                if (response?.body() != null) {
                    companyConstantObserver.postValue(response.body())
                }
            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }

    private fun ProfileDetailsResponse.isValid(): Boolean =
        status == true && apiMessage.equals("Success")

}