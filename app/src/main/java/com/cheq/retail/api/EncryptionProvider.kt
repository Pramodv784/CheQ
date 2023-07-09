package com.cheq.retail.api

import androidx.annotation.Keep
import com.cheq.retail.utils.Aes256
import com.cheq.retail.utils.EncryptionPass
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

@Keep
class EncryptionProvider(model: Any) {
    @Keep
    @SerializedName("data")
    var data: String = ""

    init {
        val gson = Gson()

        data = EncryptionPass.getDecryptedText()?.let { Aes256.encrypt(gson.toJson(model), it)}.toString()
    }
}