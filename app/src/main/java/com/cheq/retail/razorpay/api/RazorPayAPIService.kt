package com.cheq.retail.razorpay.api

import com.cheq.retail.ui.main.model.RazorpayCardDetailsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RazorPayAPIService {
    @GET("/v1/iins/{number}")
    suspend fun fetchCardDetails(@Path("number") number: String): Response<RazorpayCardDetailsModel>?
}