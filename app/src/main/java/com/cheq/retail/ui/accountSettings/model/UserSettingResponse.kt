package com.cheq.retail.ui.accountSettings.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.cheq.retail.ui.socialLogin.model.EmailLinkingStatus
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize



@Parcelize
@Keep
data class ActiveEmailsItem(

    @field:SerializedName("emailTokenSource")
    val emailTokenSource: String? = null,

    @field:SerializedName("firstName")
    val firstName: String? = null,

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("isDeleted")
    val isDeleted: Boolean? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("emailLinkingStatus")
    val emailLinkingStatus: EmailLinkingStatus? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
) : Parcelable

@Keep
data class UserSettingResponse(

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("panCard")
    val panCard: String? = null,

    @field:SerializedName("isAutoPayEnabled")
    val isAutoPayEnabled: Boolean? = null,

    @field:SerializedName("eMandateData")
    val eMandateData: EMandateData? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("dateOfBirth")
    val dateOfBirth: String? = null,

    @field:SerializedName("emailText")
    val emailText: String? = null,

    @field:SerializedName("isEmandateDone")
    val isEmandateDone: Boolean? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("deviceId")
    val deviceId: String? = null,

    @field:SerializedName("panUpdated")
    val panUpdated: Boolean? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("firstName")
    val firstName: String? = null,

    @field:SerializedName("isDeleted")
    val isDeleted: Boolean? = null,

    @field:SerializedName("inActiveEmails")
    val inActiveEmails: List<Any?>? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("activeEmails")
    val activeEmails: List<ActiveEmailsItem?>? = null,

    @field:SerializedName("whatsAppAccess")
    val whatsAppAccess: Boolean? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("fcmToken")
    val fcmToken: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)
@Keep
data class EMandateData(

    @field:SerializedName("eMandateExpires")
    val eMandateExpires: String? = null,

    @field:SerializedName("eMandateUserName")
    val eMandateUserName: String? = null,

    @field:SerializedName("eMandateBankName")
    val eMandateBankName: String? = null,

    @field:SerializedName("eMandateAccountNumber")
    val eMandateAccountNumber: String? = null,

    @field:SerializedName("logo")
    val logo: String? = null,

    @field:SerializedName("eMandateIfscCode")
    val eMandateIfscCode: String? = null,

    @field:SerializedName("eMandateDate")
    val eMandateDate: String? = null,

    @field:SerializedName("eMandateBankId")
    val eMandateBankId: String? = null
)