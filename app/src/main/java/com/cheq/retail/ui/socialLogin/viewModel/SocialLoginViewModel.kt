package com.cheq.retail.ui.socialLogin.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.application.MainApplication
import com.cheq.retail.api.RestClient
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.socialLogin.model.EmailCrawlingResponse
import com.cheq.retail.ui.socialLogin.model.EmailLinkingStatus
import com.cheq.retail.ui.socialLogin.model.UserGmailDetailsPost
import com.cheq.retail.ui.socialLogin.model.UserGmailDetailsResponse
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SocialLoginViewModel : ViewModel() {
    val responseObserver: MutableLiveData<EmailLinkingStatus> =
        MutableLiveData<EmailLinkingStatus>()

    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()

    val progressObserver: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    val emailcrawlingObserver:MutableLiveData<UserGmailDetailsResponse> = MutableLiveData<UserGmailDetailsResponse>()


    val crawlingValueObserver: MutableLiveData<Int> = MutableLiveData<Int>()

    fun postUserDetails(
        firstName: String,
        lastName: String,
        email: String,
        token: String,
        tokenSource: String,
        refreshToken: String
    ) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                progressObserver.postValue(true)
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {

                    val response = RestClient.getInstance().service
                        .postUserGmailDetails(
                            EncryptionProvider(
                                UserGmailDetailsPost(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    token = token,
                                    emailTokenSource = tokenSource,
                                    refersh_token = refreshToken,
                                    authCode = null,
                                    cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                        ?.getString(SharePrefsKeys.CHEQ_USER_ID) ?: ""
                                )
                            )
                        )

                    progressObserver.postValue(false)
                    responseObserver.postValue(response?.body()?.emailLinkingStatus)
                  //  responseObserver.postValue(true)
                  /*  if (response!!.code()==201) {
                         //emailcrawlingObserver.postValue(response.body()!!)
                        //crawlingValueObserver.postValue(response.body()!!.parsedEmails)
                        responseObserver.postValue(true)
                    } else {
                      //  statusObserver.postValue(response.message())
                        responseObserver.postValue(false)
                        //crawlingValueObserver.postValue(response.body()!!)
                        //emailcrawlingObserver.postValue(response.body()!!)
                    }*/
                }
            } else {
                statusObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
            statusObserver.postValue("Something went wrong")
        }
    }

}