package com.cheq.retail.ui.landing.adapter.viewHolder

import androidx.core.view.isVisible
import com.cheq.retail.databinding.ItemLandingCreditScoreBinding
import com.cheq.retail.ui.landing.model.LandingItemVO
import com.cheq.retail.utils.Utils
import com.cheq.retail.utils.setAsHtml

class CreditScoreVH(
    private val binding: ItemLandingCreditScoreBinding
) : LandingVH<LandingItemVO.CreditScoreItem>(binding.root) {
    override fun bind(item: LandingItemVO.CreditScoreItem) {

        val topHtml = item.score.leadingHtml

        if (topHtml != null) {
            binding.congratsTV.setAsHtml(topHtml)
        }

        binding.congratsTV.isVisible = item.score.leadingHtml != null

        binding.scoreTV.text = item.score.score.toString()
        val creditHealth = Utils.getCreditHealthScore(item.score.score)
        binding.creditScoreText.apply {
            creditHealth.apply {
                text = first
                setTextColor(second)
            }
        }

    }
}