package com.cheq.retail.ui.login.model

import androidx.annotation.Keep
import java.io.Serializable
@Keep
data class UpdateProfilePost(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var whatsAppAccess: Boolean? = null
) : Serializable