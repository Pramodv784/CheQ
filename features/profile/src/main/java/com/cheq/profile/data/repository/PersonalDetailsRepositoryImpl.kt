package com.cheq.profile.data.repository

import com.cheq.network.response.map
import com.cheq.network.response.toResult
import com.cheq.profile.data.api.UserSettingsApi
import com.cheq.profile.data.mappers.toCheqSafeModel
import com.cheq.profile.data.mappers.toPersonalDetails
import com.cheq.profile.data.models.CheqSafeRequest
import com.cheq.profile.domain.entities.CheqSafeDetails
import com.cheq.profile.domain.entities.PersonalDetails
import com.cheq.profile.domain.repository.PersonalDetailsRepository
import javax.inject.Inject


/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
class PersonalDetailsRepositoryImpl @Inject constructor(
    private val userSettingsApi: UserSettingsApi
) : PersonalDetailsRepository {

    override suspend fun getPersonalDetails(): Result<PersonalDetails> {
        return userSettingsApi
            .getUserSettings()
            .map { toPersonalDetails() }
            .toResult()
    }

    override suspend fun postCheqSafeEmail(
        cheqUserId: String,
        firstName: String,
        lastName: String,
        email: String,
        token: String,
        refersh_token: String,
        emailTokenSource: String,
        authCode: String?
    ): Result<CheqSafeDetails> {
        return userSettingsApi.postUserGmailDetails(
            CheqSafeRequest(
                cheqUserId,
                firstName,
                lastName,
                email,
                token,
                refersh_token,
                emailTokenSource,
                authCode
            )
        ).map { toCheqSafeModel() }.toResult()
    }
}