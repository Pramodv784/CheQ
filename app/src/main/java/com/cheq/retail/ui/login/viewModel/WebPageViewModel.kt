package com.cheq.retail.ui.login.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.api.RestClient
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.login.model.WebPagesResponse
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebPageViewModel : ViewModel() {
    val webPageObserver: MutableLiveData<WebPagesResponse> =
        MutableLiveData<WebPagesResponse>()
    fun getWebpage( ) {
        try {
            if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
                viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                    t.printStackTrace()
                }) {
                    val response = RestClient.getInstance().service.getWebPages()
                    if (response!!.isSuccessful) {
                        webPageObserver.postValue(response.body()!!)
                    } else {
                        webPageObserver.postValue(response.body()!!)
                    }
                }
            } else {
                //   webPageObserver.postValue("Please connect to the internet!")
            }
        } catch (e: Exception) {
        }
    }
}