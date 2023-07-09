package com.cheq.profile.data.mappers

import com.cheq.profile.data.models.UserSettingsResponse
import com.cheq.profile.domain.entities.EmailLinkingStatus
import com.cheq.profile.domain.entities.PersonalDetails


/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
internal fun UserSettingsResponse.toPersonalDetails() = PersonalDetails(
    firstName = this.firstName.orEmpty(),
    lastName = this.lastName.orEmpty(),
    mobile = this.mobile.orEmpty(),
    email = this.email.orEmpty(),
    activeEmails = this.activeEmails?.map {
        PersonalDetails.ActiveEmails(
            id = it?.id.orEmpty(),
            emailLinkingStatus = EmailLinkingStatus.find(it?.emailLinkingStatus.orEmpty()),
            email = it?.email.orEmpty(),
            firstName = it?.firstName.orEmpty(),
            lastName = it?.lastName.orEmpty(),
        )
    } ?: emptyList()
)