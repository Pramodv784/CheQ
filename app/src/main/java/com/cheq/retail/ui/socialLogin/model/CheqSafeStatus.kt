package com.cheq.retail.ui.socialLogin.model

import androidx.annotation.Keep

@Keep
enum class CheqSafeStatus {
    NO_EMAIL_LINKED,
    LINKED,
    FAILED
}