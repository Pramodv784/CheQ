package com.cheq.retail.ui.cheqsafe

import com.cheq.retail.ui.accountSettings.model.ActiveEmailsItem
import com.cheq.retail.ui.socialLogin.model.EmailLinkingStatus

sealed class ViewVO {
    object Initial : ViewVO()
    object Intro : ViewVO()
    object LinkingInProcess : ViewVO()
    data class LinkingInitiated(val linkingEmail: String, val status: EmailLinkingStatus) : ViewVO()
    object LinkingFailed : ViewVO()
    object ExitConfirmation : ViewVO()
    object UserConfirmation :ViewVO()

    data class AlreadyLinkedEmail(val linkedEmails: List<ActiveEmailsItem>) : ViewVO()
}