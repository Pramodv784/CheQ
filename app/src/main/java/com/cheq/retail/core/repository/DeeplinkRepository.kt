package com.cheq.retail.core.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class DeeplinkRepository private constructor(private val context: Context) {

    companion object {
        private const val STORED_DEEPLINK = "STORED_DEEPLINK"

        private var instance: DeeplinkRepository? = null

        fun getInstance(context: Context): DeeplinkRepository {
            return instance ?: kotlin.run {
                instance = DeeplinkRepository(context)
                instance!!
            }
        }
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("DEEPLINK", Activity.MODE_PRIVATE)
    }

    fun storeDeeplink(link: String) {
        sharedPreferences.edit {
            putString(STORED_DEEPLINK, link)
        }
    }

    fun popDeeplink(): String? {

        val deeplink = sharedPreferences.getString(STORED_DEEPLINK, null)
        sharedPreferences.edit {
            remove(STORED_DEEPLINK)
        }
        return deeplink
    }

}