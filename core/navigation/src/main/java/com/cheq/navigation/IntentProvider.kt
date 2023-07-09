package com.cheq.navigation

import android.content.Context
import android.content.Intent

/**
 * Created by Akash Khatkale on 21st May, 2023.
 * akash.k@cheq.one
 */
interface IntentProvider<in Key : IntentKey> {
    fun createIntent(context: Context, key: Key): Intent
}