package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.InputUseCase
import com.cheq.profile.domain.entities.CheqSafeDetails

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
interface PostCheqSafeEmailUseCase :
    InputUseCase<PostCheqSafeEmailUseCase.PostCheqSafeEmailUseCaseInput, Result<CheqSafeDetails>> {
    data class PostCheqSafeEmailUseCaseInput(
        var cheqUserId: String,
        var firstName: String,
        var lastName: String,
        var email: String,
        var token: String,
        var refersh_token: String,
        var emailTokenSource: String,
        var authCode: String?
    )
}