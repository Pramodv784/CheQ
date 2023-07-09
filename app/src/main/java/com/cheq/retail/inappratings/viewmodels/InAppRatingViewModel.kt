package com.cheq.retail.inappratings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.RestClient
import com.cheq.retail.inappratings.models.response.Action
import com.cheq.retail.inappratings.models.uistate.AppRatingUIState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Sanket Mendon on 25,June,2023.
 * sanket@cheq.one
 */
class InAppRatingViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<AppRatingUIState> =
        MutableStateFlow(AppRatingUIState.Loading)
    val uiState: StateFlow<AppRatingUIState> = _uiState.asStateFlow()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = AppRatingUIState.Error(throwable.localizedMessage ?: "coroutine exception")
    }

    fun setUIState(action: Action?) {
        if (action != null) {
            _uiState.value = AppRatingUIState.ShowPrompt(action)
        } else {
            getRatingPrompt()
        }
    }

    private fun getRatingPrompt() {
        viewModelScope.launch(exceptionHandler) {
            val response = RestClient.getInstance().service.getRatingPrompt()
            if (response?.isSuccessful == true) {
                val responseBody = response.body()
                val showInAppRating = responseBody?.inAppRatingDiscovery?.showInAppRatingOption
                if (showInAppRating == true) {
                    val responseTemplate = responseBody.inAppRatingDiscovery.action
                    _uiState.value = AppRatingUIState.ShowPrompt(responseTemplate)
                } else {
                    _uiState.value = AppRatingUIState.NoPrompt
                }
            } else {
                onError(response?.message() ?: "error")
            }
        }
    }

    private fun onError(message: String) {
        _uiState.value = AppRatingUIState.Error(message)
    }
}
