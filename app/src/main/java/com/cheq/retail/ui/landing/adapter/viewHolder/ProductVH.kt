package com.cheq.retail.ui.landing.adapter.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.R
import com.cheq.retail.databinding.ItemCardsBinding
import com.cheq.retail.ui.landing.model.LandingItemVO
import com.cheq.retail.ui.landing.model.LandingProductCategory
import com.cheq.retail.ui.landing.model.LandingProductType
import com.cheq.retail.utils.ImageUtils.bankLogo
import com.cheq.retail.utils.ImageUtils.loadSvg

class ProductVH(
    private val binding: ItemCardsBinding
) : LandingVH<LandingItemVO.Product>(binding.root) {
    private val bankLogo = binding.bankLogoIV
    private val bankName = binding.bankName
    private val cardType = binding.creditCardTV

    override fun bind(item: LandingItemVO.Product) {
        val data = item.product
        bankLogo.loadSvg(data.imageUrl)

        val extraProductCount = data.products.size - 1

        bankName.text =
            if (extraProductCount == 0) data.title else itemView.context.resources.getString(
                R.string.landing_card_title,
                data.title,
                extraProductCount
            )

        cardType.text = when (data.type) {
            LandingProductType.CARD -> "Credit Card"
            LandingProductType.LOAN -> "Loan"
        }
    }
}