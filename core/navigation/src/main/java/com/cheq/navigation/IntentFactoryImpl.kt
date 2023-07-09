package com.cheq.navigation

import android.content.Context
import android.content.Intent
import android.util.Log
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by Akash Khatkale on 21st May, 2023.
 * akash.k@cheq.one
 */
// Why JvmSuppressWildcards ??
// -> https://chao2zhang.medium.com/jvmsuppresswildcards-the-secret-sauce-to-your-sandwich-style-generics-b0093aa5979d
typealias IntentResolverMap = @JvmSuppressWildcards Map<Class<out IntentKey>, Provider<IntentProvider<*>>>

class IntentFactoryImpl @Inject constructor(
    private val intentFactoryMap: IntentResolverMap
) : IntentFactory {
    override fun createIntent(context: Context, key: IntentKey): Intent? {
        val intentProvider = intentFactoryMap[key::class.java]?.get() as? IntentProvider<IntentKey>
        intentProvider?.let {
            return intentProvider?.createIntent(context, key)
        } ?: run {
            Log.d(
                "IntentFactory",
                "DId not find mapping for key $key. Did you forget registering it ?"
            )
            return null
        }
    }
}