package com.cheq.retail.api

import com.cheq.retail.ui.main.model.*
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReferralAPI {
    @POST("apis/user/referral/createReferral")
    suspend fun createReferral(@Body model: EncryptionProvider): Response<JsonObject>?

    @POST("apis/user/referral/sendReferralUrl")
    suspend fun sendReferral(@Body model: EncryptionProvider): Response<ReferralUrlResponse>?

    @POST("apis/user/referral/checkShortUrl")
    suspend fun getShortReferralUrl(@Body model: EncryptionProvider): Response<ReferralUrlResponse>

    @POST("apis/user/referral/getReferralEarnedAndInvites")
    suspend fun getReferralEarnedAndInvites(@Body model: EncryptionProvider): Response<ReferredEarnedResponse>

    @POST("apis/user/referral/referralStaticData")
    suspend fun getReferralStaticData(@Body model: EncryptionProvider): Response<ReferralStaticData>

    @POST("apis/user/referral/getReferralHistory")
    suspend fun getReferralHistory(@Body model: EncryptionProvider): Response<ReferralHistory>

}