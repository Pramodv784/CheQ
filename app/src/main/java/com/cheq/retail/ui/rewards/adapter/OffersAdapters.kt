package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheq.retail.databinding.OtherOffersItemLayoutBinding
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.main.model.TypesItem

class OffersAdapters(
    private val context: Context, private val offerList: ArrayList<TypesItem>,
    private val onTopFeatureDealClicked: TopFeaturedDealAdapter.OnFeatureDealClicked
) : RecyclerView.Adapter<OffersAdapters.OffersViewHolder>() {
    private var prefs: SharePrefs? = null
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OffersViewHolder {
        prefs = SharePrefs.getInstance(context)
        val inflater = LayoutInflater.from(context)
        val binding = OtherOffersItemLayoutBinding.inflate(inflater, parent, false)

        /* binding.root.layoutParams = ViewGroup.LayoutParams((parent.width * 0.39).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)*/

        return OffersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OffersViewHolder, position: Int) {
        val data = offerList[position]
        Glide.with(context).load("${prefs?.voucherBaseUrl}${data.otherOfferBgImage}" ).into(holder.binding.ivOfferImage)

        holder.binding.tvAmount.text = "₹${data.cheqBrandAccessoriesId?.get(0)?.denominationAmount}"
        holder.binding.tvDescription.text = data.cheqBrandAccessoriesId?.get(0)?.narrationOne

        holder.itemView.setOnClickListener {
            onTopFeatureDealClicked.onTopFeatureClicked(data)
        }
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    inner class OffersViewHolder(val binding: OtherOffersItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

}