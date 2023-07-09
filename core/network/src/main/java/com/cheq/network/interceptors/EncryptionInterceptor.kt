package com.cheq.network.interceptors

import android.util.Log
import com.cheq.config.AppConfig
import com.cheq.config.CheqCrashlytics
import com.cheq.encryption.EncryptionPass
import com.cheq.network.encryption.Aes256
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import org.json.JSONObject
import java.io.IOException

/**
 * Created by Sanket Mendon on 27,April,2023.
 * sanket@cheq.one
 */

class EncryptionInterceptor constructor(
    private val appConfig: AppConfig,
    private val cheqCrashlytics: CheqCrashlytics,
    private val encryptionPass: EncryptionPass
) : Interceptor {
    private val TAG = EncryptionInterceptor::class.java.simpleName

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (!request.method.equals("GET")) {
            val rawBody: RequestBody? = request.body
            val encryptedBody: String?
            val jsonObject = JSONObject()
            if (encryptionPass.getDecryptedText().isNullOrEmpty().not()) {
                try {
                    val buffer = Buffer()
                    rawBody?.writeTo(buffer)
                    val stringBody = buffer.readUtf8()
                    encryptedBody = Aes256.encrypt(stringBody, encryptionPass.getDecryptedText())
                    jsonObject.put("data", encryptedBody)
                    Log.d(TAG, "Original request: ${stringBody}")
                    Log.d(TAG, "Encrypted request: $encryptedBody")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                cheqCrashlytics.log("Encryption key not found")
            }
            val body = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            request = request.newBuilder()
                .method(request.method, body).build()
        }

        return chain.proceed(request)
    }
}