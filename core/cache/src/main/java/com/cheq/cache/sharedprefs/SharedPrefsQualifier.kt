package com.cheq.cache.sharedprefs

import javax.inject.Qualifier

/**
 * Created by Akash Khatkale on 16th May, 2023.
 * akash.k@cheq.one
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SharedPrefsDeepLink

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SharedPrefsReferral

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SharedPrefsCheQUser

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SharedPrefsCheQ

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SharedPrefsLog