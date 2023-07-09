package com.cheq.retail.ui.landing.adapter.viewHolder

import com.cheq.retail.databinding.ItemLandingAddProductBinding
import com.cheq.retail.ui.landing.model.LandingItemVO

class AddManuallyVH(
    binding: ItemLandingAddProductBinding,
    private val addProduct: () -> Unit
) : LandingVH<LandingItemVO.AddProduct>(binding.root) {

    init {
        binding.addProductBtn.setOnClickListener {
            addProduct()
        }
    }

    override fun bind(item: LandingItemVO.AddProduct) {

    }
}