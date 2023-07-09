package com.cheq.retail.ui.rewards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.RestClient
import com.cheq.retail.inappratings.models.uistate.AppRatingVisibilityUIState
import com.cheq.retail.ui.main.model.RedeemCouponResponse
import com.cheq.retail.ui.main.model.RewardsDashboardResponse
import com.cheq.retail.ui.main.model.VoucherListResponse
import com.cheq.retail.ui.main.model.VoucherRedeemHistoryResponse
import com.cheq.retail.ui.main.model.request.RedeemCouponRequest
import com.cheq.retail.ui.rewards.model.*
import com.cheq.retail.ui.rewards.repository.RewardsRepository
import com.cheq.retail.ui.rewards.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardsViewModelNew(private val rewardsRepository: RewardsRepository) : ViewModel() {
    val rewardsDashboardLiveData: LiveData<State<RewardsDashboardResponse>> get() = rewardsRepository.rewardsLiveData
    val voucherListLiveData: LiveData<State<VoucherListResponse>> get() = rewardsRepository.voucherListLiveData
    val redeemVoucherLiveData: LiveData<State<RedeemCouponResponse>> get() = rewardsRepository.redeemVoucherLiveData
    val redeemHistoryLiveData: LiveData<State<VoucherRedeemHistoryResponse?>> get() = rewardsRepository.redeemVoucherHistoryLiveData
    val verifyVPALiveData: LiveData<State<DataRewardVPA3>> get() = rewardsRepository.verifyVPALiveData
    val convertToCashLiveData: LiveData<State<ConvertToCashResponse>> get() = rewardsRepository.convertToCahLiveData
    val vpaListLiveData: LiveData<State<VpaListResponse>> get() = rewardsRepository.vpaListLiveData

    private val _appRatingState = MutableStateFlow<AppRatingVisibilityUIState>(AppRatingVisibilityUIState.Loading)
    val appRatingState: StateFlow<AppRatingVisibilityUIState> = _appRatingState.asStateFlow()

    fun getRewardsDashBoard() {
        viewModelScope.launch(Dispatchers.IO) {
            rewardsRepository.getRewardDashBoard()
        }
    }

    fun getVpaList() {
        viewModelScope.launch(Dispatchers.IO) {
            rewardsRepository.getVpaList()
        }
    }

    fun convertToCash(request: ConvertToCashRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            rewardsRepository.convertToCash(request)
        }
    }

    fun verifyVPA(request: VerifyVPARequest) {
        viewModelScope.launch(Dispatchers.IO) {
            rewardsRepository.verifyVPA(request)
        }
    }

    fun getVoucherListByCatId(catId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            rewardsRepository.getVoucherListById(catId)
        }
    }

    fun redeemCoupon(request: RedeemCouponRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            rewardsRepository.redeemCoupon(request)
        }
    }

    fun getVoucherHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            rewardsRepository.redeemCouponHistory()
        }
    }

    fun getRatingPrompt() {
        viewModelScope.launch() {
            val response = RestClient.getInstance().service.getRatingPrompt()
            if (response?.isSuccessful == true) {
                val responseBody = response.body()
                val showInAppRating = responseBody?.inAppRatingDiscovery?.showInAppRatingOption
                if (showInAppRating == true) {
                    val responseTemplate = responseBody?.inAppRatingDiscovery?.action
                    _appRatingState.value = AppRatingVisibilityUIState.ShowRating(responseTemplate)
                } else {
                    _appRatingState.value = AppRatingVisibilityUIState.ShowReferral
                }
            }
        }
    }

    fun setRatingStateShown() {
        _appRatingState.value = AppRatingVisibilityUIState.RatingShown
    }
}