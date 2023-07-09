package com.cheq.retail.ui.cheqsafe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.accountSettings.model.ActiveEmailsItem
import com.cheq.retail.ui.socialLogin.model.EmailLinkingStatus
import com.cheq.retail.ui.socialLogin.model.UserGmailDetailsPost
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheqSafeViewModel : ViewModel() {


    val view = MutableLiveData<ViewVO>(ViewVO.Initial)


    init {
        startFlow()
    }

    fun startFlow() {
        view.value = ViewVO.Initial
        fetchLinkedEmails()
    }

    private fun fetchLinkedEmails() {
        viewModelScope.launch {
            val response = RestClient.getInstance().service.getUserSettingDetails()
            val emails = (response?.body()?.activeEmails ?: emptyList()).filterNotNull()

            if (emails.isNotEmpty()) {
                view.postValue(ViewVO.AlreadyLinkedEmail(emails))
            } else {
                view.postValue(ViewVO.Intro)
            }

        }
    }

    fun notifyEmailLinkingFailed() {
        view.postValue(ViewVO.LinkingFailed)
    }

    fun userCancel(){
        view.postValue(ViewVO.UserConfirmation)
    }

    fun notifyIntro() {
        view.postValue(ViewVO.Intro)
    }

    fun linkEmailAddress(
        firstName: String,
        lastName: String,
        email: String?,
        token: String,
        authCode: String?
    ) {

        if (email == null) {
            view.postValue(ViewVO.LinkingFailed)
            return;
        }

        viewModelScope.launch {
            view.postValue(ViewVO.LinkingInProcess)
            delay(2000)

            val response = RestClient.getInstance().service
                .postUserGmailDetails(
                    EncryptionProvider(
                        UserGmailDetailsPost(
                            cheqUserId = SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                ?.getString(SharePrefsKeys.CHEQ_USER_ID) ?: "",
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            token = token,
                            emailTokenSource = "gmail",
                            refersh_token = "",
                            authCode = authCode
                        )
                    )
                )

            val linkingStatus = response?.body()?.emailLinkingStatus

//            val linkingStatus = EmailLinkingStatus.SUCCESS

            val voState = when (linkingStatus) {
                EmailLinkingStatus.IN_PROGRESS,
                EmailLinkingStatus.SUCCESS -> ViewVO.LinkingInitiated(email, linkingStatus)
                EmailLinkingStatus.FAILED, null -> ViewVO.LinkingFailed
            }

            view.postValue(voState)
        }
    }

}