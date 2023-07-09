package com.cheq.retail.ui.login.model

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class Map(
    val privacyPolicy: PrivacyPolicy,
    val termsAndCondition: TermsAndCondition
): Serializable