package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VoucherRedeemHistoryResponse(

    @field:SerializedName("success") val success: Boolean? = null,

    @field:SerializedName("httpStatus") val httpStatus: Int? = null,

    @field:SerializedName("valueAndCount") val valueAndCount: ValueAndCount? = null,

    @field:SerializedName("vouchers") val vouchers: List<VouchersHistoryItem?>? = null
)

@Keep
data class ValueAndCount(

    @field:SerializedName("totalValue") val totalValue: String? = null,

    @field:SerializedName("totalVouchers") val totalVouchers: String? = null
)

@Keep
data class CheqBrands(

    @field:SerializedName("other_offer_bg_image") val otherOfferBgImage: Any? = null,

    @field:SerializedName("tnc") val tnc: String? = null,

    @field:SerializedName("created_at") val createdAt: String? = null,

    @field:SerializedName("denominations") val denominations: Int? = null,

    @field:SerializedName("cheq_cat_id") val cheqCatId: String? = null,

    @field:SerializedName("brand_name") val brandName: String? = null,

    @field:SerializedName("brand_logo") val brandLogo: String? = null,

    @field:SerializedName("partner_service_name") val partnerServiceName: String? = null,

    @field:SerializedName("created_by") val createdBy: String? = null,

    @field:SerializedName("brand_status") val brandStatus: String? = null,

    @field:SerializedName("partner_brand_master_id") val partnerBrandMasterId: String? = null,

    @field:SerializedName("pin_status") val pinStatus: Any? = null,

    @field:SerializedName("sequence") val sequence: Int? = null,

    @field:SerializedName("partner_service_code") val partnerServiceCode: String? = null,

    @field:SerializedName("updated_at") val updatedAt: String? = null,

    @field:SerializedName("partner_brand_id") val partnerBrandId: String? = null,

    @field:SerializedName("partner_type") val partnerType: Int? = null,

    @field:SerializedName("brand_denomination_status") val brandDenominationStatus: String? = null,

    @field:SerializedName("top_deal_bg_image") val topDealBgImage: String? = null,

    @field:SerializedName("updated_by") val updatedBy: String? = null,

    @field:SerializedName("id") val id: String? = null,

    @field:SerializedName("status") val status: String? = null,

    @field:SerializedName("exception_narration") val exceptionNarration: String? = null,

    @field:SerializedName("voucher_valid_till") val voucherValidTill: Int? = null,

    @field:SerializedName("avail_points") val availPoints: String? = null,

    @field:SerializedName("website_url") val websiteUrl: String? = null,
)

@Keep
data class VouchersHistoryItem(

    @field:SerializedName("created_at") val createdAt: String? = null,

    @field:SerializedName("cheq_cat_id") val cheqCatId: String? = null,

    @field:SerializedName("voucher_code") val voucherCode: String? = null,

    @field:SerializedName("cheq_partner_commission") val cheqPartnerCommission: Any? = null,

    @field:SerializedName("updated_at") val updatedAt: String? = null,

    @field:SerializedName("partner_brand_id") val partnerBrandId: String? = null,

    @field:SerializedName("commission_id") val commissionId: Any? = null,

    @field:SerializedName("id") val id: String? = null,

    @field:SerializedName("cheq_brand_id") val cheqBrandId: String? = null,

    @field:SerializedName("burn_voucher_id") val burnVoucherId: Any? = null,

    @field:SerializedName("amount") val amount: Int? = null,

    @field:SerializedName("quantity") val quantity: Int? = null,

    @field:SerializedName("cheq_brand_name") val cheqBrandName: String? = null,

    @field:SerializedName("partner_brand_name") val partnerBrandName: Any? = null,

    @field:SerializedName("created_by") val createdBy: String? = null,

    @field:SerializedName("pin_status") val pinStatus: String? = null,

    @field:SerializedName("cheq_overall_commission") val cheqOverallCommission: Any? = null,

    @field:SerializedName("brand_id") val brandId: String? = null,

    @field:SerializedName("cheq_brands") val cheqBrands: CheqBrands? = null,

    @field:SerializedName("user_price") val userPrice: Any? = null,

    @field:SerializedName("cheq_user_id") val cheqUserId: String? = null,

    @field:SerializedName("partner_type") val partnerType: Int? = null,

    @field:SerializedName("voucher_pin") val voucherPin: String? = null,

    @field:SerializedName("updated_by") val updatedBy: String? = null,

    @field:SerializedName("external_order_id") val externalOrderId: String? = null,

    @field:SerializedName("cheq_price") val cheqPrice: Any? = null,

    @field:SerializedName("voucher_exp") val voucherExp: String? = null,

    @field:SerializedName("status") val status: String? = null,


)
