package com.cheq.profile.data.repository

import com.cheq.network.response.map
import com.cheq.network.response.toResult
import com.cheq.profile.data.api.ReferralApi
import com.cheq.profile.data.mappers.toReferralEarnedModel
import com.cheq.profile.data.mappers.toReferralHistoryModel
import com.cheq.profile.data.mappers.toReferralStaticModel
import com.cheq.profile.data.mappers.toReferralUrlModel
import com.cheq.profile.data.models.ReferralStaticRequest
import com.cheq.profile.data.models.SendReferralRequest
import com.cheq.profile.domain.entities.ReferralEarned
import com.cheq.profile.domain.entities.ReferralHistory
import com.cheq.profile.domain.entities.ReferralStatic
import com.cheq.profile.domain.entities.ReferralUrl
import com.cheq.profile.domain.repository.ReferralRepository
import javax.inject.Inject


/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
class ReferralRepositoryImpl @Inject constructor(
    private val api: ReferralApi
) : ReferralRepository {
    override suspend fun getReferralStaticData(id: String): Result<ReferralStatic> {
        return api
            .getReferralStaticData(ReferralStaticRequest(id))
            .map { toReferralStaticModel() }
            .toResult()
    }

    override suspend fun getReferralEarnedAndInvites(id: String): Result<ReferralEarned> {
        return api
            .getReferralEarnedAndInvites(ReferralStaticRequest(id))
            .map { toReferralEarnedModel() }
            .toResult()
    }

    override suspend fun sendReferral(userId: String, url: String): Result<ReferralUrl> {
        return api
            .sendReferral(SendReferralRequest(userId, url))
            .map { toReferralUrlModel() }
            .toResult()
    }

    override suspend fun getReferralHistory(userId: String): Result<ReferralHistory> {
        return api
            .getReferralHistory(ReferralStaticRequest(userId))
            .map { toReferralHistoryModel() }
            .toResult()
    }
}