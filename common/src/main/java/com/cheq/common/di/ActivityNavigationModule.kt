package com.cheq.common.di

import com.cheq.common.ui.webview.GenericWebViewActivity
import com.cheq.navigation.IntentKey
import com.cheq.navigation.IntentProvider
import com.cheq.navigation.IntentResolverKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
object ActivityNavigationModule {
    @Provides
    @IntoMap
    @IntentResolverKey(IntentKey.GenericWebViewActivityKey::class)
    fun provideWebViewIntent(): IntentProvider<*> = GenericWebViewActivity.intentHelper
}
