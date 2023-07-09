package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.InputUseCase
import com.cheq.profile.domain.entities.ReferralStatic


/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
interface GetReferralStaticUseCase :
    InputUseCase<GetReferralStaticUseCase.GetReferralStaticInput, Result<ReferralStatic>> {
    data class GetReferralStaticInput(
        val userId: String
    )
}