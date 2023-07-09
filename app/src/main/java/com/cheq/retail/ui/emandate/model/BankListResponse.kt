package com.cheq.retail.ui.emandate.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BankListResponse(
    var data: Banks,@field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
) {
    @Keep
    data class Banks(
        var banks: ArrayList<DataEntity>,
        var top6: ArrayList<DataEntity>
    )
    @Keep
    data class DataEntity(
        var _id: String,
        var isActive: Boolean,
        var isDeleted: Boolean,
        var originalBankName: String,
        var bureauBankName: String,
        var ocrBankName: String,
        var logo: String,
        var IFSC_Prefix: String,
        var updatedAt: String,
        var createdAt: String,
        var __v: Int,
        var logoWithName: String,
        var bankName: String,
        var name: String,
        var auth_types: ArrayList<String>
    )
}