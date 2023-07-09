package com.cheq.retail.ui.landing.adapter.viewHolder

import com.cheq.retail.databinding.ItemLandingHeaderBinding
import com.cheq.retail.ui.landing.model.LandingItemVO

class LandingHeaderVH(private val binding: ItemLandingHeaderBinding) :
    LandingVH<LandingItemVO.Header>(binding.root) {

    override fun bind(item: LandingItemVO.Header) {
        binding.userNameTV.text = item.name
    }
}