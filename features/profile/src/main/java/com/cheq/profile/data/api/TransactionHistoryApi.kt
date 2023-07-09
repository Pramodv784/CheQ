package com.cheq.profile.data.api

import com.cheq.network.response.NetworkResponse
import com.cheq.profile.data.models.TransactionHistoryDetailDto
import com.cheq.profile.data.models.TransactionHistoryDetailRequest
import com.cheq.profile.data.models.TransactionHistoryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
interface TransactionHistoryApi {

    @GET("/apis/payment/paymentsV3/getTxnListV2")
    suspend fun getTransactionHistory(): NetworkResponse<TransactionHistoryDto>

    @POST("/apis/payment/paymentsV3/getTxnDetails")
    suspend fun getTransactionDetails(@Body data: TransactionHistoryDetailRequest): NetworkResponse<TransactionHistoryDetailDto>
}