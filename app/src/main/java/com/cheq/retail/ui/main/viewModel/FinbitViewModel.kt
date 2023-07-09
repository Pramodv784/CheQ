package com.cheq.retail.ui.main.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.cheq.retail.api.APIClient.client
import com.cheq.retail.api.APIServices
import com.cheq.retail.application.MainApplication
import com.cheq.retail.ui.main.model.finbit.FinbitResponse
import com.cheq.retail.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FinbitViewModel : ViewModel() {
    val statusObserver: MutableLiveData<String> =
        MutableLiveData<String>()
    val finbitObserver: MutableLiveData<Response<FinbitResponse?>> =
        MutableLiveData<Response<FinbitResponse?>>()
    fun callFinBitApi() {
        if (Utils.isNetworkAvailable(MainApplication.getApplicationContext())) {
            viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
                t.printStackTrace()
            }) {

                var apiInterface = client?.create(APIServices::class.java)
//                val call2: Call<FinbitResponse> = apiInterface!!.callFinbitJWTTokenApi("vaibhav@cheq.one", "C9@By#Kt7aYu")
//                call2.enqueue(object : Callback<FinbitResponse?> {
//                    override fun onResponse(
//                        call: Call<FinbitResponse?>?,
//                        response: Response<FinbitResponse?>
//                    ) {
//                        Log.d("finbitReponse", "" + response.body())
//                        if (response!=null) {
//                            finbitObserver.postValue(response)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<FinbitResponse?>, t: Throwable?) {
//                        call.cancel()
//                    }
//                })


            }
        } else {
            statusObserver.postValue("Please connect to the internet!")
        }
    }
}