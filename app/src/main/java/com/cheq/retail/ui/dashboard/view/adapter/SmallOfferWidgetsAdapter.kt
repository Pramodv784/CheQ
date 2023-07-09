package com.cheq.retail.ui.dashboard.view.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cheq.retail.R
import com.cheq.retail.databinding.SmallOfferWidgetItemLayoutBinding
import com.cheq.retail.ui.accountSettings.model.TxnListsItem
import com.cheq.retail.ui.main.OffersItem

class SmallOfferWidgetsAdapter(
    private val context: Context,
    private val recyclerview: View?,
    private val homeSmallBanner: View?,
    private var offersItem: List<OffersItem?>?,
    var onItemClick: ((OffersItem?, Int) -> Unit)? = null
) : RecyclerView.Adapter<SmallOfferWidgetsAdapter.SmallOfferViewHolder>() {

    fun setOfferForYouList(localOfferForYou: List<OffersItem?>?) {
        offersItem = localOfferForYou
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallOfferViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = SmallOfferWidgetItemLayoutBinding.inflate(inflater, parent, false)
        return SmallOfferViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return offersItem?.size ?: 0
    }

    override fun onBindViewHolder(holder: SmallOfferViewHolder, position: Int) {
        val data = offersItem?.get(position)
        val width: Int = Resources.getSystem().displayMetrics.widthPixels

        if (data != null && data.visibility == true) {
            holder.binding.clMainLayout.visibility = View.VISIBLE
            val layoutParams =
                holder.binding.demoView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = data.aspect_ratio
            holder.binding.demoView.layoutParams = layoutParams
            if (data.banner_type == 1) {
                Glide.with(context).load(data.banner_asset).listener(object :
                    RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        homeSmallBanner?.visibility = View.GONE
                        holder.binding.clMainLayout.visibility = View.GONE
                        homeSmallBanner?.visibility = View.GONE
                        return false;
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.demoView
                        homeSmallBanner?.visibility = View.GONE
                        recyclerview?.visibility = View.VISIBLE
                        return false
                    }

                }).into(holder.binding.ivOfferImage)

            } else {
                homeSmallBanner?.visibility = View.GONE
                holder.binding.animationView.setAnimationFromUrl(data.banner_asset)


            }


        } else {

            holder.binding.clMainLayout.visibility = View.GONE
            homeSmallBanner?.visibility = View.GONE
            holder.binding.clMainLayout.layoutParams = LinearLayout.LayoutParams(0, 0)
        }
    }

    inner class SmallOfferViewHolder(val binding: SmallOfferWidgetItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(offersItem?.get(adapterPosition), adapterPosition)
            }

        }
    }

}