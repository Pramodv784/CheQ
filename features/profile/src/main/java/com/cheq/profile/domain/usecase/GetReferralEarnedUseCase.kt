package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.InputUseCase
import com.cheq.profile.domain.entities.ReferralEarned

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
interface GetReferralEarnedUseCase: InputUseCase<GetReferralEarnedUseCase.GetReferralEarnedInput, Result<ReferralEarned?>> {
    data class GetReferralEarnedInput(
        val userId: String
    )
}