package com.cheq.network.interceptors

import com.cheq.config.AppConfig
import com.cheq.network.encryption.Aes256
import com.cheq.network.encryption.EncryptionPass
import com.cheq.network.encryption.EncryptionPass.randomHash
import com.cheq.network.encryption.EncryptionPass.sha256
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class HeaderInterceptor @Inject constructor(
    private val config: AppConfig,
    private val encryptionPass: com.cheq.encryption.EncryptionPass
) : Interceptor {
    companion object {
        const val OS = "os"
        const val ANDROID = "ANDROID"
        const val APP_VERSION = "appversion"
        const val SALT = "salt"
        const val DEVICE_ID = "deviceid"
        const val HEADER_HASH = "headerhash"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        try {
            val salt = randomHash(10)
            val allHeaders: String =
                ANDROID + "|" + config.getVersionName().uppercase() + "|" + config.getDeviceId()
                    .uppercase() + "|" + salt.uppercase()
            val hash = sha256(allHeaders)
            val encryptHeaders = Aes256.encrypt(hash, encryptionPass.getDecryptedText())
            builder.header(OS, ANDROID)
            builder.header(APP_VERSION, config.getVersionName())
            builder.header(SALT, salt.uppercase())
            builder.header(DEVICE_ID, config.getDeviceId().uppercase())
            builder.header(HEADER_HASH, encryptHeaders)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return chain.proceed(builder.build())
    }
}