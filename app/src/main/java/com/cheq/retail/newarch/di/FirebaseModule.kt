package com.cheq.retail.newarch.di

import com.cheq.common.firebase.CheqFirebase
import com.cheq.retail.newarch.firebase.CheqFirebaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Akash Khatkale on 26th May, 2023.
 * akash.k@cheq.one
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {

    @Binds
    abstract fun providesFirebase(impl: CheqFirebaseImpl): CheqFirebase

}