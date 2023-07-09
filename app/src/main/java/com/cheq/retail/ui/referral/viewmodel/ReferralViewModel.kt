package com.cheq.retail.ui.referral.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.cheq.retail.ui.main.model.ReferralStaticData
import com.cheq.retail.ui.main.model.ReferralUrlResponse
import com.cheq.retail.ui.main.model.ReferredEarnedResponse
import com.cheq.retail.ui.main.model.SendReferralRequest
import com.cheq.retail.ui.referral.repository.ReferralRepository
import com.cheq.retail.utils.NetworkResult
import com.cheq.retail.utils.ReferralHelper.getInviteHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReferralViewModel(val repository: ReferralRepository, val cheqUserId: String?, application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    val context: Context = application.applicationContext

    lateinit var linkGenerated: String
    val genLinkLiveData = MutableLiveData<String>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch { repository.getReferralEarnedAndInvites(cheqUserId) }
            launch { repository.getReferralStaticData() }
           // launch { repository.getShortReferralUrl(cheqUserId) }
        }

    }

    val referral: MutableLiveData<NetworkResult<ReferredEarnedResponse>>
        get() = repository.referral

    val refStaticdata: MutableLiveData<NetworkResult<ReferralStaticData>>
        get() = repository.refStaticData

    val refShortUrl: MutableLiveData<NetworkResult<ReferralUrlResponse>>
        get() = repository.refShortUrl

     fun generateRefLink(context: Context) {
        val linkGenerator = ShareInviteHelper.generateInviteUrl(context)
         if (cheqUserId != null) {
             linkGenerator.getInviteHelper(cheqUserId,context)
         }

        val listener2: LinkGenerator.AFa1xSDK = object : LinkGenerator.AFa1xSDK {
            override fun onResponse(s: String) {
                linkGenerated = s
                genLinkLiveData.postValue(s)
                sendReferral()
            }

            override fun onResponseError(s: String) {
            }
        }

        linkGenerator.generateLink(context, listener2)
    }

     fun sendReferral(){
        val sendReferralRequest = SendReferralRequest(cheqUserId, linkGenerated)
        viewModelScope.launch {
            repository.sendReferralUrl(sendReferralRequest)
        }
    }
}