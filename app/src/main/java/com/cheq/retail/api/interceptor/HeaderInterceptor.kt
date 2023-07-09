package com.cheq.retail.api.interceptor

import android.content.Context
import com.cheq.retail.BuildConfig
import com.cheq.retail.constants.AFConstants.ANDROID
import com.cheq.retail.constants.AFConstants.APP_VERSION
import com.cheq.retail.constants.AFConstants.DEVICE_ID
import com.cheq.retail.constants.AFConstants.HEADER_HASH
import com.cheq.retail.constants.AFConstants.OS
import com.cheq.retail.constants.AFConstants.SALT
import com.cheq.retail.utils.Aes256
import com.cheq.retail.utils.EncryptionPass
import com.cheq.retail.utils.Utils.Companion.getDeviceID
import com.cheq.retail.utils.Utils.Companion.randomHash
import com.cheq.retail.utils.Utils.Companion.sha256
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        try {
            val salt = randomHash(10)
            val allHeaders: String =
                ANDROID + "|" + BuildConfig.VERSION_NAME.uppercase()+"|" + getDeviceID().uppercase() + "|" + salt.uppercase()
            val hash = sha256(allHeaders)
            val decryptedText = EncryptionPass.getDecryptedText()
            decryptedText?.let {
                val encryptHeaders = Aes256.encrypt(hash,
                    decryptedText )
                builder.header(OS, ANDROID)
                builder.header(APP_VERSION, BuildConfig.VERSION_NAME)
                builder.header(SALT, salt.uppercase())
                builder.header(DEVICE_ID, getDeviceID().uppercase())
                builder.header(HEADER_HASH, encryptHeaders)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }


        return chain.proceed(builder.build())
    }


}