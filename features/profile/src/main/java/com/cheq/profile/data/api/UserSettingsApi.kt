package com.cheq.profile.data.api

import com.cheq.network.response.NetworkResponse
import com.cheq.profile.data.models.CheqSafeDetailsDto
import com.cheq.profile.data.models.CheqSafeRequest
import com.cheq.profile.data.models.UserSettingsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
interface UserSettingsApi {
    @GET("/apis/user/settings")
    suspend fun getUserSettings(): NetworkResponse<UserSettingsResponse>

    @POST("/apis/crawling/emailcrawl/emailToken")
    suspend fun postUserGmailDetails(@Body model: CheqSafeRequest): NetworkResponse<CheqSafeDetailsDto>
}