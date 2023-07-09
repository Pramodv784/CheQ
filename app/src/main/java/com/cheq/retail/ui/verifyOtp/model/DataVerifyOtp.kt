package com.cheq.retail.ui.verifyOtp.model

import androidx.annotation.Keep
import com.cheq.retail.api.errormodel.BlockData

@Keep
data class DataVerifyOtp(
    val token: Token,
    val user: User,
    val apiMessage: String?,
    val status: Boolean?,
    val blockedData: BlockData?
)

fun DataVerifyOtp?.isUserBlocked(): Boolean {
    return (this != null) && (this.blockedData != null)
}