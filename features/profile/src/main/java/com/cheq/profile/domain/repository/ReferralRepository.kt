package com.cheq.profile.domain.repository

import com.cheq.profile.domain.entities.ReferralEarned
import com.cheq.profile.domain.entities.ReferralHistory
import com.cheq.profile.domain.entities.ReferralStatic
import com.cheq.profile.domain.entities.ReferralUrl


/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
interface ReferralRepository {

    suspend fun getReferralStaticData(id: String): Result<ReferralStatic>

    suspend fun getReferralEarnedAndInvites(id: String): Result<ReferralEarned>

    suspend fun sendReferral(userId: String, url: String): Result<ReferralUrl>

    suspend fun getReferralHistory(userId: String): Result<ReferralHistory>

}