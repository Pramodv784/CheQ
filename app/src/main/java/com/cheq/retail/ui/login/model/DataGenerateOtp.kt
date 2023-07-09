package com.cheq.retail.ui.login.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.cheq.retail.api.errormodel.BlockData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DataGenerateOtp(
    val OTP: String?,
    val cheqUserId: String?,
    val deviceId: String?,
    val messageId: String?,
    val blockedData: BlockData?,
    @field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null

) : Parcelable
fun DataGenerateOtp?.isUserBlocked(): Boolean {
    return this != null && this.blockedData != null
}
