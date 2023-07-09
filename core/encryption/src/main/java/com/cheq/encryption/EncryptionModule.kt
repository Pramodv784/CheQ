package com.cheq.encryption

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class EncryptionModule {

    @Binds
    abstract fun provideEncryptionManager(impl: EncryptionManagerImpl): EncryptionManager

}