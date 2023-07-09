package com.cheq.profile.data.di

import com.cheq.profile.data.repository.PersonalDetailsRepositoryImpl
import com.cheq.profile.data.repository.ReferralRepositoryImpl
import com.cheq.profile.data.repository.TransactionHistoryRepositoryImpl
import com.cheq.profile.domain.repository.PersonalDetailsRepository
import com.cheq.profile.domain.repository.ReferralRepository
import com.cheq.profile.domain.repository.TransactionHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providePersonalDetailsRepository(
        personalDetailsRepositoryImpl: PersonalDetailsRepositoryImpl
    ): PersonalDetailsRepository

    @Binds
    abstract fun provideTransactionHistoryRepository(
        impl: TransactionHistoryRepositoryImpl
    ): TransactionHistoryRepository

    @Binds
    abstract fun provideReferralRepository(
        impl: ReferralRepositoryImpl
    ): ReferralRepository

}