package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.ReferralHistory
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralHistoryUseCase
import javax.inject.Inject

class GetReferralHistoryUseCaseImpl @Inject constructor(
    private val repository: ReferralRepository
) : GetReferralHistoryUseCase {
    override suspend fun invoke(input: GetReferralHistoryUseCase.GetReferralHistoryInput): Result<ReferralHistory> {
        return repository.getReferralHistory(input.userId)
    }
}