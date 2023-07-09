package com.cheq.retail.razorpay.api

import com.cheq.retail.BuildConfig
import com.cheq.retail.application.MainApplication
import com.cheq.retail.utils.Utils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RazorpayClient {

    companion object {

        private var retrofit: Retrofit? = Retrofit.Builder()
            .baseUrl(BuildConfig.razorpayBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                    .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                        val response: Response?
                        val request = chain.request().newBuilder()
                            .header("Authorization", "Basic cnpwX3Rlc3RfM0xoRnM2UDJGMWNmN0I6NnI2MzNDeDBweXRmcjJnUmZ1RXY3MDFv")
                            .build()
                        response = chain.proceed(request)

                        response
                    })
                    .build()
            )
            .build()

        fun getInstance(): RazorPayAPIService {
            return retrofit!!.create(RazorPayAPIService::class.java)
        }
    }
}