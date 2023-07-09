package com.cheq.retail.ui.login.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class WebPagesResponse(
    val list: List<Listdataresponse>,
    val map: Map,@field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
): Serializable