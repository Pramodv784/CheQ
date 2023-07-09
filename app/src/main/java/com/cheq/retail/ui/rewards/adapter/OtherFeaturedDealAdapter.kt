package com.cheq.retail.ui.rewards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cheq.retail.databinding.OtherFeatureDealItemLayoutBinding
import com.cheq.retail.databinding.TopFeatureDealItemLayoutBinding
import com.cheq.retail.extensions.voucherBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.ui.main.model.TypesItem

class OtherFeaturedDealAdapter(
    private val context: Context,
    private val feturedList: ArrayList<TypesItem>,
    private val onOtherFeatureDealClicked: OnOtherFeatureDealClicked
) : RecyclerView.Adapter<OtherFeaturedDealAdapter.TopFeaturedViewHolder>() {
    private var prefs: SharePrefs? = null
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TopFeaturedViewHolder {
        prefs = SharePrefs.getInstance(context)
        val inflater = LayoutInflater.from(context)
        val binding = OtherFeatureDealItemLayoutBinding.inflate(inflater, parent, false)

      /*  binding.root.layoutParams = ViewGroup.LayoutParams(
                 (parent.width * 0.39).toInt(),
                 ViewGroup.LayoutParams.MATCH_PARENT
         )*/
        return TopFeaturedViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TopFeaturedViewHolder, position: Int
    ) {
        val data = feturedList[position]
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(context).load("${prefs?.voucherBaseUrl}/${data.topDealBgImage}").apply(requestOptions)
            .into(holder.binding.ivMainImage)
        println("imageUrlAdapter ${data.image}")
        if (data.denominator!=null){
            holder.binding.llRewardConversion.visibility = View.VISIBLE
            holder.binding.tvVoucherCaption.text = "1 = ₹${data.denominator}"
        } else {
            holder.binding.llRewardConversion.visibility = View.INVISIBLE
        }

        if(data.cheqBrandAccessoriesId!=null && data.cheqBrandAccessoriesId.isNotEmpty()){
            holder.binding.tvVoucherType.text = data.cheqBrandAccessoriesId?.get(0)?.narrationOne
            holder.binding.tvVoucherAmount.text =
                "₹${data.cheqBrandAccessoriesId?.get(0)?.denominationAmount}"
        }




        holder.itemView.setOnClickListener {
          onOtherFeatureDealClicked.onOtherTopFeatureClicked(data)
        }
    }

    override fun getItemCount(): Int {
        return feturedList.size
    }


    inner class TopFeaturedViewHolder(val binding: OtherFeatureDealItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    interface OnOtherFeatureDealClicked {
        fun onOtherTopFeatureClicked(typesItem: TypesItem)
    }
}