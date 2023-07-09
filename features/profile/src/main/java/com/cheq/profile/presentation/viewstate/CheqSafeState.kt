package com.cheq.profile.presentation.viewstate

import com.cheq.profile.domain.entities.EmailLinkingStatus
import com.cheq.profile.domain.entities.PersonalDetails

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
sealed class CheqSafeState {
    object Initial : CheqSafeState()
    object Intro : CheqSafeState()
    object LinkingInProcess : CheqSafeState()
    data class LinkingInitiated(
        val linkingEmail: String,
        val status: EmailLinkingStatus
    ) : CheqSafeState()
    object LinkingFailed : CheqSafeState()
    object ExitConfirmation : CheqSafeState()
    object UserConfirmation :CheqSafeState()
    data class AlreadyLinkedEmail(
        val linkedEmails: List<PersonalDetails.ActiveEmails>
    ) : CheqSafeState()
}
