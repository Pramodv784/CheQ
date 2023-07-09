package com.cheq.retail.ui.main.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.cheq.retail.api.errormodel.BlockData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
@Keep
data class RewardsDashboardResponse(

    @field:SerializedName("howRewardWorks") val howRewardWorks: Boolean? = null,

    @field:SerializedName("coinBalance") val coinBalance: Int? = null,

    @field:SerializedName("rewardModule") val rewardModule: ArrayList<RewardModuleItem>? = null,

    @field:SerializedName("convertToVoucher") val convertToVoucher: Int? = null,

    @field:SerializedName("earnUpTo") val earnUpTo: Int? = null,

    @field:SerializedName("convertToCash") val convertToCash: Double? = null,

    @field:SerializedName("lockedChips") val lockedChips: Int? = null,

    val blockedData: BlockData?
)

@Keep
@Parcelize
data class TypesItem(

    @field:SerializedName("rewardModuleId") val rewardModuleId: String? = null,

    @field:SerializedName("image") val image: String? = null,

    @field:SerializedName("__v") val V: Int? = null,

    @field:SerializedName("name") val name: String? = null,

    @field:SerializedName("description") val description: String? = null,
    @field:SerializedName("priority") val priority: Int? = null,

    @field:SerializedName("title") val title: String? = null,

    @field:SerializedName("rmType") val rmType: String? = null,

    @field:SerializedName("other_offer_bg_image") val otherOfferBgImage: String? = null,

    @field:SerializedName("cheq_brand_accessories") val cheqBrandAccessoriesId: List<CheqBrandAccessoriesIdItem?>? = null,

    @field:SerializedName("brand_name") val brandName: String? = null,

    @field:SerializedName("id") val id: String? = null,

    @field:SerializedName("tnc") val tnc: String? = null,

    @field:SerializedName("brand_logo") val brandLogo: String? = null,

    @field:SerializedName("brand_status") val brandStatus: String? = null,

    @field:SerializedName("exception_narration") val exceptionNarration: String? = null,

    @field:SerializedName("voucher_valid_till") val voucherValidTill: Int? = null,

    @field:SerializedName("avail_points") val availPoints: String? = null,

    @field:SerializedName("website_url") val websiteUrl: String? = null,

    @field:SerializedName("sequence") val sequence: Int? = null,

    @field:SerializedName("category_name") val categoryName: String? = null,

    @field:SerializedName("updated_at") val updatedAt: String? = null,

    @field:SerializedName("updated_by") val updatedBy: String? = null,

    @field:SerializedName("created_at") val createdAt: String? = null,

    @field:SerializedName("category_status") val categoryStatus: String? = null,

    @field:SerializedName("created_by") val createdBy: String? = null,

    @field:SerializedName("bg_image") val bgImage: String? = null,

    @field:SerializedName("cat_icon") val cat_icon: String? = null,

    @field:SerializedName("status") val status: String? = null,

    @field:SerializedName("denominator") val denominator: String? = null,

    @field:SerializedName("top_deal_bg_image") val topDealBgImage: String? = null,

    @field:SerializedName("denoms") val denoms: List<DenomsItem?>? = null,

    var isSelected: Boolean = false


) : Parcelable

@Keep
data class OuterGridGradientColors(

    @field:SerializedName("gTwo") val gTwo: String? = null,

    @field:SerializedName("gOne") val gOne: String? = null,

    @field:SerializedName("gThree") val gThree: String? = null
)

@Keep
data class RewardModuleItem(

    @field:SerializedName("image") val image: String? = null,

    @field:SerializedName("Types") val types: List<TypesItem?>? = null,

    @field:SerializedName("__v") val V: Int? = null,

    @field:SerializedName("moduleName") val moduleName: String? = null,

    @field:SerializedName("_id") val id: String? = null,

    @field:SerializedName("conversionRatio") val conversionRatio: String? = null,

    @field:SerializedName("moduleBgImg") val moduleBgImg: String? = null,

    @field:SerializedName("moduleBgImgRatio") val moduleBgImgRatio: String? = null,

    @field:SerializedName("priority") val priority: Int? = null,

    @field:SerializedName("outerGridGradientColors") val outerGridGradientColors: OuterGridGradientColors? = null
)

@Parcelize
@Keep
data class CheqBrandAccessoriesIdItem(

    @field:SerializedName("denomination_amount") val denominationAmount: Int? = null,

    @field:SerializedName("updated_at") val updatedAt: String? = null,

    @field:SerializedName("module_type") val moduleType: String? = null,

    @field:SerializedName("updated_by") val updatedBy: String? = null,

    @field:SerializedName("created_at") val createdAt: String? = null,

    @field:SerializedName("id") val id: String? = null,

    @field:SerializedName("narration_one") val narrationOne: String? = null,

    @field:SerializedName("created_by") val createdBy: String? = null,

    @field:SerializedName("narration_two") val narrationTwo: String? = null,

    @field:SerializedName("priority_image") val priorityImage: String? = null,

    @field:SerializedName("status") val status: String? = null
) : Parcelable

fun RewardsDashboardResponse?.isUserBlocked(): Boolean {
    return this != null && this.blockedData != null
}