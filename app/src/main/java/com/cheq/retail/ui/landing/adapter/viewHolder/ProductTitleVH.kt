package com.cheq.retail.ui.landing.adapter.viewHolder

import com.cheq.retail.R
import com.cheq.retail.databinding.ItemLandingSubtitleBinding
import com.cheq.retail.ui.landing.model.LandingItemVO

class ProductTitleVH(private val binding: ItemLandingSubtitleBinding) :
    LandingVH<LandingItemVO.Subtitle>(binding.root) {
    override fun bind(item: LandingItemVO.Subtitle) {
        binding.totalCardTV.text = itemView.context.resources.getQuantityString(
            R.plurals.identified_products_count,
            item.productCount,
            item.productCount
        )
    }
}