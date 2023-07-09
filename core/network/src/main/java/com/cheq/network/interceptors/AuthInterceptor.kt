package com.cheq.network.interceptors

import com.cheq.cache.sharedprefs.SharedPrefs
import com.cheq.cache.sharedprefs.SharedPrefsCheQUser
import com.cheq.cache.sharedprefs.SharedPrefsConstants.ACCESS_TOKEN
import com.cheq.network.NetworkConstants.AUTHORIZATION
import com.cheq.network.NetworkConstants.BEARER
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


/**
 * Created by Sanket Mendon on 26,April,2023.
 * sanket@cheq.one
 */

class AuthInterceptor @Inject constructor(
    @SharedPrefsCheQUser private val sharedPrefs: SharedPrefs
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPrefs.getString(ACCESS_TOKEN)
        val request = chain.request().newBuilder()
        request.addHeader(AUTHORIZATION, "$BEARER$token")
        return chain.proceed(request.build())
    }
}