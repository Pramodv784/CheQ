package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.ReferralStatic
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.GetReferralStaticUseCase
import javax.inject.Inject


/**
 * Created by Akash Khatkale on 20th May, 2023.
 * akash.k@cheq.one
 */
class GetReferralStaticUseCaseImpl @Inject constructor(
    private val repository: ReferralRepository
) : GetReferralStaticUseCase {
    override suspend fun invoke(input: GetReferralStaticUseCase.GetReferralStaticInput): Result<ReferralStatic> {
        return repository.getReferralStaticData(
            input.userId
        )
    }
}