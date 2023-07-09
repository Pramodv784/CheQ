package com.cheq.retail.newarch.di

import com.cheq.common.config.remoteconfig.RemoteConfig
import com.cheq.config.CheqCrashlytics
import com.cheq.config.FeatureConfig
import com.cheq.encryption.EncryptionPass
import com.cheq.encryption.EncryptionPassImpl
import com.cheq.retail.newarch.config.AppConfigImpl
import com.cheq.retail.newarch.config.CheqCrashlyticsImpl
import com.cheq.retail.newarch.config.FeatureConfigImpl
import com.cheq.retail.newarch.config.RemoteConfigImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppConfigModule {

    @Binds
    abstract fun providesAppConfig(impl: AppConfigImpl): com.cheq.config.AppConfig

    @Binds
    abstract fun providesRemoteConfig(impl: RemoteConfigImpl): RemoteConfig

    @Binds
    abstract fun providesFeatureConfig(impl: FeatureConfigImpl): FeatureConfig

    @Binds
    abstract fun providesCheqCrashlytics(impl: CheqCrashlyticsImpl): CheqCrashlytics

    @Binds
    abstract fun providesEncryptionPass(impl: EncryptionPassImpl): EncryptionPass

}