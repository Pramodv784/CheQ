package com.cheq.retail.ui.onboarding.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cheq.retail.R
import com.cheq.retail.ui.main.model.DemoDTO
import com.cheq.retail.ui.main.model.DemoDTOExploreVouchers
import com.cheq.retail.ui.main.model.DemoDTOOffers
import com.cheq.retail.ui.main.model.DemoDTOTopFeatured
import com.cheq.retail.ui.onboarding.model.OnBoardingItemModel

class OnBoardingViewModel : ViewModel() {
    val onBoardingItemObserver = MutableLiveData<ArrayList<OnBoardingItemModel>>()

    fun setOnBoardingItems() {
        val itemList = arrayListOf<OnBoardingItemModel>()

        itemList.add(
            OnBoardingItemModel(
                "Track all your debt\nin a single app",
                R.raw.onboarding_screen1
            )
        )
        itemList.add(
            OnBoardingItemModel(
                "Pay all credit bills\nwith a single payment",
                R.raw.onboarding_screen2
            )
        )
        itemList.add(
            OnBoardingItemModel(
                "Get real rewards on\nall bill payments",
                R.raw.onboarding_screen3
            )
        )


        onBoardingItemObserver.postValue(itemList)
    }




}