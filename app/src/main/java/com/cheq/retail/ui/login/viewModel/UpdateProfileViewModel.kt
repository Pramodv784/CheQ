package com.cheq.retail.ui.login.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AppsFlyEvents.setCurrentUserForAF
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.login.model.UpdateProfilePost
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.ui.login.model.isUserBlocked
import com.cheq.retail.ui.splash.model.RequestUserConsent
import com.cheq.retail.ui.verifyOtp.model.isUserBlocked
import com.cheq.retail.utils.FreshChatUtil
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateProfileViewModel : ViewModel() {
    val responseObserver: MutableLiveData<UpdateProfileResponse> =
        MutableLiveData<UpdateProfileResponse>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()

    val errorBlockObserver: MutableLiveData<BlockData?> = MutableLiveData<BlockData?>()

    fun updateProfile(postData: UpdateProfilePost,checkUserID: String) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {

                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service.updateUserProfile(
                        EncryptionProvider(postData)
                    )
                    if (response !=  null && response.isSuccessful && response.body().isUserBlocked()) {
                        errorBlockObserver.postValue(response.body()?.blockedData)
                    } else {
                        if (response!!. isSuccessful) {
                            responseObserver.postValue(response.body()!!)

                            if (response.body()!!.apiMessage.equals(
                                    "user record updated",
                                    ignoreCase = true
                                )
                            ) {
                                SharePrefs.getInstance(MainApplication.getApplicationContext())
                                    ?.putString(
                                        SharePrefsKeys.FIRST_NAME,
                                        postData.firstName
                                    )
                                SharePrefs.getInstance(MainApplication.getApplicationContext())
                                    ?.putString(
                                        SharePrefsKeys.LAST_NAME,
                                        postData.lastName
                                    )
                                SharePrefs.getInstance(MainApplication.getApplicationContext())
                                    ?.putString(
                                        SharePrefsKeys.USER_EMAIL,
                                        postData.email
                                    )
                                setCurrentUserForAF(MainApplication.getApplicationContext(),checkUserID)

                                GlobalScope.launch {
                                    Utils.setMoengageuser(MainApplication.getApplicationContext()!!, checkUserID)

                                }
                                MparticleUtils.setCurrentUser(
                                    MainApplication.getApplicationContext(),

                                    checkUserID
                                )
                                // responseObserver.postValue(response.body()!!)

                            }
                        } else {
                            statusObserver.postValue(response.message())
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

    fun updateUserConsent(requestUserConsent: RequestUserConsent){
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {

            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {

            /*    val response = RestClient.getInstance().service.userConsent(
                    requestUserConsent
                )*/


            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }

}