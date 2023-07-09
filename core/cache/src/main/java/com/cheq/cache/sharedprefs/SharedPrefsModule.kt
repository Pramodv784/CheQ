package com.cheq.cache.sharedprefs

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefsModule {

    @Singleton
    @Provides
    @SharedPrefsDeepLink
    fun provideSharedPrefsDeeplink(
        @ApplicationContext context: Context,
    ): SharedPrefs = SharedPrefsImpl(
        context,
        SharedPrefsType.DEEPLINK
    )

    @Singleton
    @Provides
    @SharedPrefsReferral
    fun provideSharedPrefsReferral(
        @ApplicationContext context: Context,
    ): SharedPrefs = SharedPrefsImpl(
        context,
        SharedPrefsType.REFERRAL
    )

    @Singleton
    @Provides
    @SharedPrefsCheQUser
    fun provideSharedPrefsCheQUser(
        @ApplicationContext context: Context,
    ): SharedPrefs = SharedPrefsImpl(
        context,
        SharedPrefsType.CHEQUSER
    )

    @Singleton
    @Provides
    @SharedPrefsCheQ
    fun provideSharedPrefsCheQ(
        @ApplicationContext context: Context,
    ): SharedPrefs = SharedPrefsImpl(
        context,
        SharedPrefsType.CHEQ
    )

    @Singleton
    @Provides
    @SharedPrefsLog
    fun provideSharedPrefsLog(
        @ApplicationContext context: Context,
    ): SharedPrefs = SharedPrefsImpl(
        context,
        SharedPrefsType.LOG
    )

}