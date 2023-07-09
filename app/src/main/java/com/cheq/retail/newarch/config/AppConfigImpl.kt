package com.cheq.retail.newarch.config

import android.provider.Settings
import com.cheq.config.AppConfig
import com.cheq.retail.BuildConfig
import com.cheq.retail.application.MainApplication
import javax.inject.Inject

/**
 * Created by Akash Khatkale on 24th May, 2023.
 * akash.k@cheq.one
 */
class AppConfigImpl @Inject constructor(): AppConfig {

//
//    init {
//        coroutineScope.launch {
//            getEncryptionPass()
//        }
//    }

    override fun getVersionName(): String = BuildConfig.VERSION_NAME
    override fun isDebugApp(): Boolean = BuildConfig.DEBUG
    override fun apiEndPoint(): String = BuildConfig.apiEndPoint
    override fun firebaseDatabaseUrl(): String = BuildConfig.firebaseDataBaseURL
    override fun getGoogleRequestIdToken(): String = BuildConfig.googleRequestIdToken
    override fun getGoogleRequestServerAuthCode(): String = BuildConfig.googleRequestServerAuthCode
    override fun getDeviceId(): String {
        return Settings.Secure.getString(
            MainApplication.getApplicationContext().contentResolver, Settings.Secure.ANDROID_ID
        )
    }

//    private suspend fun getEncryptionPass(): String? {
//        return encryptionPass.getDecryptedText()
//    }
//
//    private suspend fun getEncryptionKey(): String? {
//        val res = cheqFirebase.getValue(API_KEY_REFERENCE, String::class.java)
//        return res.getOrNull()
//    }

}