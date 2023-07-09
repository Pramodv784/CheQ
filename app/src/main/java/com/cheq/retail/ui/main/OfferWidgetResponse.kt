package com.cheq.retail.ui.main

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OfferWidgetResponse(

    @field:SerializedName("offers")
    val offers: List<OffersItem?>? = null,

    @field:SerializedName("visibility")
    val visibility: Boolean? = null,

    @field:SerializedName("section_title")
    val section_title: String? = null
)

@Keep
data class Rule(

    @field:SerializedName("valid_to")
    val valid_to: String? = null,

    @field:SerializedName("screen")
    var screen: List<String?>? = null,

    @field:SerializedName("valid_from")
    val valid_from: String? = null,

    @field:SerializedName("whitelist")
    val whitelist: Whitelist? = null
)

@Keep
data class BannerDetails(

    @field:SerializedName("cta")
    val cta: Cta? = null,

    @field:SerializedName("descprition_asset")
    val descprition_asset: String? = null,

    @field:SerializedName("aspect_ratio")
    val aspect_ratio: String? = null,


    @field:SerializedName("tncLink")
    val tncLink: String? = null,

    @field:SerializedName("descprition_asset_type")
    val descprition_asset_type: Int? = null
)

@Keep
data class Secondary(

    @field:SerializedName("action")
    val action: String? = null,
    @field:SerializedName("action_type")
    val action_type: String? = null,

    @field:SerializedName("text")
    val text: String? = null
)

@Keep
data class Primary(

    @field:SerializedName("action")
    val action: String? = null,
    @field:SerializedName("action_type")
    val action_type: String? = null,

    @field:SerializedName("text")
    val text: String? = null
)

@Keep
data class Cta(

    @field:SerializedName("secondary")
    val secondary: Secondary? = null,

    @field:SerializedName("primary")
    val primary: Primary? = null
)

@Keep
data class OffersItem(

    @field:SerializedName("aspect_ratio")
    val aspect_ratio: String? = null,

    @field:SerializedName("visibility")
    val visibility: Boolean? = null,

    @field:SerializedName("banner_type")
    val banner_type: Int? = null,

    @field:SerializedName("banner_asset")
    val banner_asset: String? = null,

    @field:SerializedName("banner_redirection")
    val banner_redirection: String? = null,

    @field:SerializedName("banner_redirection_type")
    val banner_redirection_type: String? = null,

    @field:SerializedName("meta_data")
    val meta_data: String? = null,


    @field:SerializedName("rule")
    val rule: Rule? = null,

    @field:SerializedName("banner_details")
    val banner_details: BannerDetails? = null
)

@Keep
data class Whitelist(

    @field:SerializedName("turn_on")
    val turn_on: Boolean? = null,

    @field:SerializedName("userId")
    val userId: List<String?>? = null
)
