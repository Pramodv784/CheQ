package com.cheq.retail.ui.landing.model

import androidx.annotation.Keep

@Keep
sealed class LandingItemVO(val type: ELandingViewItemType) {
    @Keep
    data class Header(val name: String) : LandingItemVO(ELandingViewItemType.HEADER)
    @Keep
    object AddProduct : LandingItemVO(ELandingViewItemType.ADD_MANUALLY)
    @Keep
    data class CreditScoreItem(val score: CreditScore) :
        LandingItemVO(ELandingViewItemType.CREDIT_SCORE)
    @Keep
    data class Subtitle(val productCount: Int) : LandingItemVO(ELandingViewItemType.PRODUCT_TITLE)
    @Keep
    data class Product(val product: LandingProductCategory) :
        LandingItemVO(ELandingViewItemType.PRODUCT)
}
@Keep
enum class ELandingViewItemType {
    HEADER,
    ADD_MANUALLY,
    CREDIT_SCORE,
    PRODUCT_TITLE,
    PRODUCT
}