package com.cheq.profile.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.common.extension.empty
import com.cheq.profile.domain.entities.EmailLinkingStatus
import com.cheq.profile.domain.entities.EmailTokenSource
import com.cheq.profile.domain.entities.PersonalDetails
import com.cheq.profile.domain.usecase.GetPersonalDetailsUsecase
import com.cheq.profile.domain.usecase.PostCheqSafeEmailUseCase
import com.cheq.profile.presentation.viewstate.CheqSafeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sanket Mendon on 23,May,2023.
 * sanket@cheq.one
 */
@HiltViewModel
class MyAccountViewModel @Inject constructor(
    private val getPersonalDetailsUsecase: GetPersonalDetailsUsecase,
    private val postCheqSafeEmailUseCase: PostCheqSafeEmailUseCase
) : ViewModel() {
    private var personalDetails: PersonalDetails? = null
    private val _userInitialsLiveData: MutableLiveData<PersonalDetails?> = MutableLiveData()
    val userInitialsStateLiveData: LiveData<PersonalDetails?> = _userInitialsLiveData

    private val _cheqSafeState: MutableLiveData<CheqSafeState> = MutableLiveData()
    val cheqSafeState: LiveData<CheqSafeState> = _cheqSafeState

    private val _initialCheqSafeState: MutableLiveData<CheqSafeState> = MutableLiveData()
    val initialCheqSafeState: LiveData<CheqSafeState> = _initialCheqSafeState

    fun fetchUserSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            getPersonalDetailsUsecase.invoke().onSuccess { personalDetails ->
                personalDetails?.let {
                    _userInitialsLiveData.postValue(it)
                    setPersonalDetails(it)
                    fetchLinkedEmails(it)
                }
            }
        }
    }

    private fun setPersonalDetails(details: PersonalDetails?) {
        details?.let { this.personalDetails = it }
    }

    fun getPersonalDetails() = this.personalDetails

    fun notifyEmailLinkingFailed() {
        _cheqSafeState.postValue(CheqSafeState.LinkingFailed)
    }

    fun userCancel(){
        _cheqSafeState.postValue(CheqSafeState.UserConfirmation)
    }

    fun notifyEnableCheqSafe() {
        _cheqSafeState.postValue(CheqSafeState.Intro)
    }

    private fun fetchLinkedEmails(personalDetails: PersonalDetails) = viewModelScope.launch {
        if (personalDetails.activeEmails.isNotEmpty()) {
            _initialCheqSafeState.postValue(CheqSafeState.AlreadyLinkedEmail(personalDetails.activeEmails))
        } else {
            _initialCheqSafeState.postValue(CheqSafeState.Intro)
        }
    }

    fun linkEmailAddress(
        userId: String,
        firstName: String,
        lastName: String,
        email: String?,
        token: String,
        authCode: String?,
        onFailure: () -> Unit = {},
        onSuccess: (state: CheqSafeState) -> Unit = {}
    ) {
        if (email == null) {
            _cheqSafeState.postValue(CheqSafeState.LinkingFailed)
            return;
        }

        viewModelScope.launch {
            _cheqSafeState.postValue(CheqSafeState.LinkingInProcess)
            delay(2000)
            val response = postCheqSafeEmailUseCase.invoke(
                PostCheqSafeEmailUseCase.PostCheqSafeEmailUseCaseInput(
                    cheqUserId = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    token = token,
                    emailTokenSource = EmailTokenSource.GOOGLE.value,
                    refersh_token = String.empty,
                    authCode = authCode
                )
            )
            response
                .onSuccess {
                    it.let { data ->
                        when (val linkingStatus = data.emailLinkingStatus) {
                            EmailLinkingStatus.IN_PROGRESS,
                            EmailLinkingStatus.SUCCESS -> _cheqSafeState.postValue(CheqSafeState.LinkingInitiated(email, linkingStatus))
                            EmailLinkingStatus.FAILED -> _cheqSafeState.postValue(CheqSafeState.LinkingFailed)
                        }
                    }
                }
                .onFailure {
                    error(it)
                }
        }
    }
}