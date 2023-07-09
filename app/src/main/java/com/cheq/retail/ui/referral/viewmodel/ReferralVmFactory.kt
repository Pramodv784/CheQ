package com.cheq.retail.ui.referral.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.ui.referral.repository.ReferralRepository

class ReferralVmFactory(private val referralRepository: ReferralRepository,private val cheqUserId:String?, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return ReferralViewModel(referralRepository,cheqUserId, application ) as T
    }
}