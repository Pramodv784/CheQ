package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class RedeemCouponResponse(

	@field:SerializedName("data")
	val data: VoucherResponse? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null,

	@field:SerializedName("apiMessage")
	val massage: String? = null,

	@field:SerializedName("user_err_msg")
	val userErrorMessage: String? = null


)

@Keep
data class VoucherResponse(

	@field:SerializedName("partner_orders_id")
	val partnerOrdersId: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("cheq_cat_id")
	val cheqCatId: String? = null,

	@field:SerializedName("voucher_code")
	val voucherCode: String? = null,

	@field:SerializedName("cheq_partner_commission")
	val cheqPartnerCommission: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("partner_brand_id")
	val partnerBrandId: String? = null,

	@field:SerializedName("commission_id")
	val commissionId: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("cheq_brand_id")
	val cheqBrandId: String? = null,

	@field:SerializedName("burn_voucher_id")
	val burnVoucherId: Any? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("quantity")
	val quantity: Int? = null,

	@field:SerializedName("cheq_brand_name")
	val cheqBrandName: String? = null,

	@field:SerializedName("partner_brand_name")
	val partnerBrandName: Any? = null,

	@field:SerializedName("created_by")
	val createdBy: String? = null,

	@field:SerializedName("pin_status")
	val pinStatus: String? = null,

	@field:SerializedName("cheq_overall_commission")
	val cheqOverallCommission: Any? = null,

	@field:SerializedName("brand_id")
	val brandId: String? = null,

	@field:SerializedName("user_price")
	val userPrice: Any? = null,

	@field:SerializedName("cheq_user_id")
	val cheqUserId: String? = null,

	@field:SerializedName("partner_type")
	val partnerType: Int? = null,

	@field:SerializedName("voucher_pin")
	val voucherPin: String? = null,

	@field:SerializedName("updated_by")
	val updatedBy: String? = null,

	@field:SerializedName("external_order_id")
	val externalOrderId: String? = null,

	@field:SerializedName("cheq_price")
	val cheqPrice: Any? = null,

	@field:SerializedName("voucher_exp")
	val voucherExp: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("exception_narration") val exceptionNarration: String? = null,

	@field:SerializedName("voucher_valid_till") val voucherValidTill: Int? = null,

	@field:SerializedName("avail_points") val availPoints: String? = null,

	@field:SerializedName("website_url") val websiteUrl: String? = null,
)
