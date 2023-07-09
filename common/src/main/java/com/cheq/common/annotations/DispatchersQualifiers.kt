package com.cheq.common.annotations

import javax.inject.Qualifier

/**
 * Created by Akash Khatkale on 23rd May, 2023.
 * akash.k@cheq.one
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher