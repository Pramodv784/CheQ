package com.cheq.retail.ui.socialLogin.model

import androidx.annotation.Keep

@Keep
data class UserGmailDetailsPost(
    var cheqUserId: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var token: String,
    var refersh_token: String,
    var emailTokenSource : String,
    var authCode: String?

)