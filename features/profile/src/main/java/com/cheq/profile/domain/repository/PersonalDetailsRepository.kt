package com.cheq.profile.domain.repository

import com.cheq.profile.domain.entities.CheqSafeDetails
import com.cheq.profile.domain.entities.PersonalDetails

/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
interface PersonalDetailsRepository {
    suspend fun getPersonalDetails(): Result<PersonalDetails>

    suspend fun postCheqSafeEmail(
        cheqUserId: String,
        firstName: String,
        lastName: String,
        email: String,
        token: String,
        refersh_token: String,
        emailTokenSource: String,
        authCode: String?
    ): Result<CheqSafeDetails>
}