package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.ReferralEarned
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralEarnedUseCase
import javax.inject.Inject


/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
class GetReferralEarnedUseCaseImpl @Inject constructor(
    private val repository: ReferralRepository
) : GetReferralEarnedUseCase {
    override suspend fun invoke(input: GetReferralEarnedUseCase.GetReferralEarnedInput): Result<ReferralEarned?> {
        return repository.getReferralEarnedAndInvites(
            input.userId
        )
    }
}