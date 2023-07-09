package com.cheq.retail.ui.splash.model

import androidx.annotation.Keep
import com.cheq.retail.ui.socialLogin.model.CheqSafeStatus
import com.google.gson.annotations.SerializedName

@Keep
data class ProfileDetailsResponse(
    @SerializedName("isActive")
    var isactive: Boolean,
    @SerializedName("isDeleted")
    var isdeleted: Boolean,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("deviceId")
    val deviceid: String,
    @SerializedName("updatedAt")
    val updatedat: String,
    @SerializedName("createdAt")
    val createdat: String,
    @SerializedName("__v")
    var V: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstname: String,
    @SerializedName("lastName")
    val lastname: String,
    @SerializedName("dateOfBirth")
    val dateofbirth: String,
    @SerializedName("panCard")
    val pancard: String,
     @SerializedName("customerStatus")
    val customerStatus: String,
    @SerializedName("isEmandateDone")
    val isEmandateDone: Boolean,

    val apiMessage: String? = null,


    val status: Boolean? = null,

    @SerializedName("cheqSafeStatus")
    val cheqSafeStatus: CheqSafeStatus
)