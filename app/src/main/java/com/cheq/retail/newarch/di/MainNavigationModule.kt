package com.cheq.retail.newarch.di

import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentProvider
import com.cheq.navigation.IntentResolverKey
import com.cheq.retail.ui.login.ExistingUserActivity
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.splash.SplashActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Created by Akash Khatkale on 28th May, 2023.
 * akash.k@cheq.one
 */
@Module
@InstallIn(SingletonComponent::class)
object MainNavigationModule {

    @Provides
    @IntoMap
    @IntentResolverKey(IntentKey.ExistingUserActivityKey::class)
    fun provideExistingUserIntent(): IntentProvider<*> = ExistingUserActivity.intentHelper

    @Provides
    @IntoMap
    @IntentResolverKey(IntentKey.SplashActivityKey::class)
    fun provideSplashActivity(): IntentProvider<*> = SplashActivity.intentHelper

    @Provides
    @IntoMap
    @IntentResolverKey(IntentKey.MainActivityKey::class)
    fun provideMainActivity(): IntentProvider<*> = MainActivity.intentHelper

}