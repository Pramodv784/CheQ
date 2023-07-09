package com.cheq.profile.data.api

import com.cheq.network.response.NetworkResponse
import com.cheq.profile.data.models.ReferralEarnedDto
import com.cheq.profile.data.models.ReferralHistoryDto
import com.cheq.profile.data.models.ReferralStaticDto
import com.cheq.profile.data.models.ReferralStaticRequest
import com.cheq.profile.data.models.ReferralUrlDto
import com.cheq.profile.data.models.SendReferralRequest
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
interface ReferralApi {

    @POST("apis/user/referral/referralStaticData")
    suspend fun getReferralStaticData(@Body body: ReferralStaticRequest): NetworkResponse<ReferralStaticDto>

    @POST("apis/user/referral/getReferralEarnedAndInvites")
    suspend fun getReferralEarnedAndInvites(@Body body: ReferralStaticRequest): NetworkResponse<ReferralEarnedDto>

    @POST("apis/user/referral/sendReferralUrl")
    suspend fun sendReferral(@Body model: SendReferralRequest): NetworkResponse<ReferralUrlDto>

    @POST("apis/user/referral/getReferralHistory")
    suspend fun getReferralHistory(@Body body: ReferralStaticRequest): NetworkResponse<ReferralHistoryDto>

}