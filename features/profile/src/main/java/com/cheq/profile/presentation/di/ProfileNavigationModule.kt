package com.cheq.profile.presentation.di

import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentProvider
import com.cheq.navigation.IntentResolverKey
import com.cheq.profile.presentation.MyAccountActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Created by Akash Khatkale on 21st May, 2023.
 * akash.k@cheq.one
 */
@Module
@InstallIn(SingletonComponent::class)
object ProfileNavigationModule {

    @Provides
    @IntoMap
    @IntentResolverKey(IntentKey.MyAccountActivityKey::class)
    fun provideProfileIntent(): IntentProvider<*> = MyAccountActivity.intentHelper

}