package com.cheq.retail.ui.main.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
@Keep
@Parcelize
data class CompanyConstant(

    @field:SerializedName("fullPaymentRewardPercentage")
    val fullPaymentRewardPercentage: Int? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("rewardToVoucherConversion")
    val rewardToVoucherConversion: Int? = null,

    @field:SerializedName("rewardForReferral")
    val rewardForReferral: Int? = null,

    @field:SerializedName("accessTokenExpiry")
    val accessTokenExpiry: Int? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("rewardExpiresAfter")
    val rewardExpiresAfter: Int? = null,

    @field:SerializedName("isDeleted")
    val isDeleted: Boolean? = null,

    @field:SerializedName("refreshTokenExpiry")
    val refreshTokenExpiry: Int? = null,

    @field:SerializedName("cardActivationAmount")
    val cardActivationAmount: Int? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("globalOnboardingMode")
    val globalOnboardingMode: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("rewardForSignUp")
    val rewardForSignUp: Int? = null,

    @field:SerializedName("waitlistCounter")
    val waitlistCounter: Int? = null,

    @field:SerializedName("partialPaymentRewardPercentage")
    val partialPaymentRewardPercentage: @RawValue Any? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("UPILimit")
    val uPILimit: Int? = null,

    @field:SerializedName("waitlistOfferPoints")
    val waitlistOfferPoints: Int? = null,

    @field:SerializedName("rewardToRupeesConversion")
    val rewardToRupeesConversion: @RawValue Any? = null,

    @field:SerializedName("showWaitlistCountOnlyAfter")
    val showWaitlistCountOnlyAfter: Int? = null,

    @field:SerializedName("rewardForProductActivation")
    val rewardForProductActivation: Int? = null,

    @field:SerializedName("assetsBaseUrl")
    val assetsBaseUrl: AssetsBaseUrl? = null,

    @field:SerializedName("eMandateExpiresAt")
    val eMandateExpiresAt: Int? = null,

    @field:SerializedName("waitlistUserExpirationDays")
    val waitlistUserExpirationDays: Int? = null,

    @field:SerializedName("bureauCounter")
    val bureauCounter: Int? = null,

    @field:SerializedName("linkActivationAndTokenization")
    val linkActivationAndTokenization: Boolean? = null,

    @field:SerializedName("showWhatsappInWaitlistScreen")
    val showWhatsappInWaitlistScreen: Boolean? = null,

    @field:SerializedName("rewardForCheckSafe")
    val rewardForCheckSafe: Int? = null,

    @field:SerializedName("maxWaitlistRewardedUsers")
    val maxWaitlistRewardedUsers: Int? = null,

    @field:SerializedName("_id")
    val _id: String? = null, @field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
) : Parcelable
@Keep
@Parcelize
data class AssetsBaseUrl(

    @field:SerializedName("voucher")
    val voucher: String? = null,

    @field:SerializedName("bankMaster")
    val bankMaster: String? = null,

    @field:SerializedName("banner")
    val banner: String? = null,

    @field:SerializedName("waitlist")
    val waitlist: String? = null,

    @field:SerializedName("html")
    val html: String? = null,

    @field:SerializedName("faqs")
    val faqs: String? = null,

    @field:SerializedName("policies")
    val policies: String? = null,

    @field:SerializedName("loanMaster")
    val loanMaster: String? = null
) : Parcelable
