package com.cheq.retail.ui.main.maininterface

import com.cheq.retail.ui.main.model.DenomsItem

interface RewardsInterface {
    fun onMyVoucherClicked(isClicked: Boolean, data: DenomsItem)
    fun onMyC2CClicked(isClicked: Boolean, amount: Float, convertToCashRate : Double)
    fun onMyVoucherClicked(isClicked: Boolean)
    fun onCategoriesClicked(catId: String, categoryPosition: Int)
    fun onCategoriesClicked(categoryPosition: Int)


}