package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.ReferralUrl
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.usecase.SendReferralUrlUseCase
import javax.inject.Inject


/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
class SendReferralUrlUseCaseImpl @Inject constructor(
    private val repository: ReferralRepository
) : SendReferralUrlUseCase {
    override suspend fun invoke(input: SendReferralUrlUseCase.SendReferralUrlInput): Result<ReferralUrl?> {
        return repository.sendReferral(
            input.userId,
            input.url
        )
    }
}