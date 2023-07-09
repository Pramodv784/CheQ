package com.cheq.common.di

import com.cheq.common.freshchat.FreshChat
import com.cheq.common.freshchat.FreshChatImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Akash Khatkale on 28th May, 2023.
 * akash.k@cheq.one
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FreshChatModule {

    @Binds
    @Singleton
    abstract fun bindsFreshChat(impl: FreshChatImpl): FreshChat
}