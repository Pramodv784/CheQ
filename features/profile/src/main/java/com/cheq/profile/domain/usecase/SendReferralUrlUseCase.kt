package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.InputUseCase
import com.cheq.profile.domain.entities.ReferralUrl


/**
 * Created by Akash Khatkale on 22nd May, 2023.
 * akash.k@cheq.one
 */
interface SendReferralUrlUseCase :
    InputUseCase<SendReferralUrlUseCase.SendReferralUrlInput, Result<ReferralUrl?>> {
    data class SendReferralUrlInput(
        val userId: String,
        val url: String
    )
}