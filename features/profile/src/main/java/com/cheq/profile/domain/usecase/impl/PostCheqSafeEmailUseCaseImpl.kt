package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.CheqSafeDetails
import com.cheq.profile.domain.repository.PersonalDetailsRepository
import com.cheq.profile.domain.usecase.PostCheqSafeEmailUseCase
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
class PostCheqSafeEmailUseCaseImpl @Inject constructor(
    private val repository: PersonalDetailsRepository
) : PostCheqSafeEmailUseCase {
    override suspend fun invoke(input: PostCheqSafeEmailUseCase.PostCheqSafeEmailUseCaseInput): Result<CheqSafeDetails> {
        return repository.postCheqSafeEmail(
            cheqUserId = input.cheqUserId,
            firstName = input.firstName,
            lastName = input.lastName,
            emailTokenSource = input.emailTokenSource,
            refersh_token = input.refersh_token,
            token = input.token,
            email = input.email,
            authCode = input.authCode
        )
    }
}