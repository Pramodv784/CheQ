package com.cheq.profile.data.di

import com.cheq.profile.data.api.ReferralApi
import com.cheq.profile.data.api.TransactionHistoryApi
import com.cheq.profile.data.api.UserSettingsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): TransactionHistoryApi =
        retrofit.create(TransactionHistoryApi::class.java)

    @Provides
    @Singleton
    fun provideUserSettingsApi(retrofit: Retrofit): UserSettingsApi =
        retrofit.create(UserSettingsApi::class.java)

    @Provides
    @Singleton
    fun provideReferralApi(retrofit: Retrofit): ReferralApi =
        retrofit.create(ReferralApi::class.java)
}