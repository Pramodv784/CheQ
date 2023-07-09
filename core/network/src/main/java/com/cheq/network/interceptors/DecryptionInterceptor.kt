package com.cheq.network.interceptors

import android.util.Log
import com.cheq.encryption.EncryptionPass
import com.cheq.network.encryption.Aes256
import com.cheq.network.models.CheqResponse
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.util.*


/**
 * Created by Sanket Mendon on 27,April,2023.
 * sanket@cheq.one
 */

class DecryptionInterceptor constructor(
    private val encryptionPass: EncryptionPass
) : Interceptor {
    private val TAG = DecryptionInterceptor::class.java.simpleName

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val rawBody: ResponseBody? = response.body
        if (response.isSuccessful /*&& !chain.request().method.equals("GET")*/) {
            val stringBody: String? = rawBody?.string()
            val cheqResponse = Gson().fromJson(stringBody ?: "", CheqResponse::class.java)
            cheqResponse.data?.let { encryptedData ->
                cheqResponse.data = Aes256.decrypt(encryptedData, encryptionPass.getDecryptedText())
            }
            Log.d(TAG, "Original response: ${stringBody}")
            Log.d(TAG, "Decrypted response: ${cheqResponse.data}")
            val contentType = response.body?.contentType()
            val responseBody = Gson().toJson(cheqResponse).toResponseBody(contentType)
            return response.newBuilder().body(responseBody).build()
        } else {
            return response
        }
    }
}