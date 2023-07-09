package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName

data class UserSettingsResponse(
    @SerializedName("__v") val version: Int?,
    @SerializedName("_id") val id: String?,
    @SerializedName("activeEmails") val activeEmails: List<ActiveEmailDto?>?,
    @SerializedName("appVersion") val appVersion: String?,
    @SerializedName("appsflyer_id") val appsflyerId: String?,
    @SerializedName("bureauLastPull") val bureauLastPull: String?,
    @SerializedName("bureauProvider") val bureauProvider: String?,
    @SerializedName("bureauRefreshPullConfig") val bureauRefreshPullConfig: BureauRefreshPullConfig?,
    @SerializedName("cheqSafeStatus") val cheqSafeStatus: String?,
    @SerializedName("cheqUserId") val cheqUserId: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("creditScore") val creditScore: Int?,
    @SerializedName("currentAppVersion") val currentAppVersion: String?,
    @SerializedName("currentOs") val currentOs: String?,
    @SerializedName("customerStatus") val customerStatus: String?,
    @SerializedName("dateOfBirth") val dateOfBirth: String?,
    @SerializedName("deviceId") val deviceId: String?,
    @SerializedName("eMandateAccountNumber") val eMandateAccountNumber: Any?,
    @SerializedName("eMandateBankId") val eMandateBankId: Any?,
    @SerializedName("eMandateBankName") val eMandateBankName: Any?,
    @SerializedName("eMandateData") val eMandateData: EMandateData?,
    @SerializedName("eMandateDate") val eMandateDate: Any?,
    @SerializedName("eMandateExpires") val eMandateExpires: Any?,
    @SerializedName("eMandateIfscCode") val eMandateIfscCode: Any?,
    @SerializedName("eMandateUserName") val eMandateUserName: Any?,
    @SerializedName("email") val email: String?,
    @SerializedName("emailText") val emailText: String?,
    @SerializedName("fcmToken") val fcmToken: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("inActiveEmails") val inActiveEmails: List<Any?>?,
    @SerializedName("isActive") val isActive: Boolean?,
    @SerializedName("isAutoPayEnabled") val isAutoPayEnabled: Boolean?,
    @SerializedName("isBlock") val isBlock: Boolean?,
    @SerializedName("isDeleted") val isDeleted: Boolean?,
    @SerializedName("isEligibleForReferral") val isEligibleForReferral: Boolean?,
    @SerializedName("isEmandateDone") val isEmandateDone: Boolean?,
    @SerializedName("isPanPopulatedByBureau") val isPanPopulatedByBureau: Boolean?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("logo") val logo: Any?,
    @SerializedName("mainDeviceId") val mainDeviceId: String?,
    @SerializedName("messageFyi") val messageFyi: Any?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("oldMobile") val oldMobile: Any?,
    @SerializedName("os") val os: String?,
    @SerializedName("panCard") val panCard: String?,
    @SerializedName("panUpdated") val panUpdated: Boolean?,
    @SerializedName("referralUrl") val referralUrl: String?,
    @SerializedName("status") val status: Boolean?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("whatsAppAccess") val whatsAppAccess: Boolean?
) {
    data class ActiveEmailDto(
        @SerializedName("__v") val version: Int?,
        @SerializedName("_id") val id: String?,
        @SerializedName("authCode") val authCode: Any?,
        @SerializedName("cheqUserId") val cheqUserId: String?,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("emailLinkingStatus") val emailLinkingStatus: String?,
        @SerializedName("emailTokenSource") val emailTokenSource: String?,
        @SerializedName("firstName") val firstName: String?,
        @SerializedName("isActive") val isActive: Boolean?,
        @SerializedName("isDeleted") val isDeleted: Boolean?,
        @SerializedName("lastName") val lastName: String?,
        @SerializedName("last_retry_date") val lastRetryDate: Any?,
        @SerializedName("messageFyi") val messageFyi: Any?,
        @SerializedName("os") val os: String?,
        @SerializedName("refreshToken") val refreshToken: String?,
        @SerializedName("retry_attempt") val retryAttempt: Int?,
        @SerializedName("token") val token: String?,
        @SerializedName("updatedAt") val updatedAt: String?
    )

    data class BureauRefreshPullConfig(
        @SerializedName("stageOneId_") val stageOneId: String?,
        @SerializedName("stageTwoId_") val stageTwoId: String?
    )

    data class EMandateData(
        @SerializedName("eMandateAccountNumber") val eMandateAccountNumber: Any?,
        @SerializedName("eMandateBankId") val eMandateBankId: Any?,
        @SerializedName("eMandateBankName") val eMandateBankName: Any?,
        @SerializedName("eMandateDate") val eMandateDate: Any?,
        @SerializedName("eMandateExpires") val eMandateExpires: Any?,
        @SerializedName("eMandateIfscCode") val eMandateIfscCode: Any?,
        @SerializedName("eMandateUserName") val eMandateUserName: Any?,
        @SerializedName("logo") val logo: Any?
    )
}