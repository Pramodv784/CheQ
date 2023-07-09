package com.cheq.profile.domain.usecase.impl

import com.cheq.profile.domain.entities.PersonalDetails
import com.cheq.profile.domain.repository.PersonalDetailsRepository
import com.cheq.profile.domain.usecase.GetPersonalDetailsUsecase
import javax.inject.Inject


/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */

class GetPersonalDetailsUsecaseImpl @Inject constructor(
    private val repository: PersonalDetailsRepository
) : GetPersonalDetailsUsecase {
    override suspend fun invoke(): Result<PersonalDetails?> {
        return repository.getPersonalDetails()
    }
}