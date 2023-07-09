package com.cheq.profile.domain.di

import com.cheq.profile.domain.usecase.GetPersonalDetailsUsecase
import com.cheq.profile.domain.usecase.GetReferralEarnedUseCase
import com.cheq.profile.domain.usecase.GetReferralHistoryUseCase
import com.cheq.profile.domain.usecase.GetReferralStaticUseCase
import com.cheq.profile.domain.usecase.GetTransactionHistoryDetailsUseCase
import com.cheq.profile.domain.usecase.GetTransactionHistoryUseCase
import com.cheq.profile.domain.usecase.PostCheqSafeEmailUseCase
import com.cheq.profile.domain.usecase.SendReferralUrlUseCase
import com.cheq.profile.domain.usecase.impl.GetPersonalDetailsUsecaseImpl
import com.cheq.profile.domain.usecase.impl.GetReferralEarnedUseCaseImpl
import com.cheq.profile.domain.usecase.impl.GetReferralHistoryUseCaseImpl
import com.cheq.profile.domain.usecase.impl.GetReferralStaticUseCaseImpl
import com.cheq.profile.domain.usecase.impl.GetTransactionHistoryDetailsUseCaseImpl
import com.cheq.profile.domain.usecase.impl.GetTransactionHistoryUseCaseImpl
import com.cheq.profile.domain.usecase.impl.PostCheqSafeEmailUseCaseImpl
import com.cheq.profile.domain.usecase.impl.SendReferralUrlUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * Created by Akash Khatkale on 18th May, 2023.
 * akash.k@cheq.one
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun bindPersonalDetailsUsecase(
        getTransactionHistoryUseCaseImpl: GetPersonalDetailsUsecaseImpl
    ): GetPersonalDetailsUsecase

    @Binds
    abstract fun bindTransactionHistoryUseCase(
        impl: GetTransactionHistoryUseCaseImpl
    ): GetTransactionHistoryUseCase

    @Binds
    abstract fun bindTransactionHistoryDetailsUseCase(
        impl: GetTransactionHistoryDetailsUseCaseImpl
    ): GetTransactionHistoryDetailsUseCase

    @Binds
    abstract fun bindReferralStaticUseCase(
        impl: GetReferralStaticUseCaseImpl
    ): GetReferralStaticUseCase

    @Binds
    abstract fun bindReferralEarnedUseCase(
        impl: GetReferralEarnedUseCaseImpl
    ): GetReferralEarnedUseCase

    @Binds
    abstract fun bindReferralUrlUseCase(
        impl: SendReferralUrlUseCaseImpl
    ): SendReferralUrlUseCase

    @Binds
    abstract fun bindReferralHistoryUseCase(
        impl: GetReferralHistoryUseCaseImpl
    ): GetReferralHistoryUseCase

    @Binds
    abstract fun bindCheqSafeDetailsUseCase(
        impl: PostCheqSafeEmailUseCaseImpl
    ): PostCheqSafeEmailUseCase

}