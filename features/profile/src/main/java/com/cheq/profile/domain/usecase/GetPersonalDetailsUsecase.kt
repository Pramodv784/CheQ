package com.cheq.profile.domain.usecase

import com.cheq.common.usecase.NoInputUseCase
import com.cheq.profile.domain.entities.PersonalDetails


/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
interface GetPersonalDetailsUsecase : NoInputUseCase<Result<PersonalDetails?>>