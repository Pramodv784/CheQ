package com.cheq.retail.ui.login.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.application.MainApplication
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.constants.AppsFlyEvents.setCurrentUserForAF
import com.cheq.retail.sharePreferences.SharePrefCheqUserId
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.ui.login.model.PanDetailsPostModel
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.ui.login.model.isUserBlocked
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetPanCardViewModel : ViewModel() {
    val responseObserver: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val statusObserver: MutableLiveData<String> = MutableLiveData<String>()
    val failureObserver: MutableLiveData<String> = MutableLiveData<String>()

    val progressObserver: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val creditResponse: MutableLiveData<UpdateProfileResponse> =
        MutableLiveData<UpdateProfileResponse>()

    val errorBlockObserver: MutableLiveData<BlockData?> = MutableLiveData<BlockData?>()

    fun updateProfile(dob: String, pan: String) {

        when {
            /* binding.etPan.text.isNullOrEmpty() -> {
                 statusObserver.postValue("Please enter pan number!")
                 return
             }
             binding.etPan.text.length != 10 -> {
                 statusObserver.postValue("Invalid pan number!")
                 return
             }
 */
            else -> {

                try {
                    if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                        progressObserver.postValue(true)
                        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                            t.printStackTrace()
                        }) {
                            val postData = PanDetailsPostModel(
                                dob, pan
                            )
                            val response = RestClient.getInstance().service.updateUserPanDetails(
                                EncryptionProvider(postData)
                            )
                            if (response != null && response.isSuccessful && response.body().isUserBlocked()) {
                                errorBlockObserver.postValue(response.body()?.blockedData)
                            } else {
                                if (response?.body()?.status == true && response.body()?.creditResponse != null) {

                                    creditResponse.postValue(response.body())
                                    val cheqUserId =
                                        SharePrefCheqUserId.getInstance(MainApplication.getApplicationContext())
                                            ?.getString(SharePrefsKeys.CHEQ_USER_ID)
                                    cheqUserId?.let { it1 ->
                                        setCurrentUserForAF(
                                            MainApplication.getApplicationContext(),
                                            it1
                                        )

                                        MparticleUtils.setCurrentUser(
                                            MainApplication.getApplicationContext(),

                                            it1
                                        )
                                    }

                                } else {
                                    failureObserver.postValue(response?.body()?.apiMessage?:AFConstants.SOMETHING_WENT_WRONG)
                                    progressObserver.postValue(false)
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
        }

    }

}