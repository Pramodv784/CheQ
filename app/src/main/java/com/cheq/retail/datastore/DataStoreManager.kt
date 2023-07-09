package com.cheq.retail.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


class DataStoreManager(val context: Context) {




    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "CHEQ")
        val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
        val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
    }

    suspend fun saveAccessTokenToDataStore(accessToken: String) {
        context.dataStore.edit {
        it[ACCESS_TOKEN] = accessToken
         }
    }

    suspend fun saveRefreshTokenToDataStore(refreshToken: String) {
        context.dataStore.edit {
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessTokenFromDataStore() = context.dataStore.data.map {
        it[ACCESS_TOKEN]?:""
    }
    suspend fun getRefreshTokenFromDataStore() = context.dataStore.data.map {
        it[ACCESS_TOKEN]?:""
    }
}






