package com.cheq.retail.ui.main.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
data class VoucherListResponse(

    @field:SerializedName("httpStatus") val httpStatus: Int? = null,

    @field:SerializedName("vouchers") val vouchers: ArrayList<VouchersItem>? = null,
    @field:SerializedName("apiMessage")
    val apiMessage: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
)

@Keep
data class Vouchers(



    @field:SerializedName("vouchers") val vouchers: List<VouchersItem?>? = null,

)

@Keep
data class VouchersItem(

    @field:SerializedName("minValue") val minValue: String? = null,

    @field:SerializedName("name") val name: String? = null,

    @field:SerializedName("tnc") val tnc: String? = null,

    @field:SerializedName("brand_logo") val brandLogo: String? = null,

    @field:SerializedName("exception_narration") val exceptionNarration: String? = null,

    @field:SerializedName("voucher_valid_till") val voucherValidTill: Int? = null,

    @field:SerializedName("avail_points") val availPoints: String? = null,
    @field:SerializedName("denominator") val denominator: String? = null,

    @field:SerializedName("website_url") val websiteUrl: String? = null,
    @field:SerializedName("denoms") val denoms: List<DenomsItem?>? = null
)

@Keep
@Parcelize
data class DenomsItem(

    @field:SerializedName("denominations") val denominations: Int? = null,

    @field:SerializedName("brand_name") val brandName: String? = null,

    @field:SerializedName("brand_logo") val brandLogo: String? = null,

    @field:SerializedName("id") val id: String? = null,

    var isSelected: Boolean = false
) : Parcelable
