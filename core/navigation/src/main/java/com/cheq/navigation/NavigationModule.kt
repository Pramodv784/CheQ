package com.cheq.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Akash Khatkale on 21st May, 2023.
 * akash.k@cheq.one
 */
@InstallIn(SingletonComponent::class)
@Module
abstract class NavigationModule {
    @Binds
    abstract fun bindNavigation(intentFactoryImpl: IntentFactoryImpl): IntentFactory

}