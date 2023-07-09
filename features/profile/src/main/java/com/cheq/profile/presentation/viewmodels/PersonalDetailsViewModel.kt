package com.cheq.profile.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cheq.profile.domain.entities.PersonalDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
@HiltViewModel
class PersonalDetailsViewModel @Inject constructor() : ViewModel() {
    private val _personalDetailsLive: MutableLiveData<PersonalDetails?> = MutableLiveData()
    val personalDetailsLive: LiveData<PersonalDetails?> = _personalDetailsLive
    fun setPersonalDetails(personalDetails: PersonalDetails) {
        _personalDetailsLive.postValue(personalDetails)
    }
}