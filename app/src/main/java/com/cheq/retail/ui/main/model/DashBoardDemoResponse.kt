package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class DashBoardDemoResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("requestId")
	val requestId: String? = null,

	@field:SerializedName("httpStatus")
	val httpStatus: Int? = null
)
@Keep
data class Data(

	@field:SerializedName("howRewardWorks")
	val howRewardWorks: Boolean? = null,

	@field:SerializedName("otherOffers")
	val otherOffers: List<OtherOffersItem?>? = null,

	@field:SerializedName("coinBalance")
	val coinBalance: Int? = null,

	@field:SerializedName("rewardModule")
	val rewardModule: List<RewardModuleItem?>? = null,

	@field:SerializedName("featuredDeal")
	val featuredDeal: List<FeaturedDealItem?>? = null,

	@field:SerializedName("convertToVoucher")
	val convertToVoucher: Int? = null,

	@field:SerializedName("earnUpTo")
	val earnUpTo: Int? = null,

	@field:SerializedName("convertToCash")
	val convertToCash: Double? = null
)


@Keep
data class OtherOffersItem(

	@field:SerializedName("other_offer_bg_image")
	val otherOfferBgImage: Any? = null,

	@field:SerializedName("cheq_brand_accessories_id")
	val cheqBrandAccessoriesId: List<CheqBrandAccessoriesIdItem?>? = null,

	@field:SerializedName("brand_name")
	val brandName: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("brand_logo")
	val brandLogo: Any? = null,

	@field:SerializedName("brand_status")
	val brandStatus: String? = null
)




@Keep
data class FeaturedDealItem(

	@field:SerializedName("cheq_brand_accessories_id")
	val cheqBrandAccessoriesId: List<CheqBrandAccessoriesIdItem?>? = null,

	@field:SerializedName("top_deal_bg_image")
	val topDealBgImage: String? = null,

	@field:SerializedName("brand_name")
	val brandName: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("brand_logo")
	val brandLogo: Any? = null,

	@field:SerializedName("brand_status")
	val brandStatus: String? = null
)
