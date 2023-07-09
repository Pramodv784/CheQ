package com.cheq.profile.data.mappers

import com.cheq.common.extension.orFalse
import com.cheq.profile.data.models.CheqSafeDetailsDto
import com.cheq.profile.domain.entities.CheqSafeDetails
import com.cheq.profile.domain.entities.EmailLinkingStatus

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
fun CheqSafeDetailsDto.toCheqSafeModel(): CheqSafeDetails =
    CheqSafeDetails(
        status = this.status.orFalse,
        message = this.message.orEmpty(),
        apiMessage = this.apiMessage.orEmpty(),
        emailLinkingStatus = EmailLinkingStatus.find(this.emailLinkingStatus.orEmpty())
    )