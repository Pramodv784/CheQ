package com.cheq.network.response

import com.cheq.network.models.CheqResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * Created by Akash Khatkale on 28th May, 2023.
 * akash.k@cheq.one
 */
class NetworkResponseAdapter(
    private val type: Type,
) : CallAdapter<Any?, Call<NetworkResponse<Any?>>> {

    override fun responseType(): Type = CheqResponse::class.java

    override fun adapt(call: Call<Any?>): Call<NetworkResponse<Any?>> {
        return NetworkResponseCall(call, type)
    }
}