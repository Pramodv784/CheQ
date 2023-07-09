package com.cheq.network.api

import com.cheq.network.models.RefreshToken
import com.cheq.network.models.TokenUpdateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkApi {

    @POST("/apis/auth/v2/accessToken")
    fun accessTokenCall(@Body model: RefreshToken): Call<TokenUpdateResponse>?
}