package com.cheq.retail.ui.main

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CheqSafeBannerResponse(

    /* @field:SerializedName("visibility")
     val visibility: Boolean? = null,

     @field:SerializedName("banner_url")
     val banner_url: String? = null,

     @field:SerializedName("title")
     val title: String? = null,

     @field:SerializedName("banner_type")
     val banner_type: Int? = null,

     @field:SerializedName("banner_redirection_type")
     val banner_redirection_type: String? = null,

     @field:SerializedName("banner_redirection")
     val banner_redirection: String? = null,

     @field:SerializedName("ratio")
     val ratio: String? = null*/

    @field:SerializedName("cheqsafe_visibility")
    val cheqsafe_visibility: Boolean? = null,
    @field:SerializedName("feature_config_home")
    val feature_config_home: FeatureConfig? = null,
    @field:SerializedName("feature_config_payment")
    val feature_config_payment: FeatureConfig? = null,
    @field:SerializedName("feature_config_ctc")
    val feature_config_ctc: FeatureConfig? = null,
    @field:SerializedName("feature_config_setting")
    val feature_config_setting: FeatureConfig? = null
)
@Keep
data class FeatureConfig(
    @field:SerializedName("banners")
    val banners: Banner? = null,
    @field:SerializedName("rule")
    val rule: Rules? = null
)

@Keep
data class FeatureConfigSetting(
    @field:SerializedName("rule")
    val rule: Rules? = null
)
@Keep
data class Banner(
    @field:SerializedName("initial")
    val initial: String? = null,
    @field:SerializedName("failed")
    val failed: String? = null
)
@Keep
data class Rules(
    @field:SerializedName("platform")
    val platform: Platform? = null,
    @field:SerializedName("whitelist")
    val whitelist: WhitelistUser? = null
)
@Keep
data class Platform(
    @field:SerializedName("android")
    val android: Boolean? = null,
)
@Keep
data class WhitelistUser(
    @field:SerializedName("whitelist_users")
    val whitelist_users: List<String>? = null,

    @field:SerializedName("enabled")
    val enabled: Boolean? = null,
)
