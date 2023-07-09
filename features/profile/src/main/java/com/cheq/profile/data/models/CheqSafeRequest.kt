package com.cheq.profile.data.models

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
data class CheqSafeRequest(
    var cheqUserId: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var token: String,
    var refersh_token: String,
    var emailTokenSource: String,
    var authCode: String?
)