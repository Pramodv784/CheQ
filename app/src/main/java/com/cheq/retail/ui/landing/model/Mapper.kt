package com.cheq.retail.ui.landing.model

val LandingEntity.asViewObject: LandingVO
    get() {
        val items = mutableListOf<LandingItemVO>()

        // header
        items.add(LandingItemVO.Header(name = name))

        // add the credit score
        creditScore?.let {
            items.add(LandingItemVO.CreditScoreItem(it))
        }

        // add the product along with subtitle if they exist
        if (productCount != 0) {
            items.add(LandingItemVO.Subtitle(productCount = productCount))
            items.addAll(products.map { LandingItemVO.Product(it) })
        }

        // no product
        // no credit score
        // -> provide add product option
        if (productCount == 0) {
            items.add(LandingItemVO.AddProduct)
        }

        return LandingVO(
            items = items,
            isDecoratedFooterVisible = productCount > 0
        )
    }