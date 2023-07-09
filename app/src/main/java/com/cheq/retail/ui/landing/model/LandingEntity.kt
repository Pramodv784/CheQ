package com.cheq.retail.ui.landing.model

import androidx.annotation.Keep

@Keep
data class LandingEntity(
    val name: String,
    val creditScore: CreditScore?,
    val products: List<LandingProductCategory>
) {
    @Keep
    companion object {
        //https://companieslogo.com/img/orig/HDB-bb6241fe.png?t=1633497370

        fun getDummyView(): LandingEntity {
            return LandingEntity(
                name = "Tejas",
                creditScore = CreditScore(
                    score = 700,
                    provider = "Experian",
                    leadingHtml = "Woohoo! you’re <span style=\"color:#00C197\">Top 12%</span> in your area"
                ),
                products = emptyList()
                        /*listOf(
                    LandingProductCategory(
                        title = "HDFC",
                        imageUrl = "https://companieslogo.com/img/orig/HDB-bb6241fe.png?t=1633497370",
                        type = LandingProductType.CARD,
                        products = listOf(
                            LandingProduct.Card(
                                id = "ABC",
                                last4Digits = "1234",
                                imageUrl = "https://companieslogo.com/img/orig/HDB-bb6241fe.png?t=1633497370"
                            )
                        )
                    ),
                    LandingProductCategory(
                        title = "HDFC",
                        imageUrl = "https://companieslogo.com/img/orig/HDB-bb6241fe.png?t=1633497370",
                        type = LandingProductType.CARD,
                        products = listOf(
                            LandingProduct.Card(
                                id = "ABC",
                                last4Digits = "1234",
                                imageUrl = "https://companieslogo.com/img/orig/HDB-bb6241fe.png?t=1633497370"
                            )
                        )
                    ),


                )*/
            )
        }
    }

    val productCount: Int
        get() = if (products.isEmpty())
            0
        else
            products.map { it.products.size }
                .reduce { acc, current ->
                    acc + current
                }
}

@Keep
data class CreditScore(
    val score: Int,
    val provider: String,
    val leadingHtml: String?,
)
@Keep
data class LandingProductCategory(
    val title: String,
    val imageUrl: String,
    val type: LandingProductType,
    val products: List<LandingProduct>,
) {
    val hasSingleProduct: Boolean
        get() = products.size == 1
}
@Keep
enum class LandingProductType {
    CARD, LOAN
}

// TODO: complete the integration here
@Keep
sealed class LandingProduct {
    abstract val id: String

    @Keep
    data class Card(
        override val id: String,
        val last4Digits: String,
        val imageUrl: String,

        ) : LandingProduct()
    @Keep
    data class Loan(
        override val id: String,
        val number: String,
        val imageUrl: String,
    ) : LandingProduct()
}