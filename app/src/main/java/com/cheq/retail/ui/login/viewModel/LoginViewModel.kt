package com.cheq.retail.ui.login.viewModel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.login.model.DataGenerateOtp
import com.cheq.retail.ui.login.model.GenerateLoginOtpPost
import com.cheq.retail.ui.login.model.GetUserDetailsModel
import com.cheq.retail.ui.main.model.SmsPostModel
import com.cheq.retail.utils.MessagingServiceUtils
import com.cheq.retail.utils.SmsUtils
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val otpObserver: MutableLiveData<DataGenerateOtp> =
        MutableLiveData<DataGenerateOtp>()

    val userDetailsObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    @RequiresApi(Build.VERSION_CODES.N)
    fun generateOtp(mobileNo: String? ) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                      val response = RestClient.getInstance().service.generateOtpForLogin(

                            EncryptionProvider(GenerateLoginOtpPost(
                                mobileNo?.trim(),
                                Utils.getDeviceID(),
                                ""+SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.CHEQ_USER_ID)
                            ))

                    )

                    MessagingServiceUtils.sendNotification(
                        "Login OTP : ",
                        response?.body()?.OTP
                    )


                    SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                        ?.putString(SharePrefsKeys.MESSAGE_Id, response?.body()?.messageId)
                    otpObserver.postValue(response?.body())
                    progressObserver.postValue(false)

                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }
    }

    fun getUserDetails(mobileNo: String?,context: Context) {

        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.getUserDetailsByMobileNo(
                        EncryptionProvider(
                            GetUserDetailsModel(
                                mobileNo!!.trim()
                            )
                        )
                    )
                    SharePrefs.instance!!.setUserData(response!!.body()!!)
                   // DataStoreManager(context).saveAccessTokenToDataStore(response!!.body()!!.token)
                    getLastSyncTime()
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
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
                            (Utils.getDateToMilliseconds(response?.body()!!.smsTime) + 1000).toString()
                        )
                    }

                    readDeviceSms()
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }
    }

    //read sms list
    private fun readDeviceSms() {
        val smsList = SmsUtils.getLatestDeviceSms()

        when {
            smsList.isNotEmpty() -> {
                postSmsToServer(smsList)
            }
            else -> {
                progressObserver.postValue(false)
                userDetailsObserver.postValue(true)
            }
        }
    }

    private fun postSmsToServer(smsList: List<SmsPostModel.SmsData>) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    RestClient.getInstance().service.postSmsToServer(
                        EncryptionProvider(
                            SmsPostModel(smsList)
                        )
                    )

                    SharePrefs.getInstance(MainApplication.getApplicationContext())!!
                        .putString(SharePrefsKeys.LAST_SYNC_TIME, smsList.last().smsTime)

                    progressObserver.postValue(false)
                    userDetailsObserver.postValue(true)
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }
    }
}