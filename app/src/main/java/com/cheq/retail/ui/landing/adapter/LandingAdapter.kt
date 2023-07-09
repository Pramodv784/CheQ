package com.cheq.retail.ui.landing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.cheq.retail.R
import com.cheq.retail.databinding.*
import com.cheq.retail.ui.landing.adapter.viewHolder.*
import com.cheq.retail.ui.landing.model.ELandingViewItemType
import com.cheq.retail.ui.landing.model.LandingItemVO
import com.cheq.retail.ui.landing.model.LandingProductCategory

class LandingAdapter :
    ListAdapter<LandingItemVO, LandingVH<out LandingItemVO>>(LandingDiffCallback()) {

    interface ActionListener {
        fun addProduct()
    }

    var actionListener: ActionListener? = null

    override fun getItemViewType(position: Int): Int {
        return currentList[position].type.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LandingVH<out LandingItemVO> {

        val layoutInflater = LayoutInflater.from(parent.context);

        return when (ELandingViewItemType.values()[viewType]) {
            ELandingViewItemType.HEADER -> LandingHeaderVH(
                ItemLandingHeaderBinding.inflate(layoutInflater, parent, false)
            )
            ELandingViewItemType.ADD_MANUALLY
            -> AddManuallyVH(
                ItemLandingAddProductBinding.inflate(layoutInflater, parent, false),
                addProduct = {
                    actionListener?.addProduct()
                }
            )
            ELandingViewItemType.CREDIT_SCORE -> CreditScoreVH(
                ItemLandingCreditScoreBinding.inflate(layoutInflater, parent, false)
            )
            ELandingViewItemType.PRODUCT_TITLE -> ProductTitleVH(
                ItemLandingSubtitleBinding.inflate(layoutInflater, parent, false)
            )
            ELandingViewItemType.PRODUCT -> ProductVH(
                ItemCardsBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: LandingVH<out LandingItemVO>, position: Int) {

        when (val item = currentList[position]) {
            is LandingItemVO.AddProduct -> (holder as? AddManuallyVH)?.bind(item)
            is LandingItemVO.CreditScoreItem -> (holder as? CreditScoreVH)?.bind(item)
            is LandingItemVO.Header -> (holder as? LandingHeaderVH)?.bind(item)
            is LandingItemVO.Product -> (holder as? ProductVH)?.bind(item)
            is LandingItemVO.Subtitle -> (holder as? ProductTitleVH)?.bind(item)
        }


    }

    class LandingDiffCallback : DiffUtil.ItemCallback<LandingItemVO>() {
        override fun areItemsTheSame(
            oldItem: LandingItemVO,
            newItem: LandingItemVO
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: LandingItemVO,
            newItem: LandingItemVO
        ): Boolean {
            return oldItem == newItem
        }

    }
}