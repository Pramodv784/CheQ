package com.cheq.retail.ui.dashboard.view.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cheq.retail.R
import com.cheq.retail.databinding.SmallOfferWidgetItemLayoutBinding
import com.cheq.retail.ui.main.OffersItem


class OffersForYouWidgetsAdapter(
    private val context: Context,
    private val recyclerview: View? = null,
    private val homeSmallBanner: View? = null,
    private var offersItem: List<OffersItem?>?,
    var onItemClick: ((OffersItem?, Int) -> Unit)? = null
) : RecyclerView.Adapter<OffersForYouWidgetsAdapter.OffersForYouHolder>() {
    fun setOfferForYouList(localOfferForYou: List<OffersItem?>?) {
        offersItem = localOfferForYou
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersForYouHolder {
        val inflater = LayoutInflater.from(context)
        val binding = SmallOfferWidgetItemLayoutBinding.inflate(inflater, parent, false)
        return OffersForYouHolder(binding)
    }

    override fun getItemCount(): Int {
        return offersItem?.size ?: 0
    }

    override fun onBindViewHolder(holder: OffersForYouHolder, position: Int) {

        val data = offersItem?.get(position)

        if (data != null && data.visibility == true) {

            holder.binding.clMainLayout.visibility=View.VISIBLE


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
                        homeSmallBanner?.visibility=View.VISIBLE
                        return false;
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.demoView.visibility=View.VISIBLE
                        recyclerview?.visibility=View.VISIBLE
                        homeSmallBanner?.visibility=View.GONE
                        return false
                    }

                }).into(holder.binding.ivOfferImage)
            } else {

                holder.binding.animationView.setAnimationFromUrl(data.banner_asset)


            }

        } else {

            holder.binding.clMainLayout.visibility = View.GONE
            holder.binding.clMainLayout.layoutParams = LinearLayout.LayoutParams(0, 0)
        }
    }


    inner class OffersForYouHolder(val binding: SmallOfferWidgetItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(offersItem?.get(adapterPosition), adapterPosition)
            }
        }
    }

}