package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.InputUseCase
import com.cheq.profile.domain.entities.ReferralHistory

/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
interface GetReferralHistoryUseCase :
    InputUseCase<GetReferralHistoryUseCase.GetReferralHistoryInput, Result<ReferralHistory>> {
    data class GetReferralHistoryInput(
        val userId: String
    )
}